package dinglydell.tftechness.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public class TileMachineDynamo extends TileMachineRF implements IEnergyProvider {

	private static final float RPS_TO_RF = 50f;

	@Override
	public int extractEnergy(ForgeDirection dir, int amt, boolean simulate) {
		amt = Math.min(amt, rf);
		if (!simulate) {
			rf -= amt;
		}
		return amt;
	}

	@Override
	protected int spendRF(int amt) {

		return 0;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			return;
		}
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord
					+ dir.offsetY, zCoord + dir.offsetZ);
			if (te instanceof IEnergyReceiver
					&& !(te instanceof TileMachineComponent)) {
				extractEnergy(dir.getOpposite(),
						((IEnergyReceiver) te).receiveEnergy(dir, rf, false),
						false);
			}
		}
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	/**
	 * Rotates the dynamo with a certain amount of RPS.
	 * 
	 * @return the reduction in RPS as a result of induction
	 */
	public float rotateDynamo(float rps) {
		WireTier tier = getWireTier();
		float dRPS = tier.inductionRate * rps;
		rfRate = (int) Math.min(dRPS * tier.efficiency * RPS_TO_RF,
				tier.transferRate);
		rf = Math.min(rfCapacity, rf + rfRate);

		return dRPS;
	}

	@Override
	public boolean isGenerator() {
		return true;
	}

}
