package dinglydell.tftechness.item;

import java.util.HashMap;
import java.util.Map;

import com.bioxx.tfc.api.Enums.EnumFuelMaterial;

import dinglydell.tftechness.world.TFTWorldData;

public class TFTFuel {

	protected static Map<String, TFTFuel> fuels = new HashMap<String, TFTFuel>();

	/** time it takes the fuel to burn in seconds */
	public int burnTime;
	/** temperature fuel burns at in degrees celsius */
	public int burnTemp;
	/** carbon content in kg */
	public float carbonContent;

	/** The temperature in degrees celsius required to ignite the fuel */
	public int ignitionTemperature;

	public String name;

	public TFTFuel(String name, int burnTime, int burnTemp,
			float carbonContent, int ignitionTemperature) {
		this.name = name;
		this.burnTime = burnTime;
		this.burnTemp = burnTemp;
		this.carbonContent = carbonContent;
		this.ignitionTemperature = ignitionTemperature;
		fuels.put(name, this);
	}

	public TFTFuel(EnumFuelMaterial material, float carbonContent,
			int ignitionTemperature) {
		this(material.name(), material.burnTimeMax, material.burnTempMax,
				carbonContent, ignitionTemperature);
	}

	public float getCarbonContentByPpm() {
		return carbonContent * TFTWorldData.MASS_TO_PPM;
	}

	public static TFTFuel getFuel(String name) {
		if (name == null || !fuels.containsKey(name)) {
			return null;
		}
		return fuels.get(name);
	}
}
