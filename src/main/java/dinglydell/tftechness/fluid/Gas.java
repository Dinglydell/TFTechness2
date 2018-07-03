package dinglydell.tftechness.fluid;

import java.util.HashMap;
import java.util.Map;

import dinglydell.tftechness.TFTechness2;

public class Gas {
	public static Map<String, Gas> gasRegistry = new HashMap<String, Gas>();
	//public static Gas AIR = new Gas("air", -274);
	//TODO: Fluid forms of air-gases
	public static Gas NITROGEN = new Gas("nitrogen", -274);
	public static Gas OXYGEN = new Gas("oxygen", -274);
	public static Gas ARGON = new Gas("argon", -274);
	public static Gas CARBON_DIOXIDE = new Gas("carbonDioxide", -274);
	//	public static Gas Neon = new Gas("")
	public static Gas STEAM = new Gas("steam", 100).setZeroVapourPoint(-198);

	public String gasName;
	/** typical boiling point at 1atm pressure */
	private float boilingPoint;
	/** The point at which vapour pressure ~1Pa */
	private float zeroVapourPoint = TFTechness2.ABSOLUTE_ZERO;
	/** P = e^(k(T - freeze point)) */
	private double vapourPressureK;

	public Gas(String name, float boilingPoint) {
		this.gasName = name;
		this.boilingPoint = boilingPoint;
		gasRegistry.put(name, this);

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

	//public FluidStack condense(float amt){
	//Fluid f = TFTPropertyRegistry.getLiquid(this);

	//}
}
