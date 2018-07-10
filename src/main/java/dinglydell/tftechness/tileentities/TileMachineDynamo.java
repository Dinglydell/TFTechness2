package dinglydell.tftechness.tileentities;

import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyProvider;

public class TileMachineDynamo extends TileMachineRF implements IEnergyProvider {

	private static final float RPS_TO_RF = 100f;

	@Override
	public int extractEnergy(ForgeDirection dir, int amt, boolean doExtract) {
		amt = Math.min(amt, rf);
		if (doExtract) {
			rf -= amt;
		}
		return amt;
	}

	@Override
	protected int spendRF(int amt) {
		return 0;
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
