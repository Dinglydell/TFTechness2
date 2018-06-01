package dinglydell.tftechness.tileentities;

public class TileMachineHeatingElement extends TileMachineRF {

	protected static final float RF_TO_TEMP = 0.05f;

	public TileMachineHeatingElement() {
		super();
	}

	@Override
	protected int spendRF(int spend) {

		temperature += spend * RF_TO_TEMP * tier.efficiency;
		return spend;
	}
}
