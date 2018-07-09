package dinglydell.tftechness.fluid;

import java.util.HashMap;
import java.util.Map;

import dinglydell.tftechness.TFTechness2;

public class Gas {
	public static Map<String, Gas> gasRegistry = new HashMap<String, Gas>();
	//public static Gas AIR = new Gas("air", -274);
	//TODO: Fluid forms of air-gases
	public static Gas NITROGEN = new Gas("nitrogen", -274, 0.028f);
	public static Gas OXYGEN = new Gas("oxygen", -274, 0.032f);
	public static Gas ARGON = new Gas("argon", -274, 0.040f);
	public static Gas CARBON_DIOXIDE = new Gas("carbonDioxide", -274, 44);
	//	public static Gas Neon = new Gas("")
	public static Gas STEAM = new Gas("steam", 100, 0.018f)
			.setZeroVapourPoint(-198);

	public String gasName;
	/** typical boiling point at 1atm pressure */
	private float boilingPoint;
	/** The point at which vapour pressure ~1Pa */
	private float zeroVapourPoint = TFTechness2.ABSOLUTE_ZERO;
	/** P = e^(k(T - freeze point)) */
	private double vapourPressureK;
	/** kg/mol */
	private float molarMass;

	public Gas(String name, float boilingPoint, float molarMass) {
		this.gasName = name;
		this.boilingPoint = boilingPoint;
		gasRegistry.put(name, this);
		this.molarMass = molarMass;
		calcVapourPressure();
	}

	public Gas setZeroVapourPoint(float temperature) {
		this.zeroVapourPoint = temperature;
		this.calcVapourPressure();
		return this;
	}

	private void calcVapourPressure() {
		if (boilingPoint <= TFTechness2.ABSOLUTE_ZERO) {
			vapourPressureK = -1;
			return;
		}
		vapourPressureK = Math.log(TFTechness2.STANDARD_PRESSURE)
				/ (boilingPoint - zeroVapourPoint);

	}

	/** Returns the boiling point of the gas in degrees celsius */
	public float getBoilingPoint() {
		return boilingPoint;
	}

	public double getVapourPressure(float temperature) {
		if (vapourPressureK < 0) {
			return Float.POSITIVE_INFINITY;
		}
		return Math.exp(vapourPressureK * (temperature - zeroVapourPoint));
	}

	/** kg/mol */
	public float getMolarMass() {
		return molarMass;
	}

	//public FluidStack condense(float amt){
	//Fluid f = TFTPropertyRegistry.getLiquid(this);

	//}
}
