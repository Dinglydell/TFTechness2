package dinglydell.tftechness.event;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.TFC_ItemHeat;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TFTEventHandler {

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
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
