package dinglydell.tftechness.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import blusunrize.immersiveengineering.common.IEContent;

public abstract class TileMachineRF extends TileMachineComponent {
	public enum WireTier {
		lv(256, 1f), mv(1024, 0.85f), hv(4096, 0.7f);

		public final float efficiency;
		public final int transferRate;

		WireTier(int transferRate, float efficiency) {
			this.transferRate = transferRate;
			this.efficiency = efficiency;
		}

		public ItemStack getWire() {
			return new ItemStack(IEContent.itemWireCoil, 1, this.ordinal());
		}
	}

	protected WireTier tier = WireTier.lv;

	private int rfRate;

	public TileMachineRF setWireTier(WireTier tier) {
		this.tier = tier;
		//default consuming machine capacity is 20 seconds of power
		this.setRFCapacity(tier.transferRate * 400);
		return this;
	}

	@Override
	public void writeComponentPropertiesToNBT(NBTTagCompound data) {

		super.writeComponentPropertiesToNBT(data);
		data.setInteger("tier", tier.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {

		super.readFromNBT(data);

		this.setWireTier(WireTier.values()[data.getInteger("tier")]);
	}

	@Override
	public void writeServerToClientMessage(NBTTagCompound nbt) {

		super.writeServerToClientMessage(nbt);
		nbt.setInteger("tier", tier.ordinal());
		nbt.setInteger("rfRate", rfRate);

	}

	@Override
	public void readServerToClientMessage(NBTTagCompound nbt) {

		super.readServerToClientMessage(nbt);
		tier = WireTier.values()[nbt.getInteger("tier")];
		rfRate = nbt.getInteger("rfRate");
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (!worldObj.isRemote) {
			//TFTechness2.logger.info(temperature);
			//	temperature += (1 + tier.ordinal()) * 5f;
			rfRate = spendRF(Math.min(rf, tier.transferRate));
			rf -= rfRate;
		}
	}

	protected abstract int spendRF(int amt);

	public int getEnergyConsumptionRate() {
		return rfRate;
	}
}
