package dinglydell.tftechness.core;

import java.lang.reflect.Field;
import java.util.Arrays;

import net.minecraft.init.Blocks;
import zmaster587.advancedRocketry.tile.multiblock.TileAstrobodyDataProcessor;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dinglydell.tftechness.TFTechness2;

//@MCVersion(value = "1.7.10")
//@Mod(modid = TFTechnessCore.MODID, version = TFTechness2.VERSION, dependencies = "required-before:terrafirmacraft;required-before:ImmersiveEngineering;before:advancedRocketry;before:libVulpes")
public class TFTechnessCore extends DummyModContainer {
	public static final String MODID = TFTechness2.MODID + "Core";

	public TFTechnessCore() {
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = MODID;
		meta.name = "TFTCore";
		meta.version = TFTechness2.VERSION;
		meta.credits = "";
		meta.authorList = Arrays.asList("");
		meta.description = "";
		meta.url = "";
		meta.updateUrl = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";

	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);

		return true;
	}

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
