package dinglydell.tftechness.fluid;

import net.minecraft.util.StatCollector;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.util.StringUtil;
import dinglydell.tftechness.world.TFTWorldData;

/** Like a FluidStack, but specifically for gases */
public class GasStack {
	public static final float STANDARD_PRESSURE = 1e5f;
	public static final float GAS_CONSTANT = 8.31f;

	private Gas gas;
	/** The amount of gas, in moles */
	public double amount;
	/** The temperature of the gas in celsius */
	private float temperature;

	public GasStack(Gas gas, float amount, float temperature) {
		this.gas = gas;
		this.amount = amount;
		this.temperature = temperature;
	}

	/** Creates a gas stack under standard conditions (100KPa, 15 degrees) */
	public GasStack(Gas gas, float volume) {
		this.gas = gas;
		this.amount = STANDARD_PRESSURE * volume
				/ (GAS_CONSTANT * TFTWorldData.baseTemperature);
		this.temperature = TFTWorldData.baseTemperature
				+ TFTechness2.ABSOLUTE_ZERO;
	}

	/**
	 * Returns the pressure in Pa of the gas at a given volume
	 * 
	 * @param volume
	 *            The volume, in m^3, that the gas takes up
	 * */
	public double getPressure(double volume) {
		return amount * (temperature - TFTechness2.ABSOLUTE_ZERO)
				* GAS_CONSTANT / volume;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public String getDisplayString(float volume) {
		double p = getPressure(volume);
		;
		return StatCollector.translateToLocal("gas." + gas.gasName) + ": "
				+ StringUtil.prefixify(p) + "Pa";
	}

}
