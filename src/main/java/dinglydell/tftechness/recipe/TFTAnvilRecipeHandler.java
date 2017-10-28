package dinglydell.tftechness.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.Crafting.AnvilManager;
import com.bioxx.tfc.api.Crafting.AnvilRecipe;
import com.bioxx.tfc.api.Crafting.AnvilReq;
import com.bioxx.tfc.api.Crafting.PlanRecipe;
import com.bioxx.tfc.api.Enums.RuleEnum;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.item.TFTMeta;
import dinglydell.tftechness.metal.Material;

public class TFTAnvilRecipeHandler {
	public static final String rodPlan = "rod";
	public static final String sheetPlan = "sheet";
	public static final String bushingPlan = "bushing";
	public static final String gearPlan = "gear";
	public long wSeed = Long.MIN_VALUE;

	@SubscribeEvent(receiveCanceled = true)
	public void onServerWorldTick(WorldEvent.Load event) {
		World world = event.world;
		long newSeed = world.getWorldInfo().getSeed();
		if (world.provider.dimensionId == 0 && newSeed != wSeed && newSeed != 0) {
			AnvilManager.world = world;

			AnvilManager manager = AnvilManager.getInstance();
			//TODO: seed based rules
			manager.addPlan(rodPlan, new PlanRecipe(
					new RuleEnum[] { RuleEnum.HITLAST,
							RuleEnum.BENDSECONDFROMLAST,
							RuleEnum.BENDLAST }));
			manager.addPlan(bushingPlan, new PlanRecipe(
					new RuleEnum[] { RuleEnum.HITLAST,
							RuleEnum.BENDSECONDFROMLAST,
							RuleEnum.BENDLAST }));
			manager.addPlan(gearPlan, new PlanRecipe(
					new RuleEnum[] { RuleEnum.HITLAST,
							RuleEnum.BENDSECONDFROMLAST,
							RuleEnum.BENDLAST }));

			for (Material m : TFTechness2.materials) {
				m.addAnvilRecipes();
			}

			//tin gear bushing
			manager.addRecipe(new AnvilRecipe(new ItemStack(TFCItems.tinSheet),
					null, TFTAnvilRecipeHandler.bushingPlan, AnvilReq.COPPER,
					TFTMeta.tinGearBushing));

			//iron gear
			manager.addRecipe(new AnvilRecipe(new ItemStack(
					TFCItems.wroughtIronSheet2x), TFTMeta.tinGearBushing,
					TFTAnvilRecipeHandler.gearPlan, AnvilReq.WROUGHTIRON,
					TFTMeta.ironGear));

			//steel gear
			manager.addRecipe(new AnvilRecipe(new ItemStack(
					TFCItems.steelSheet2x), TFTMeta.tinGearBushing,
					TFTAnvilRecipeHandler.gearPlan, AnvilReq.STEEL,
					TFTMeta.steelGear));

			//wSeed = world.getWorldInfo().getSeed();
		}
	}
}
