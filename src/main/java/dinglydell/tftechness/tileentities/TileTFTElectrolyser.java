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
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.TFC_ItemHeat;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.fluid.FluidTankMetal;
import dinglydell.tftechness.fluid.SolutionTank;
import dinglydell.tftechness.fluid.TFTFluids;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.metal.MetalStat;
import dinglydell.tftechness.multiblock.IMultiblockTFT;
import dinglydell.tftechness.multiblock.MultiblockElectrolyser;
import dinglydell.tftechness.util.OreDict;

public class TileTFTElectrolyser extends TileTFTMachineBase implements
		IFluidHandler {
	public enum Slots {
		electrodeA, electrodeB, alumina, redstone, mold
	}

	protected static final int MR_ALUMINA = 102;
	/** Maximum capacity of molten redstone (B) */
	protected static final int MAX_REDSTONE_CAPACITY = 30;
	private static final int SH_REDSTONE = 1136;
	private static final int SH_ALUMINA = 451;
	private static final float COOLING_COEF = 1;
	private static final float ITEM_HEAT_COEFFICIENT = 1;
	/** number of moles of alumina in 1 item */
	private static final int MOLES_ALUMINA = 250;
	/** number of moles of alumina that can dissolve in 1 bucket of redstone */
	private static final int MOLES_ALUMINA_PER_B_REDSTONE = 250;
	/** moles alumina -> mb aluminium */
	private static final int ALUMINA_TO_ALUMINIUM = 20;

	protected int thermalEnergy = 14365440;
	protected int oldTemp = 0;
	protected int targetTemperature;

	protected SolutionTank cryoliteTank = new SolutionTank(
			MAX_REDSTONE_CAPACITY * 1000);//, TFTFluids.moltenMetal.get("Redstone"));
	protected FluidTankMetal aluminiumTank = new FluidTankMetal(
			MAX_REDSTONE_CAPACITY * 1000, TFTFluids.moltenMetal.get("Aluminum"));

	protected int molAlumina = 0;

	//private static final int ELECTRODE_SLOT_A = 0;
	//private static final int ELECTRODE_SLOT_B = 1;
	//private static final int ALUMINA_SLOT = 2;
	//private static final int REDSTONE_SLOT = 3;
	//private static final int MOLD_SLOT = 4;

	public TileTFTElectrolyser() {
		inventory = new ItemStack[5];
		//thermalEnergy = (int) (TFC_Climate.getHeightAdjustedTemp(worldObj,
		//		xCoord,
		//		yCoord,
		//		zCoord) * getNetSHMass());
	}

	@Override
	public boolean openGui(World world, EntityPlayer player) {
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
		if (slot == Slots.electrodeA.ordinal()
				|| slot == Slots.electrodeB.ordinal()) {
			return item.getItem() == IEContent.itemGraphiteElectrode;
		}
		if (slot == Slots.alumina.ordinal()) {
			return item.getItem() == TFTItems.alumina;
		}
		if (slot == Slots.redstone.ordinal()) {
			return OreDict.itemMatches(item, "dustRedstone");
		}
		if (slot == Slots.mold.ordinal()) {
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
		return this.thermalEnergy / getNetSHMass() - 273;
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

	/** Unit: moles */
	private int getAluminaAmt() {

		return molAlumina;
	}

	private int getAluminaCapacity() {
		return (int) (cryoliteTank.getFluidAmount() / 1000f * MOLES_ALUMINA_PER_B_REDSTONE);

	}

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
		int oldThermalEnergy = this.thermalEnergy;
		this.thermalEnergy += Math.min(amt,
				Math.max(0, (targetTemperature + 273) * getNetSHMass()
						- this.thermalEnergy));

		return this.thermalEnergy - oldThermalEnergy;
	}

	@Override
	protected void writeToMasterNBT(NBTTagCompound data) {
		super.writeToMasterNBT(data);
		data.setInteger("ThermalEnergy", thermalEnergy);
		data.setInteger("TargetTemperature", targetTemperature);
		data.setTag("CryoliteTank", cryoliteTank.writeToNBT());
		data.setTag("AluminiumTank", aluminiumTank.writeToNBT());
	}

	@Override
	protected void readFromMasterNBT(NBTTagCompound data) {
		super.readFromMasterNBT(data);
		this.thermalEnergy = data.getInteger("ThermalEnergy");
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
			this.thermalEnergy -= (this.getTemperature() - externalTemp)
					* COOLING_COEF;
			ItemStack red = inventory[Slots.redstone.ordinal()];
			if (red != null) {
				red.stackSize -= cryoliteTank.fill(red, true);
				if (red.stackSize == 0) {
					inventory[Slots.redstone.ordinal()] = null;
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

			cryoliteTank.updateTank(getTemperature());
			//proccessReaction();
			updateFluidTemperature(aluminiumTank);
			//heatSlot(Slots.alumina.ordinal());
		}
		super.updateEntity();

	}

	private void proccessReaction() {
		if (cryoliteTank.getSolute(TFTItems.alumina) > 0) {

			int amt = aluminiumTank
					.fill(aluminiumTank
							.getLockedFluid()
							.createStack(ALUMINA_TO_ALUMINIUM, getTemperature()),
							false);
			if (amt == ALUMINA_TO_ALUMINIUM) {

				aluminiumTank.fill(aluminiumTank.getLockedFluid()
						.createStack(ALUMINA_TO_ALUMINIUM, getTemperature()),
						true);
			}
		}

	}

	private void updateFluidTemperature(FluidTankMetal tank) {
		if (tank.getFluidAmount() != 0) {
			((FluidMoltenMetal) tank.getFluid().getFluid())
					.setTemperature(getTemperature(), tank.getFluid());
		}

	}

	private void heatSlot(int slot) {
		if (inventory[slot] == null) {
			return;
		}
		HeatIndex hi = HeatRegistry.getInstance()
				.findMatchingIndex(inventory[slot]);
		int mass = 100;
		//TODO: make this less of a hack
		if (slot == Slots.alumina.ordinal()) {
			mass = TFTechness2.statMap.get("Alumina").density
					/ TFTechness2.ingotsPerBlock;
		} else if (slot == Slots.redstone.ordinal()) {
			mass = TFTechness2.statMap.get("Redstone").density
					/ TFTechness2.ingotsPerBlock;
		}
		float slotTemp = getSlotTemperature(slot);

		float temp = getTemperature();
		if (slotTemp > hi.meltTemp) {
			//TODO: Make this less of a hack
			if (OreDict.itemMatches(inventory[slot], "dustRedstone")) {
				cryoliteTank.fill(TFTFluids.moltenMetal.get("Redstone")
						.createStack(1000 / TFTechness2.ingotsPerBlock
								* inventory[slot].stackSize,
								temp),
						true);
			}
			inventory[slot] = null;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return;
		}
		if (slotTemp == temp) {
			return;
		}
		int dH = -(int) ((slotTemp - temp) * ITEM_HEAT_COEFFICIENT);
		float newSlotTemp;
		float SHM = hi.specificHeat * 1000 * mass;
		if (slotTemp > temp) {
			newSlotTemp = Math.max(temp, slotTemp + dH * SHM);
		} else {
			newSlotTemp = Math.min(temp, slotTemp + dH * SHM);
		}
		dH = (int) ((newSlotTemp - slotTemp) / SHM);
		thermalEnergy -= dH;
		TFC_ItemHeat.setTemp(inventory[slot], newSlotTemp);

	}

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
		nbt.setInteger("ThermalEnergy", thermalEnergy);
	}

	@Override
	public void readServerToClientMessage(NBTTagCompound nbt) {
		super.readServerToClientMessage(nbt);
		thermalEnergy = nbt.getInteger("ThermalEnergy");
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
