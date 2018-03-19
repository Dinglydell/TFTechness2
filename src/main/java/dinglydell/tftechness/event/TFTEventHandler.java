package dinglydell.tftechness.event;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketDeOrbitingEvent;
import zmaster587.advancedRocketry.entity.EntityRocket;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Food.ItemFoodTFC;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.Food;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFC_ItemHeat;
import com.bioxx.tfc.api.Constant.Global;
import com.bioxx.tfc.api.Util.Helper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import dinglydell.tftechness.world.TFTWorldData;

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

	//replaces stone with basalt for asteroid missions
	@SubscribeEvent
	public void onRocketDeorbit(RocketDeOrbitingEvent event) {
		EntityRocket rocket = (EntityRocket) event.entity;
		List<TileEntity> invTiles = rocket.storage.getInventoryTiles();
		for (TileEntity te : invTiles) {
			IInventory inv = (IInventory) te;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(0);
				if (stack != null
						&& Item.getItemFromBlock(Blocks.stone) == stack
								.getItem()) {
					inv.setInventorySlotContents(i, new ItemStack(
							TFCBlocks.stoneIgEx, stack.stackSize, 1));
				}
			}
		}
	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		TFTWorldData data = TFTWorldData.get(event.world);

		data.tick(event);

	}

	@SubscribeEvent
	public void onBlockBroken(BlockEvent.BreakEvent event) {
		//TODO: Make this work when blocks are created/destroyed by things other than player
		if (event.block == TFCBlocks.leaves || event.block == TFCBlocks.leaves2) {
			TFTWorldData.get(event.world).breakLeaf();
		}
	}

	@SubscribeEvent
	public void onBlockPlaced(BlockEvent.PlaceEvent event) {
		//TODO: Make this work when blocks are created/destroyed by things other than player
		if (event.block == TFCBlocks.leaves || event.block == TFCBlocks.leaves2) {
			TFTWorldData.get(event.world).makeLeaf();
		}
	}
}
