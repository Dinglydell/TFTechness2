package dinglydell.tftechness.world;

import net.minecraftforge.common.DimensionManager;

import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.Core.WeatherManager;

public class WeatherManagerTFT extends WeatherManager {
	//protected Random rand = new Random();
	@Override
	public float getDailyTemp() {
		return getDailyTemp(TFC_Time.getTotalDays());
	}

	@Override
	public float getDailyTemp(int day) {
		//rand.setSeed(seed + day);
		//float cfactor = -100;
		TFTWorldData data = TFTWorldData.get(DimensionManager.getWorld(0));
		float cfactor = data.temperatureOffset;
		return cfactor + super.getDailyTemp(day);
	}
}
