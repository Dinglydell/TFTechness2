package dinglydell.tftechness.event;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Food.ItemFoodTFC;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.Food;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.TFC_ItemHeat;
import com.bioxx.tfc.api.Constant.Global;
import com.bioxx.tfc.api.Util.Helper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TFTEventHandler {

	public static final float OZ_TO_KG = 0.02834952f;
	public static final float FOOD_MAX_MASS_KG = Helper
			.roundNumber(Global.FOOD_MAX_WEIGHT * OZ_TO_KG, 1000);

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		if (event.itemStack.getItem() instanceof ItemFoodTFC) {
			//ItemFoodTFC food = (ItemFoodTFC) event.itemStack.getItem();
			float kg = Helper.roundNumber(Food.getWeight(event.itemStack)
					* OZ_TO_KG, 1000);
			event.toolTip.add(TFC_Core.translate("gui.food.amount") + " " + kg
					+ " kg / " + FOOD_MAX_MASS_KG + " kg");
		}
		if (event.itemStack.getItem() instanceof ItemTerra) {
			return;
		}
		float temp = TFC_ItemHeat.getTemp(event.itemStack);

		if (temp != 0) {
			HeatIndex index = HeatRegistry.getInstance()
					.findMatchingIndex(event.itemStack);
			event.toolTip.add(TFC_ItemHeat.getHeatColor(temp,
					index == null ? Float.MAX_VALUE : index.meltTemp));
		}
	}
}
