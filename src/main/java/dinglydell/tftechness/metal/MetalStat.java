package dinglydell.tftechness.metal;

import com.bioxx.tfc.api.HeatRaw;

public class MetalStat {
	public HeatRaw heat;
	public int ingotMass;
	
	public MetalStat(double specificHeat, double meltTemp, int ingotMass) {
		this.heat = new HeatRaw(specificHeat, meltTemp);
		this.ingotMass = ingotMass;
	}
}
