package dinglydell.tftechness.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dinglydell.tftechness.TFTechness2;

public class TFTConfig {

	public static int HEMP_ID;

	public static void loadConifg(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(new File(
				event.getModConfigurationDirectory(), TFTechness2.MODID
						+ "/General.cfg"), true);

		HEMP_ID = config.getInt("HempCropID",
				"Crops",
				19,
				0,
				Integer.MAX_VALUE,
				"");
	}
}
