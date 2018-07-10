package dinglydell.tftechness.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import blusunrize.immersiveengineering.common.IEContent;
import dinglydell.tftechness.block.component.property.ComponentProperty;

public abstract class TileMachineRF extends TileMachineComponent {
	public enum WireTier {
		lv(256, 1f, 0.02f), mv(1024, 0.85f, 0.1f), hv(4096, 0.7f, 0.5f);

		public final float efficiency;
		public final int transferRate;
		public final float inductionRate;

		WireTier(int transferRate, float efficiency, float inductionRate) {
			this.transferRate = transferRate;
			this.efficiency = efficiency;
			this.inductionRate = inductionRate;
		}

		public ItemStack getWire() {
			return new ItemStack(IEContent.itemWireCoil, 1, this.ordinal());
		}
	}

	//protected WireTier tier = WireTier.lv;

	protected int rfRate;

	//public TileMachineRF setWireTier(WireTier tier) {
	//	this.tier = tier;
	//	//default consuming machine capacity is 20 seconds of power
	//	this.setRFCapacity(tier.transferRate * 400);
	//	return this;
	//}

	@Override
	public void writeServerToClientMessage(NBTTagCompound nbt) {

		super.writeServerToClientMessage(nbt);
		//nbt.setInteger("tier", tier.ordinal());
		nbt.setInteger("rfRate", rfRate);

	}

	@Override
	public void readServerToClientMessage(NBTTagCompound nbt) {

		super.readServerToClientMessage(nbt);
		//tier = WireTier.values()[nbt.getInteger("tier")];
		rfRate = nbt.getInteger("rfRate");
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (!worldObj.isRemote && !isGenerator()) { //TODO: make RF consuming machines a subclass of this

			//redstone power -> shutdown
			if (worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) != 0) {
				rfRate = 0;
			} else {
				rfRate = spendRF(Math.min(rf, getWireTier().transferRate));
				rf -= rfRate;
			}

		}
	}

	protected abstract int spendRF(int amt);

	public int getEnergyRate() {
		return rfRate;
	}

	public WireTier getWireTier() {
		if (!materials.containsKey(ComponentProperty.WIRE_TIER)) {
			return WireTier.lv;
		}
		return (WireTier) materials.get(ComponentProperty.WIRE_TIER).validFor
				.get(ComponentProperty.WIRE_TIER);
	}

	/** Returns true if we're both on and able to use stuff */
	public boolean isEnabled() {
		return rf > 0
				&& worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) == 0;
	}

	public boolean isGenerator() {
		return false;
	}
}
