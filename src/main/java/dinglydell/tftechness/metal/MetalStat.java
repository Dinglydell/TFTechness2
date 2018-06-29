package dinglydell.tftechness.metal;

import com.bioxx.tfc.api.HeatRaw;

public class MetalStat {
	public HeatRaw heat;
	/** Density in kg/m^3 */
	public int density;
	public float conductivity;
	public float yieldStress;

	/**
	 * @param specificHeat
	 *            Specific heat in kj/(kg K)
	 * @param meltTemp
	 *            Melting point in degrees C
	 * @param density
	 *            Density in kg/m^3
	 * @param conductivity
	 *            A measure of the thermal conductivity in %
	 * @param yieldStress
	 *            The yield point of a material in Pa (maximum stress before
	 *            deformation)
	 * */
	public MetalStat(double specificHeat, double meltTemp, int density,
			float conductivity, float yieldStress) {
		this.heat = new HeatRaw(specificHeat, meltTemp);
		this.density = density;
		this.conductivity = conductivity;
		this.yieldStress = yieldStress;
	}

	/**
	 * Returns the specific heat in SI units (j/(kg K))
	 * 
	 * TFC uses kj/(kg K) by default
	 * */
	public float getSISpecificHeat() {
		return heat.specificHeat * 1000;
	}
}
