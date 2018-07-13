package dinglydell.tftechness.tileentities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.bioxx.tfc.Core.Metal.MetalRegistry;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.Metal;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.TFC_ItemHeat;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.component.property.ComponentProperty;
import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.fluid.FluidStackFloat;
import dinglydell.tftechness.fluid.ITESolutionTank;
import dinglydell.tftechness.fluid.SolutionTank;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.recipe.SolutionRecipe;
import dinglydell.tftechness.world.TFTWorldData;

public class TileMachineComponentTank extends TileMachineInventory implements
		IFluidHandler, ITESolutionTank {
	protected SolutionTank tank;
	private ItemStack stack;
	protected boolean isSealed;
	private boolean didExplode;
	private Map<ForgeDirection, Integer> soundCooldown = new HashMap<ForgeDirection, Integer>();
	protected Map<ForgeDirection, Double> dP = new HashMap<ForgeDirection, Double>();

	public TileMachineComponentTank() {
		super();
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			soundCooldown.put(dir, 0);
		}
	}

	protected int getCapacity() {
		return 1000;
	}

	@Override
	public void initialiseProperties() {

		super.initialiseProperties();
		tank = new SolutionTank(this, getCapacity());
		tank.setGasContent(TFTWorldData.get(worldObj)
				.getAtmosphericComposition(xCoord, yCoord, zCoord));
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		if (tank != null) {
			data.setTag("Tank", tank.writeToNBT());
		}
		data.setBoolean("Sealed", isSealed);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		tank = SolutionTank.readFromNBT(this, data.getCompoundTag("Tank"));
		isSealed = data.getBoolean("Sealed");
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			Random rand = new Random();
			for (Entry<ForgeDirection, Double> entry : dP.entrySet()) {
				if (worldObj.getBlock(xCoord + entry.getKey().offsetX,
						yCoord + entry.getKey().offsetY,
						zCoord + entry.getKey().offsetZ).isAir(worldObj,
						xCoord + entry.getKey().offsetX,
						yCoord + entry.getKey().offsetY,
						zCoord + entry.getKey().offsetZ)
						&& entry.getValue() > 25) {
					double rootdP = Math.sqrt(entry.getValue());
					if (rand.nextDouble() > 5 / rootdP) {
						ForgeDirection dir = entry.getKey();
						double speed = rootdP / 100;
						worldObj.spawnParticle("smoke",
								xCoord + 0.5 + dir.offsetX * 0.5,
								yCoord + 0.5 + dir.offsetY * 0.5,
								zCoord + 0.5 + dir.offsetZ * 0.5,
								dir.offsetX * speed + 0.1 * (1 - dir.offsetX)
										* (rand.nextDouble() - 0.5),
								dir.offsetY * speed + 0.1 * (1 - dir.offsetY)
										* (rand.nextDouble() - 0.5),
								dir.offsetZ * speed + 0.1 * (1 - dir.offsetZ)
										* (rand.nextDouble() - 0.5));
					}
				}
			}
			return;
		}

		if (stack != null) {
			stack = tank.fill(stack, true);
			if (stack != null && stack.stackSize == 0) {
				stack = null;
			}
		}
		temperature = tank.updateTank(temperature);
		tank.removeCondition(SolutionRecipe.electrodes);
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			if (!isSealed
					&& worldObj.getBlock(x, y, z).isAir(worldObj, x, y, z)) {
				double p = tank.getTotalPressure();
				tank.equaliseGas(0.01f + 0.005f * dir.offsetY, 1, TFTWorldData
						.get(worldObj).getAtmosphericComposition(x, y, z));
				dP.put(dir, p - tank.getTotalPressure());

				if (dP.get(dir) > 20) {

					soundCooldown.put(dir, soundCooldown.get(dir) - 1);
					if (soundCooldown.get(dir) <= 0) {

						soundCooldown.put(dir, 6);
						//TFTechness2.logger.info(dP / 1000);
						worldObj.playSoundEffect(x,
								y,
								z,
								TFTechness2.MODID + ":machine.tank.hiss",
								(float) (Math.sqrt(dP.get(dir)) / 100),
								1f);
					}
				}

			} else {
				soundCooldown.put(dir, 0);
				dP.put(dir, 0d);
			}
			TileEntity tile = worldObj.getTileEntity(x, y, z);
			if (tile != null) {
				if (tile instanceof TileMachineComponentTank) {

					TileMachineComponentTank tileTank = (TileMachineComponentTank) tile;
					double p = tank.getTotalPressure();
					tank.equalise(tileTank.tank, dir);
					dP.put(dir, p - tank.getTotalPressure());
					//if (tank.hasCondition(SolutionRecipe.electrodes)) {// share conditions
					//	tileTank.tank.addCondition(SolutionRecipe.electrodes);
					//} else if (tileTank.tank
					//		.hasCondition(SolutionRecipe.electrodes)) {
					//	tank.addCondition(SolutionRecipe.electrodes);
					//}

				} else if (tile instanceof TileMachineElectrode) {
					TileMachineElectrode tileElectrode = (TileMachineElectrode) tile;

					if (tileElectrode.getEnergyRate() > 0
							&& tileElectrode.hasElectrodes()) {
						tank.addCondition(SolutionRecipe.electrodes);
					}
				} else if (tile instanceof TileMachineComponentItemShelf) {
					TileMachineComponentItemShelf tileShelf = (TileMachineComponentItemShelf) tile;
					if (tank.containsAnyFluid()) { //try and drain into molds
						//TODO: optimise this - maybe the shelf should keep track of molds to save iterating the shelf each time
						// maybe the tank should keep track of neighbouring shelves, or even neighbouring molds
						for (int i = 0; i < tileShelf.inventory.length; i++) {
							ItemStack stack = tileShelf.getStackInSlot(i);
							if (stack == null) {
								continue;
							}
							for (FluidStackFloat f : tank.getFluids()) {
								if (f.amount == 0) {
									continue;
								}
								if (f.getFluid() instanceof FluidMoltenMetal) {
									FluidMoltenMetal fm = (FluidMoltenMetal) f
											.getFluid();
									Metal m = MetalRegistry.instance
											.getMetalFromString(fm
													.getMetalName());
									if (m == null) {
										continue;
									}
									HeatIndex hi = HeatRegistry.getInstance()
											.findMatchingIndex(new ItemStack(
													m.ingot));
									if (hi.meltTemp > temperature) {
										continue;
									}
									if (stack.getItem() == TFCItems.ceramicMold) {
										ItemStack mold = new ItemStack(
												m.meltedItem, 1, 99);
										tileShelf.setInventorySlotContents(i,
												mold);
										TFC_ItemHeat.setTemp(mold, temperature);
										tank.drain(new FluidStackFloat(fm, 1,
												temperature), true);
									} else if (stack.getItem() == m.meltedItem
											&& stack.getItemDamage() > 0) {

										float temp = TFC_ItemHeat
												.getTemp(stack);
										if (temp < hi.meltTemp) {
											continue;
										}
										float dropFrac = 1 / (100 - stack
												.getItemDamage());
										float newTemp = temp * (1 - dropFrac)
												+ temperature * dropFrac;
										stack.setItemDamage(stack
												.getItemDamage() - 1);
										TFC_ItemHeat.setTemp(stack, newTemp);
										tank.drain(new FluidStackFloat(fm, 1,
												temperature), true);
									}
								}
							}
						}
					}
				}
			}
		}
		double atmosPressure = TFTWorldData.get(worldObj)
				.getAtmosphericPressure(yCoord);
		double pressure = tank.getTotalPressure();

		double pressureDiff = Math.abs(pressure - atmosPressure);
		if (pressureDiff > getMaxPressure()) {
			//tank.equaliseGas(10f, 1f, new HashMap<Gas, GasStack>());
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			//float strength = (float) (Math.sqrt(pressureDiff) * 0.0025);
			//TFTechness2.logger.info("Pressure difference: " + pressureDiff);
			//TFTechness2.logger.info("BANG! " + strength + "(x: " + xCoord
			//		+ ", y: " + yCoord + ", z: " + zCoord + ")");
			//
			//worldObj.createExplosion(null,
			//		xCoord,
			//		yCoord,
			//		zCoord,
			//		Math.max(2, strength),
			//		true);

		}
	}

	@Override
	public float getSpecificHeat() {

		return super.getSpecificHeat() + (tank == null ? 0 : tank.getSHMass());
	}

	@Override
	public double getMaxPressure() {
		if (!materials.containsKey(ComponentProperty.MAXIMUM_PRESSURE)) {
			return 1e6f;
		}
		return (Float) materials.get(ComponentProperty.MAXIMUM_PRESSURE).validFor
				.get(ComponentProperty.MAXIMUM_PRESSURE);
	}

	@Override
	public void writeServerToClientMessage(NBTTagCompound nbt) {
		super.writeServerToClientMessage(nbt);
		if (tank != null) {
			nbt.setTag("Tank", tank.writeToNBT());
		}
		nbt.setBoolean("isSealed", isSealed);
		NBTTagList dPTag = new NBTTagList();
		for (Entry<ForgeDirection, Double> entry : dP.entrySet()) {
			NBTTagCompound entryTag = new NBTTagCompound();
			entryTag.setInteger("dir", entry.getKey().ordinal());
			entryTag.setDouble("dP", entry.getValue());
			dPTag.appendTag(entryTag);
		}
		nbt.setTag("dP", dPTag);

	}

	@Override
	public void readServerToClientMessage(NBTTagCompound nbt) {

		super.readServerToClientMessage(nbt);
		if (tank == null) {
			tank = SolutionTank.readFromNBT(this, nbt.getCompoundTag("Tank"));
		} else {
			tank.readFromNBT(nbt.getCompoundTag("Tank"));
		}
		isSealed = nbt.getBoolean("isSealed");
		NBTTagList dpList = nbt.getTagList("dP", 10);
		for (int i = 0; i < dpList.tagCount(); i++) {
			NBTTagCompound tag = dpList.getCompoundTagAt(i);
			ForgeDirection dir = ForgeDirection.values()[tag.getInteger("dir")];
			dP.put(dir, tag.getDouble("dP"));
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, from, doFill);
	}

	public float fill(ForgeDirection from, FluidStackFloat resource,
			boolean doFill) {
		return tank.fill(resource, from, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return stack;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack st = stack;
		stack = null;
		return st;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack st) {
		stack = st;

	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {

		return true;
	}

	public SolutionTank getTank() {
		return tank;
	}

	public boolean onRightClick(World world, EntityPlayer player) {
		player.openGui(TFTechness2.instance,
				TFTGuis.Tank.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		return true;
	}

	@Override
	public float attemptOverflow(float overVol, Set<ITESolutionTank> from,
			boolean doOverflow) {

		float transfered = 0;
		//try down frist
		transfered += attemptOverflowInDirection(ForgeDirection.DOWN,
				overVol,
				from,
				doOverflow);

		if (transfered >= overVol) {
			return transfered;
		}
		//try NESW
		//average to go to each direction
		float amt = (overVol - transfered) / 4;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (dir.offsetY != 0) {
				continue;
			}
			transfered += attemptOverflowInDirection(dir, amt, from, doOverflow);
		}
		if (transfered >= overVol) {
			return transfered;
		}

		//try UP
		transfered += attemptOverflowInDirection(ForgeDirection.UP,
				overVol,
				from,
				doOverflow);

		return transfered;
	}

	private float attemptOverflowInDirection(ForgeDirection to, float overVol,
			Set<ITESolutionTank> from, boolean doOverflow) {

		TileEntity tile = worldObj.getTileEntity(xCoord + to.offsetX, yCoord
				+ to.offsetY, zCoord + to.offsetZ);

		if (tile instanceof TileMachineComponentTank) {
			if (!from.add((ITESolutionTank) tile)) {
				return 0;
			}
			TileMachineComponentTank tt = (TileMachineComponentTank) tile;
			return tank.transferFluidTo(tt.tank,
					overVol,
					ForgeDirection.DOWN,
					doOverflow,
					from);

		}
		return 0;

	}

	@Override
	public double getAtmosphericPressure() {

		return TFTWorldData.get(worldObj).getAtmosphericPressure(yCoord);
	}

	public void toggleSeal() {
		this.isSealed = !this.isSealed;

		this.sendClientToServerMessage();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

	}

	public boolean isSealed() {

		return isSealed;
	}

	@Override
	public void writeClientToServerMessage(NBTTagCompound nbt) {

		super.writeClientToServerMessage(nbt);

		nbt.setBoolean("isSealed", isSealed);
	}

	@Override
	public void readClientToServerMessage(NBTTagCompound nbt) {
		super.readClientToServerMessage(nbt);
		isSealed = nbt.getBoolean("isSealed");
	}

	@Override
	public IIcon getIcon(int side) {

		return component.getIcon(isSealed ? 0 : 1);
	}

	@Override
	public boolean onDestroy() {

		boolean drop = super.onDestroy();
		if (didExplode) {
			return false;
		}
		if (!didExplode) {
			didExplode = true;
			double atmosPressure = TFTWorldData.get(worldObj)
					.getAtmosphericPressure(yCoord);
			double pressure = tank.getTotalPressure();
			double pressureDiff = Math.abs(pressure - atmosPressure);

			float strength = (float) (Math.sqrt(pressureDiff) * 0.0025);
			TFTechness2.logger.info("Pressure difference: " + pressureDiff);
			TFTechness2.logger.info("BANG! " + strength + "(x: " + xCoord
					+ ", y: " + yCoord + ", z: " + zCoord + ")");
			if (strength > 0.15) {
				worldObj.createExplosion(null,
						xCoord,
						yCoord,
						zCoord,
						Math.min(strength, 50),
						true);
			}
			return drop && strength < 2;
		}
		return drop;
	}

	@Override
	public void randomDisplayTick(Random rand) {
		super.randomDisplayTick(rand);

	}
}
