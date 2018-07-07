package dinglydell.tftechness.tileentities;

public class TileMachineHeatingElement extends TileMachineRF {

	protected static final float RF_TO_TEMP = 0.05f;
	protected float throttleTemp = Float.MAX_VALUE;

	public TileMachineHeatingElement() {
		super();
	}

	@Override
	protected int spendRF(int spend) {
		int maxSpend = (int) ((throttleTemp - temperature) * getSpecificHeat() / getWireTier().efficiency);
		spend = Math.max(0, Math.min(maxSpend, spend));
		temperature += spend / getSpecificHeat() * getWireTier().efficiency;
		return spend;
	}

	public void setThrottleTemperature(float otherTargetTemp) {

		this.throttleTemp = otherTargetTemp;
	}

}
