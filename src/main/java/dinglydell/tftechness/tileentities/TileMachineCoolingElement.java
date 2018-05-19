package dinglydell.tftechness.tileentities;

import dinglydell.tftechness.TFTechness2;

public class TileMachineCoolingElement extends TileMachineRF {

	private static final float RF_TO_COOLANT = 0.01f;

	@Override
	protected int spendRF(int spend) {

		temperature = Math.max(TFTechness2.ABSOLUTE_ZERO, temperature - spend
				* RF_TO_COOLANT * tier.efficiency);
		return spend;
	}

}
