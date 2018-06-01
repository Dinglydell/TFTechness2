package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.fluid.SolutionTank;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.recipe.SolutionRecipe;

public class TileMachineComponentTank extends TileMachineInventory implements
		IFluidHandler {
	protected SolutionTank tank = new SolutionTank(1000);
	private ItemStack stack;

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);

		data.setTag("Tank", tank.writeToNBT());
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		tank.readFromNBT(data.getCompoundTag("Tank"));
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			return;
		}

		if (stack != null) {
			stack.stackSize -= tank.fill(stack, true);
			if (stack.stackSize == 0) {
				stack = null;
			}
		}
		tank.updateTank(temperature);
		tank.removeCondition(SolutionRecipe.electrodes);
		for (ForgeDirection dir : ForgeDirection.values()) {
			if (dir == ForgeDirection.UNKNOWN) {
				continue;
			}
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			TileEntity tile = worldObj.getTileEntity(x, y, z);
			if (tile != null) {
				if (tile instanceof TileMachineComponentTank) {

					TileMachineComponentTank tileTank = (TileMachineComponentTank) tile;
					tank.equalise(tileTank.tank);
					//if (tank.hasCondition(SolutionRecipe.electrodes)) {// share conditions
					//	tileTank.tank.addCondition(SolutionRecipe.electrodes);
					//} else if (tileTank.tank
					//		.hasCondition(SolutionRecipe.electrodes)) {
					//	tank.addCondition(SolutionRecipe.electrodes);
					//}

				} else if (tile instanceof TileMachineElectrode) {
					TileMachineElectrode tileElectrode = (TileMachineElectrode) tile;

					if (tileElectrode.getEnergyConsumptionRate() > 0
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
							for (FluidStack f : tank.getFluids()) {
								if (f.getFluid() instanceof FluidMoltenMetal) {
									FluidMoltenMetal fm = (FluidMoltenMetal) f
											.getFluid();
									Metal m = MetalRegistry.instance
											.getMetalFromString(fm
													.getMetalName());
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
										tank.drain(fm.createStack(1,
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
										tank.drain(fm.createStack(1,
												temperature), true);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void writeServerToClientMessage(NBTTagCompound nbt) {
		super.writeServerToClientMessage(nbt);

		nbt.setTag("Tank", tank.writeToNBT());
	}

	@Override
	public void readServerToClientMessage(NBTTagCompound nbt) {

		super.readServerToClientMessage(nbt);
		tank.readFromNBT(nbt.getCompoundTag("Tank"));
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
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

	public boolean openGui(World world, EntityPlayer player) {
		player.openGui(TFTechness2.instance,
				TFTGuis.Tank.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		return true;
	}
}
