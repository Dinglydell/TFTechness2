package dinglydell.tftechness.world;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.world.World;

import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.Core.WeatherManager;
import com.bioxx.tfc.api.Constant.Global;

public class TFT_Climate {
	public static float getTemp0(World world, int day, int hour, int x, int z,
			boolean bio) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		TFTWorldData data = TFTWorldData.get(world);
		if (data.isEarth() && TFC_Climate.getCacheManager(world) != null) {
			Method zFactor = TFC_Climate.class.getDeclaredMethod("getZFactor",
					int.class);
			zFactor.setAccessible(true);
			float zMod = (Float) zFactor.invoke(null, z);
			//float zMod = 0.5f;
			float zTemp = zMod * TFC_Climate.getMaxTemperature() - 20
					+ ((zMod - 0.5f) * 10);

			float rain = TFC_Climate.getRainfall(world, x, Global.SEALEVEL, z);
			float rainMod = (1 - (rain / 4000)) * zMod;

			int month = TFC_Time.getSeasonFromDayOfYear(day, z);
			int lastMonth = TFC_Time.getSeasonFromDayOfYear(day
					- TFC_Time.daysInMonth, z);
			Method monthTempMethod = TFC_Climate.class
					.getDeclaredMethod("getMonthTemp", int.class, int.class);
			monthTempMethod.setAccessible(true);
			float monthTemp = (Float) monthTempMethod.invoke(null, month, z);
			float lastMonthTemp = (Float) monthTempMethod.invoke(null,
					lastMonth,
					z);
			//float monthTemp = 1;
			//float lastMonthTemp = 0;

			int dayOfMonth = TFC_Time.getDayOfMonthFromDayOfYear(day);

			float hourMod;
			float dailyTemp;
			if (bio) {
				hourMod = 0.2f;
				dailyTemp = 0;
			} else {
				int h = (hour - 6) % TFC_Time.HOURS_IN_DAY;
				if (h < 0) {
					h += TFC_Time.HOURS_IN_DAY;
				}

				if (h < 12)
					hourMod = ((float) h / 11) * 0.3F;
				else
					hourMod = 0.3F - ((((float) h - 12) / 11) * 0.3F);

				dailyTemp = WeatherManager.getInstance().getDailyTemp(day);
			}
			dailyTemp += data.temperatureOffset;

			float monthDelta = ((monthTemp - lastMonthTemp) * dayOfMonth)
					/ TFC_Time.daysInMonth;
			float temp = lastMonthTemp + monthDelta;

			temp += dailyTemp + (hourMod * (zTemp + dailyTemp));

			if (temp >= 12)
				temp += (8 * rainMod) * zMod;
			else
				temp -= (8 * rainMod) * zMod;

			return temp;
		}

		//non-earth planets
		int hoursInDay = (data.dimProps.rotationalPeriod / 1000);
		long totalHours = TFC_Time.getTotalHours();
		int timeOfDay = (hour - 6) % hoursInDay;
		if (timeOfDay < 0) {
			timeOfDay += hoursInDay;
		}
		float amplitude = (float) (100 / (1 + Math.log(1 + data.dimProps
				.getAtmosphereDensity())));
		float hourOffset = (float) (amplitude * Math.sin(2 * Math.PI
				* totalHours / (float) hoursInDay + Math.PI / 4));
		//float yearLength = (float) (2 * Math.PI / (0.36 * (201 - data.dimProps.getOrbitalDist())));

		// the z value for which the temperature is average
		float avgZ = data.dimProps.getGravitationalMultiplier() * 5000;
		float zFactor = 2f / ((z / avgZ) + 1) - 1;
		float seasonAmplitude = amplitude * (1 - 1f / (z + 1));
		float seasonOffset = (float) (seasonAmplitude * Math.sin(data.dimProps
				.getOrbitTheta()));
		return Math.max(-273f, data.getTemperature() + hourOffset
				+ seasonOffset + zFactor);
	}

}
