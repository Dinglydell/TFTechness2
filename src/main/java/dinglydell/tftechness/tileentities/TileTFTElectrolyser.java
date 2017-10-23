package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import blusunrize.immersiveengineering.common.IEContent;

import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.api.TFCItems;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.metal.MetalStat;
import dinglydell.tftechness.multiblock.IMultiblockTFT;
import dinglydell.tftechness.multiblock.MultiblockElectrolyser;
import dinglydell.tftechness.util.OreDict;

public class TileTFTElectrolyser extends TileTFTMachineBase {
	public enum Slots {
		electrodeA, electrodeB, alumina, redstone, mold
	}

	//very simplified since air has a varying SH
	protected static final int SH_AIR = 1376;
	//also simplified
	protected static final float DENSITY_AIR = 1.2f;

	protected static final int MR_ALUMINA = 102;
	/** Maximum capacity of molten redstone (B) */
	protected static final int MAX_REDSTONE_CAPACITY = 30;
	private static final int SH_REDSTONE = 1136;
	private static final int SH_ALUMINA = 451;
	private static final float COOLING_COEF = 1;

	protected int thermalEnergy = 14365440;
	protected int oldTemp = 0;
	protected int targetTemperature;

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

		return 64;
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
		int volRed = getRedstoneAmt();
		int volAir = MAX_REDSTONE_CAPACITY - getRedstoneAmt();
		int amtAlumina = getAluminaAmt();
		float massAir = volAir * DENSITY_AIR;
		int massRed = volRed;
		int massAlumina = amtAlumina * MR_ALUMINA / 1000;
		int volAluminium = getAluminiumAmt();
		MetalStat Al = TFTechness2.statMap.get("Aluminium");
		int massAluminium = volAluminium * Al.density;
		return massAir * SH_AIR + massRed * SH_REDSTONE + massAlumina
				* SH_ALUMINA + massAluminium * Al.heat.specificHeat;
	}

	/** Unit: buckets (m^3) */
	private int getAluminiumAmt() {

		return 0;
	}

	/** Unit: moles */
	private int getAluminaAmt() {

		return 0;
	}

	/** Unit: buckets (m^3) */
	private int getRedstoneAmt() {
		return 0;
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
	}

	@Override
	protected void readFromMasterNBT(NBTTagCompound data) {
		super.readFromMasterNBT(data);
		this.thermalEnergy = data.getInteger("ThermalEnergy");
		this.targetTemperature = data.getInteger("TargetTemperature");
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			float externalTemp = TFC_Climate.getHeightAdjustedTemp(worldObj,
					xCoord,
					yCoord,
					zCoord);
			this.thermalEnergy -= (this.getTemperature() - externalTemp)
					* COOLING_COEF;
		}
		super.updateEntity();

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
}
