package dinglydell.tftechness.achievement;

import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

import com.bioxx.tfc.Core.TFC_Achievements;

import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.item.TFTMeta;

public class AchievementHandler {

	//public static Achievement stoneAge = new Achievement(
	//		"achievement.tftechness.stoneAge", "tftechness.stoneAge", 0, 0,
	//		TFCItems.looseRock, null).setSpecial();
	//public static Achievement bronzeAge = new Achievement(
	//		"achievement.tftechness.bronzeAge", "tftechness.bronzeAge", 2, 0,
	//		TFCItems.bronzePickaxeHead, stoneAge).setSpecial();
	//	public static Achievement ironAge = new Achievement(
	//			"achievement.tftechness.ironAge", "tftechness.ironAge", 4, 0,
	//		TFCItems.wroughtIronIngot, bronzeAge).setSpecial();

	public static Achievement steamAge = new Achievement(
			"achievement.tftechness.steamAge", "tftechness.steamAge", 4, 0,
			TFTMeta.fireboxSolid, TFC_Achievements.achBlastFurnace)
			.setSpecial();
	public static Achievement rfAge = new Achievement(
			"achievement.tftechness.rfAge", "tftechness.rfAge", 6, 0,
			TFTMeta.steamEngineHobbyist, steamAge).setSpecial();
	//	public static Achievement electrum = new Achievement(
	//			"achievement.tftechness.electrum", "tftechness.electrum", 9, 2,
	//			TFTItems.ingots.get("Electrum"), rfAge);
	public static Achievement mvAge = new Achievement(
			"achievement.tftechness.mvAge", "tftechness.mvAge", 9, 0,
			TFTMeta.ieMvWire, rfAge).setSpecial();
	public static Achievement aluminium = new Achievement(
			"achievement.tftechness.aluminium", "tftechness.aluminium", 10, 2,
			TFTItems.ingots.get("Aluminum"), mvAge);
	public static Achievement hvAge = new Achievement(
			"achievement.tftechness.hvAge", "tftechness.hvAge", 12, 0,
			TFTMeta.ieHvWire, aluminium).setSpecial();

	//public static Achievement titanium = new Achievement(
	//		"achievement.tftechness.titanium", "tftechness.titanium", 15, 2,
	//		TFTItems.ingots.get("Titanium"), hvAge);

	public static Achievement spaceAge = new Achievement(
			"achievement.tftechness.spaceAge", "tftechness.spaceAge", 15, 0,
			AdvancedRocketryBlocks.blockEngine, hvAge).setSpecial();

	//public static Achievement interplanetaryAge = new Achievement(
	//		"achievement.tftechness.interplanetaryAge",
	//		"tftechness.interplanetaryAge", 16, 0,
	//		AdvancedRocketryBlocks.blockWarpCore, spaceAge).setSpecial();

	public static void init() {
		//stoneAge.registerStat();

		Achievement[] achieves = new Achievement[] {// stoneAge,
		//	bronzeAge,
		//	ironAge,
		steamAge,
				rfAge,
				//	electrum,
				mvAge,
				aluminium,
				hvAge,
				//	titanium,
				spaceAge
		//interplanetaryAge
		};

		for (Achievement ach : achieves) {
			ach.registerStat();
		}

		AchievementPage.registerAchievementPage(new AchievementPage(
				"TFTechness", achieves));

	}
}
