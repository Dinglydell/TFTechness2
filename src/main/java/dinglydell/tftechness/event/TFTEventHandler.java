package dinglydell.tftechness.event;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.BlockEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketDeOrbitingEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
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
import com.bioxx.tfc.api.Events.AnvilCraftEvent;
import com.bioxx.tfc.api.Util.Helper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import dinglydell.tftechness.achievement.AchievementHandler;
import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.item.TFTMeta;
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
		if (TFC_ItemHeat.hasTemp(event.itemStack)) {
			float temp = TFC_ItemHeat.getTemp(event.itemStack);
			HeatIndex index = HeatRegistry.getInstance()
					.findMatchingIndex(event.itemStack);
			//if (temp < 15) {
			//event.toolTip.add(getColdHeatColour(temp));
			/* } else */if (!(event.itemStack.getItem() instanceof ItemTerra)) {

				event.toolTip.add(TFC_ItemHeat.getHeatColor(temp,
						index == null ? Float.MAX_VALUE : index.meltTemp));
			}
		}
	}

	private String getColdHeatColour(float temp) {
		String colour = null;
		if (temp <= 0) {
			colour = EnumChatFormatting.AQUA.toString() + "Frozen";
		} else {
			colour = "Cold";
		}
		return colour;
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
		//cpw.mods.fml.common.gameevent.TickEvent.
		data.tick(event);

	}

	@SubscribeEvent
	public void onBlockBroken(BlockEvent.HarvestDropsEvent event) {
		if (event.block == TFCBlocks.leaves || event.block == TFCBlocks.leaves2) {
			TFTWorldData.get(event.world).breakLeaf();
		}

	}

	@SubscribeEvent
	public void onBlockPlaced(BlockEvent.PlaceEvent event) {
		if (event.block == TFCBlocks.leaves || event.block == TFCBlocks.leaves2) {
			TFTWorldData.get(event.world).makeLeaf();
		}

	}

	@SubscribeEvent
	public void onTreeGrown(SaplingGrowTreeEvent event) {
		TFTWorldData.get(event.world).makeTree(event);

	}

	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event) {

		//if (event.crafting.getItem() instanceof ItemTool) {
		//	ToolMaterial t = ((ItemTool) event.crafting.getItem())
		//			.func_150913_i();
		//	if (t == TFCItems.sedToolMaterial || t == TFCItems.mMToolMaterial
		//			|| t == TFCItems.igExToolMaterial
		//			|| t == TFCItems.igInToolMaterial) {
		//		event.player.triggerAchievement(AchievementHandler.stoneAge);
		//	} else if (t == TFCItems.bismuthBronzeToolMaterial
		//			|| t == TFCItems.blackBronzeToolMaterial
		//			|| t == TFCItems.bronzeToolMaterial) {
		//		event.player.triggerAchievement(AchievementHandler.bronzeAge);
		//	}
		//
		//}
		//
		//if (event.crafting.getItem() instanceof ItemAnvil) {
		//	AnvilReq req;
		//	if (((ItemAnvil) event.crafting.getItem()).field_150939_a == TFCBlocks.anvil) {
		//		req = AnvilReq.getReqFromInt(event.crafting.getItemDamage());
		//	} else {
		//		req = AnvilReq.getReqFromInt2(event.crafting.getItemDamage());
		//	}
		//	if (req.Tier == AnvilReq.BRONZE.Tier) {
		//		event.player.triggerAchievement(AchievementHandler.bronzeAge);
		//	}
		//}
		//
		//if (event.crafting.getItem() == TFCItems.wroughtIronIngot) {
		//	event.player.triggerAchievement(AchievementHandler.ironAge);
		//}
		//
		if (event.crafting.isItemEqual(TFTMeta.fireboxSolid)
				|| event.crafting.isItemEqual(TFTMeta.fireboxLiquid)) {
			event.player.triggerAchievement(AchievementHandler.steamAge);
		}
		if (event.crafting.isItemEqual(TFTMeta.steamEngineHobbyist)) {
			event.player.triggerAchievement(AchievementHandler.rfAge);
		}
		if (event.crafting.isItemEqual(TFTMeta.ieMvWire)) {
			event.player.triggerAchievement(AchievementHandler.mvAge);
		}
		if (event.crafting.getItem() == TFTItems.ingots.get("Aluminum")) {
			event.player.triggerAchievement(AchievementHandler.hvAge);
		}
		if (event.crafting.isItemEqual(TFTMeta.ieHvWire)) {
			event.player.triggerAchievement(AchievementHandler.hvAge);
		}

	}

	@SubscribeEvent
	public void onRocketLaunch(RocketLaunchEvent event) {

		if (event.entity.riddenByEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity.riddenByEntity;
			player.triggerAchievement(AchievementHandler.spaceAge);
		}
	}

	@SubscribeEvent
	public void onAnvilCraft(AnvilCraftEvent event) {
		//if (event.result.getItem() == TFCItems.wroughtIronIngot
		//		&& event.entity instanceof EntityPlayer) {
		//	((EntityPlayer) event.entity)
		//			.triggerAchievement(AchievementHandler.ironAge);
		//}
	}   //

}
