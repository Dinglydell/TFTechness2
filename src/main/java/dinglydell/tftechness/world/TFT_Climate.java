package dinglydell.tftechness.world;

import net.minecraft.world.World;

public class TFT_Climate {
	private static float getTemp0(World world, int day, int hour, int x, int z,
			boolean bio) {
		return -20;
		//if(TFC_Climate.getCacheManager(world) != null)
		//{
		//	float zMod = getZFactor(z);
		//	float zTemp = zMod * getMaxTemperature() - 20 + ((zMod - 0.5f) * 10);
		//
		//	float rain = getRainfall(world, x, Global.SEALEVEL, z);
		//	float rainMod = (1-(rain/4000))*zMod;
		//
		//	int month = TFC_Time.getSeasonFromDayOfYear(day,z);
		//	int lastMonth = TFC_Time.getSeasonFromDayOfYear(day-TFC_Time.daysInMonth,z);
		//	
		//	float monthTemp = getMonthTemp(month, z);
		//	float lastMonthTemp = getMonthTemp(lastMonth, z);
		//
		//	int dayOfMonth = TFC_Time.getDayOfMonthFromDayOfYear(day);
		//
		//	float hourMod;
		//	float dailyTemp;
		//	if (bio) {
		//		hourMod = 0.2f;
		//		dailyTemp = 0;
		//	} else {
		//		int h = (hour - 6) % TFC_Time.HOURS_IN_DAY;
		//		if (h < 0) {
		//			h += TFC_Time.HOURS_IN_DAY;
		//		}
		//
		//		if(h < 12)
		//			hourMod = ((float)h / 11) * 0.3F;
		//		else
		//			hourMod = 0.3F - ((((float)h-12) / 11) * 0.3F);
		//		
		//		dailyTemp = WeatherManager.getInstance().getDailyTemp(day);
		//	}
		//
		//	float monthDelta = ((monthTemp-lastMonthTemp) * dayOfMonth) / TFC_Time.daysInMonth;
		//	float temp = lastMonthTemp + monthDelta;
		//
		//	temp += dailyTemp + (hourMod*(zTemp + dailyTemp));
		//
		//	if(temp >= 12)
		//		temp += (8*rainMod)*zMod;
		//	else
		//		temp -= (8*rainMod)*zMod;
		//		
		//	return temp;
		//}
		//return -10;
	}   //

}
