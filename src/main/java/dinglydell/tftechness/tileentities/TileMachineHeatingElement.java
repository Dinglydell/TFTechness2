package dinglydell.tftechness.tileentities;

public class TileMachineHeatingElement extends TileMachineRF {

	protected static final float RF_TO_TEMP = 0.05f;
	protected float targetTemp = Float.MAX_VALUE;

	public TileMachineHeatingElement() {
		super();
	}

	@Override
	protected int spendRF(int spend) {
		int maxSpend = (int) ((targetTemp - temperature) * getSpecificHeat() / getWireTier().efficiency);
		spend = Math.max(0, Math.min(maxSpend, spend));
		temperature += spend / getSpecificHeat() * getWireTier().efficiency;
		return spend;
	}

	@Override
	public void setTargetTemperature(float otherTargetTemp) {

		this.targetTemp = otherTargetTemp;
	}

}
