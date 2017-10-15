package dinglydell.tftechness.recipe;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import com.bioxx.tfc.api.Crafting.AnvilManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.metal.Material;

public class TFTAnvilRecipeHandler {
	public static final String rodPlan = "rod";
	public static final String sheetPlan = "sheet";

	public long wSeed = Long.MIN_VALUE;
	@SubscribeEvent(receiveCanceled = true)
	public void onServerWorldTick(WorldEvent.Load event) {
		World world = event.world;
		long newSeed = world.getWorldInfo().getSeed();
		if (world.provider.dimensionId == 0 && newSeed != wSeed && newSeed != 0) {
			AnvilManager.world = world;
			for(Material m : TFTechness2.materials ){
				m.addAnvilRecipes();
			}
			//wSeed = world.getWorldInfo().getSeed();
		}
	}
}
