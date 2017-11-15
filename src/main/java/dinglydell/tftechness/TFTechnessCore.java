package dinglydell.tftechness;

import java.lang.reflect.Field;

import net.minecraft.init.Blocks;
import zmaster587.advancedRocketry.tile.multiblock.TileAstrobodyDataProcessor;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TFTechness2.MODID + "Core", version = TFTechness2.VERSION, dependencies = "required-before:terrafirmacraft;required-before:ImmersiveEngineering;before:advancedRocketry;before:libVulpes")
public class TFTechnessCore {

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		hackARMultiblock();
	}

	private void hackARMultiblock() {
		//astrobody data processor
		Object[][][] structure = new Object[][][] { { { Blocks.air,
				'c',
				Blocks.air },
				{ Blocks.air, Blocks.air, Blocks.air } },

				{ { 'P', 'I', 'O' }, { 'D', 'D', 'D' } } };
		try {
			Field structField = TileAstrobodyDataProcessor.class
					.getDeclaredField("structure");
			TFTechness2.finalField(structField);
			structField.setAccessible(true);
			structField.set(null, structure);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
