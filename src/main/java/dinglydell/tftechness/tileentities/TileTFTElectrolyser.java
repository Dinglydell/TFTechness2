package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import blusunrize.immersiveengineering.common.IEContent;

import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.Core.Metal.MetalRegistry;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.Metal;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.TFC_ItemHeat;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.fluid.FluidTankMetal;
import dinglydell.tftechness.fluid.SolutionTank;
import dinglydell.tftechness.fluid.TFTFluids;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.item.TFTItemPropertyRegistry;
import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.metal.MetalStat;
import dinglydell.tftechness.multiblock.IMultiblockTFT;
import dinglydell.tftechness.multiblock.MultiblockElectrolyser;
import dinglydell.tftechness.recipe.SolutionRecipe;

public class TileTFTElectrolyser extends TileTFTMachineBase implements
		IFluidHandler {
	public enum Slots {
		ELECTRODE_A, ELECTRODE_B, INPUT, MOLD
	}

	//	protected static final int MR_ALUMINA = 102;
	/** Maximum capacity of molten redstone (B) */
	protected static final int MAX_REDSTONE_CAPACITY = 30;

	protected static final int MAX_ALUMINIUM_CAPACITY = 5;

	private static final float COOLING_COEF = 0.001f;
	//private static final float ITEM_HEAT_COEFFICIENT = 1;

	/** RF is multiplied by this before being used to heat up the machine */
	private static final int HEAT_COEFFICIENT = 50;

	/** internal temperature (degrees C) */
	protected float temperature = 20f;
	protected int oldTemp = 0;
	protected int targetTemperature = 1010;

	protected SolutionTank cryoliteTank = new SolutionTank(
			MAX_REDSTONE_CAPACITY * 1000);//, TFTFluids.moltenMetal.get("Redstone"));
	protected FluidTankMetal aluminiumTank = new FluidTankMetal(
			MAX_ALUMINIUM_CAPACITY * 1000,
			TFTFluids.moltenMetal.get("Aluminum"));

	//private static final int ELECTRODE_SLOT_A = 0;
	//private static final int ELECTRODE_SLOT_B = 1;
	//private static final int ALUMINA_SLOT = 2;
	//private static final int REDSTONE_SLOT = 3;
	//private static final int MOLD_SLOT = 4;

	public TileTFTElectrolyser() {
		inventory = new ItemStack[4];

		//thermalEnergy = (int) (TFC_Climate.getHeightAdjustedTemp(worldObj,
		//		xCoord,
		//		yCoord,
		//		zCoord) * getNetSHMass());
	}

	@Override
	public boolean openGui(World world, EntityPlayer player) {
		if (!hasMaster()) {
			return false;
		}
		player.openGui(TFTechness2.instance,
				TFTGuis.Electrolyser.ordinal(),
				world,
				getMasterX(),
				getMasterY(),
				getMasterZ());
		return true;

	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return "Electrolyser";
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {

		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return true;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (slot == Slots.ELECTRODE_A.ordinal()
				|| slot == Slots.ELECTRODE_B.ordinal()) {
			return item.getItem() == IEContent.itemGraphiteElectrode;
		}
		//if (slot == Slots.alumina.ordinal()) {
		//	return item.getItem() == TFTItems.alumina;
		//}
		if (slot == Slots.INPUT.ordinal()) {
			return TFTItemPropertyRegistry.isPowder(item);
		}
		if (slot == Slots.MOLD.ordinal()) {
			return item.getItem() == TFCItems.ceramicMold
					|| item.getItem() == TFTItems.unshaped.get("Aluminium");
		}
		return false;
	}

	@Override
	public IMultiblockTFT getMultiblock() {
		return MultiblockElectrolyser.instance;
	}

	public float getTargetTemperature() {
		return this.targetTemperature;
	}

	public void setTargetTemperature(int target) {
		this.targetTemperature = target;
		this.markDirty();
		if (worldObj.isRemote) {
			this.sendClientToServerMessage();
		}

	}

	public float getTemperature() {
		return this.temperature;
		//return this.temperature / getNetSHMass() - 273;
	}

	/** Returns average specific heat * mass */
	private float getNetSHMass() {
		//float volRed = getRedstoneAmt();
		//float volAir = MAX_REDSTONE_CAPACITY - volRed;
		//int amtAlumina = getAluminaAmt();
		//float massAir = volAir * DENSITY_AIR;
		//MetalStat red = TFTechness2.statMap.get("Redstone");
		//float massRed = volRed * red.density;
		//MetalStat alumina = TFTechness2.statMap.get("Alumina");
		//int massAlumina = amtAlumina * MR_ALUMINA / 1000;
		float volAluminium = getAluminiumAmt();

		//my eyes are burning
		MetalStat Al = TFTechness2.statMap.get("Aluminum");
		float massAluminium = volAluminium * Al.density;
		//return massAir * SH_AIR + massRed * red.getSISpecificHeat()
		//	+ massAlumina * alumina.getSISpecificHeat() + massAluminium
		//* Al.getSISpecificHeat();
		return cryoliteTank.getSHMass() + massAluminium
				* Al.getSISpecificHeat();
	}

	/** Unit: buckets (m^3) */
	private float getAluminiumAmt() {

		return aluminiumTank.getFluidAmount() / 1000;
	}

	///** Unit: moles */
	//private float getAluminaAmt() {
	//
	//	return cryoliteTank.getSolute(TFTItems.alumina);
	//}

	//private int getAluminaCapacity() {
	//return cryoliteTank.getFluidAmount() * TFTItemPropertyRegistry.getSolubilityIn(stack, TFTFluid);

	//}

	/** Unit: buckets (m^3) */
	private float getRedstoneAmt() {
		return cryoliteTank.getFluidAmount() / 1000;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection direction) {
		return 20000;
	}

	@Override
	protected int getMaxRfRate() {

		return 4096;
	}

	@Override
	protected int spendRf(int amt) {
		float SHMass = getNetSHMass();
		if (this.temperature >= this.targetTemperature) {
			return 0;
		}
		float dT = HEAT_COEFFICIENT * amt / SHMass;
		float oldTemp = temperature;
		this.temperature = Math.min(this.temperature + dT,
				this.targetTemperature);
		dT = this.temperature - oldTemp;
		//float energy = oldThermalEnergy;
		//energy += Math.min(HEAT_COEFFICIENT * amt, Math.max(0,
		//	(targetTemperature + 273) *SHMass - energy));
		//this.temperature = (energy / SHMass) - 273;

		return (int) (dT * SHMass / HEAT_COEFFICIENT);
	}

	@Override
	protected void writeToMasterNBT(NBTTagCompound data) {
		super.writeToMasterNBT(data);
		data.setFloat("Temperature", temperature);
		data.setInteger("TargetTemperature", targetTemperature);
		data.setTag("CryoliteTank", cryoliteTank.writeToNBT());
		data.setTag("AluminiumTank", aluminiumTank.writeToNBT());
	}

	@Override
	protected void readFromMasterNBT(NBTTagCompound data) {
		super.readFromMasterNBT(data);
		this.temperature = data.getFloat("Temperature");
		this.targetTemperature = data.getInteger("TargetTemperature");
		this.cryoliteTank.readFromNBT(data.getCompoundTag("CryoliteTank"));
		this.aluminiumTank.readFromNBT(data.getCompoundTag("AluminiumTank"));
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote && isMaster()) {
			float externalTemp = TFC_Climate.getHeightAdjustedTemp(worldObj,
					xCoord,
					yCoord,
					zCoord);
			this.temperature -= (this.getTemperature() - externalTemp)
					* COOLING_COEF;
			ItemStack red = inventory[Slots.INPUT.ordinal()];
			if (red != null) {
				red.stackSize -= cryoliteTank.fill(red, true);
				if (red.stackSize == 0) {
					inventory[Slots.INPUT.ordinal()] = null;
				}
			}
			//heatSlot(Slots.redstone.ordinal());
			//if (cryoliteTank.getFluidAmount() != 0
			//		&& inventory[Slots.alumina.ordinal()] != null
			//		&& inventory[Slots.alumina.ordinal()].getItem() == TFTItems.alumina) {
			//	if (molAlumina + MOLES_ALUMINA <= getAluminaCapacity()) {
			//		molAlumina += MOLES_ALUMINA;
			//		inventory[Slots.alumina.ordinal()].stackSize--;
			//		if (inventory[Slots.alumina.ordinal()].stackSize == 0) {
			//			inventory[Slots.alumina.ordinal()] = null;
			//		}
			//	}
			//}
			if (inventory[Slots.ELECTRODE_A.ordinal()] != null
					&& inventory[Slots.ELECTRODE_B.ordinal()] != null
					&& inventory[Slots.ELECTRODE_A.ordinal()]
							.isItemEqual(inventory[Slots.ELECTRODE_B.ordinal()])
					&& inventory[Slots.ELECTRODE_A.ordinal()].getItem() == IEContent.itemGraphiteElectrode) {
				cryoliteTank.setCondition(SolutionRecipe.electrodes);
			} else {
				cryoliteTank.setCondition(null);
			}
			cryoliteTank.updateTank(getTemperature());
			//proccessReaction();
			FluidStack cryoDrain = cryoliteTank
					.drain(new FluidStack(aluminiumTank.getLockedFluid(),
							cryoliteTank.getFluidAmount(aluminiumTank
									.getLockedFluid()) / 10),
							false);
			int al = aluminiumTank.fill(cryoDrain, false);
			if (cryoDrain != null && al == cryoDrain.amount) {
				aluminiumTank.fill(cryoliteTank.drain(new FluidStack(
						aluminiumTank.getLockedFluid(),
						cryoliteTank.getFluidAmount(aluminiumTank
								.getLockedFluid()) / 10),
						true), true);
			}
			updateFluidTemperature(aluminiumTank);
			handleMoldOutput(Slots.MOLD.ordinal());
			//heatSlot(Slots.alumina.ordinal());
		}
		super.updateEntity();

	}

	private void handleMoldOutput(int slot) {
		ItemStack mold = inventory[slot];
		if (mold == null || aluminiumTank.getFluidAmount() == 0) {
			return;
		}
		FluidMoltenMetal f = aluminiumTank.getLockedFluid();
		Metal m = MetalRegistry.instance.getMetalFromString(f.getMetalName());
		HeatIndex hi = HeatRegistry.getInstance()
				.findMatchingIndex(new ItemStack(m.ingot));
		float temperature = f.getTemperature(aluminiumTank.getFluid());
		if (hi.meltTemp >= temperature) {
			return;
		}
		int drainAmt = 1;
		if (mold.getItem() == TFCItems.ceramicMold) {
			inventory[slot] = new ItemStack(m.meltedItem, 1, 99);
			TFC_ItemHeat.setTemp(inventory[slot], temperature);
			aluminiumTank.drain(1, true);
		} else if (mold.getItem() == m.meltedItem && mold.getItemDamage() > 0) {
			float temp = TFC_ItemHeat.getTemp(mold);
			if (temp >= hi.meltTemp) {
				// fraction of the mold this drop will make up - used to calc new temperature
				float dropFrac = 1 / (100 - mold.getItemDamage());
				float newTemp = temp * (1 - dropFrac) + temperature * dropFrac;
				mold.setItemDamage(mold.getItemDamage() - 1);
				TFC_ItemHeat.setTemp(inventory[slot], newTemp);
				aluminiumTank.drain(1, true);
			}
		}

	}

	private void updateFluidTemperature(FluidTankMetal tank) {
		if (tank.getFluidAmount() != 0) {
			((FluidMoltenMetal) tank.getFluid().getFluid())
					.setTemperature(getTemperature(), tank.getFluid());
		}

	}

	//private void heatSlot(int slot) {
	//	if (inventory[slot] == null) {
	//		return;
	//	}
	//	HeatIndex hi = HeatRegistry.getInstance()
	//			.findMatchingIndex(inventory[slot]);
	//	int mass = 100;
	//	//TODO: make this less of a hack
	//	if (slot == Slots.alumina.ordinal()) {
	//		mass = TFTechness2.statMap.get("Alumina").density
	//				/ TFTechness2.ingotsPerBlock;
	//	} else if (slot == Slots.redstone.ordinal()) {
	//		mass = TFTechness2.statMap.get("Redstone").density
	//				/ TFTechness2.ingotsPerBlock;
	//	}
	//	float slotTemp = getSlotTemperature(slot);
	//
	//	float temp = getTemperature();
	//	if (slotTemp > hi.meltTemp) {
	//		//TODO: Make this less of a hack
	//		if (OreDict.itemMatches(inventory[slot], "dustRedstone")) {
	//			cryoliteTank.fill(TFTFluids.moltenMetal.get("Redstone")
	//					.createStack(1000 / TFTechness2.ingotsPerBlock
	//							* inventory[slot].stackSize,
	//							temp),
	//					true);
	//		}
	//		inventory[slot] = null;
	//		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	//		return;
	//	}
	//	if (slotTemp == temp) {
	//		return;
	//	}
	//	int dH = -(int) ((slotTemp - temp) * ITEM_HEAT_COEFFICIENT);
	//	float newSlotTemp;
	//	float SHM = hi.specificHeat * 1000 * mass;
	//	if (slotTemp > temp) {
	//		newSlotTemp = Math.max(temp, slotTemp + dH * SHM);
	//	} else {
	//		newSlotTemp = Math.min(temp, slotTemp + dH * SHM);
	//	}
	//	dH = (int) ((newSlotTemp - slotTemp) / SHM);
	//	thermalEnergy -= dH;
	//	TFC_ItemHeat.setTemp(inventory[slot], newSlotTemp);
	//
	//}

	private float getSlotTemperature(int slot) {
		float SH;
		int mass;
		return TFC_ItemHeat.getTemp(inventory[slot]);

	}

	@Override
	public void readClientToServerMessage(NBTTagCompound nbt) {
		targetTemperature = nbt.getInteger("TargetTemperature");

	}

	@Override
	public void writeClientToServerMessage(NBTTagCompound nbt) {
		nbt.setInteger("TargetTemperature", targetTemperature);

	}

	@Override
	public void writeServerToClientMessage(NBTTagCompound nbt) {

		super.writeServerToClientMessage(nbt);
		nbt.setFloat("Temperature", temperature);
		nbt.setTag("CryoliteTank", cryoliteTank.writeToNBT());
		nbt.setTag("AluminiumTank", aluminiumTank.writeToNBT());
	}

	@Override
	public void readServerToClientMessage(NBTTagCompound nbt) {
		super.readServerToClientMessage(nbt);
		temperature = nbt.getFloat("Temperature");
		cryoliteTank.readFromNBT(nbt.getCompoundTag("CryoliteTank"));
		aluminiumTank.readFromNBT(nbt.getCompoundTag("AluminiumTank"));
	}

	@Override
	public int fill(ForgeDirection from, FluidStack stack, boolean doFill) {
		return cryoliteTank.fill(stack, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack stack,
			boolean doDrain) {
		return drain(from, stack.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return aluminiumTank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return cryoliteTank.fill(new FluidStack(fluid, 1), false) != 0;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return aluminiumTank.getLockedFluid() == fluid
				&& aluminiumTank.getFluidAmount() > 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { aluminiumTank.getInfo() };
	}

	public SolutionTank getCryoliteTank() {
		return cryoliteTank;
	}

	public FluidTankMetal getAluminiumTank() {
		return aluminiumTank;
	}

}
