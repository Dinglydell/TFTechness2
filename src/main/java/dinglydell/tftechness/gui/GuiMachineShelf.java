package dinglydell.tftechness.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.tileentities.TileMachineComponentItemShelf;

public class GuiMachineShelf extends GuiMachine {
	public static final ResourceLocation SHELF_TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/itemShelf.png");

	public GuiMachineShelf(InventoryPlayer player,
			TileMachineComponentItemShelf tile) {

		super(new ContainerMachineShelf(player, tile), player, tile);
	}

	@Override
	protected void renderToolTip(ItemStack stack, int x, int y) {
		List list = stack.getTooltip(this.mc.thePlayer,
				this.mc.gameSettings.advancedItemTooltips);
		for (int k = 0; k < list.size(); ++k) //set colours
		{
			if (k == 0) //name colour
			{
				list.set(k,
						stack.getRarity().rarityColor + (String) list.get(k));
			} else //extra colour
			{
				list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
			}
		}
		//then, add frozen tooltip if appropriate
		if (tile.getTemperature() <= 0) {
			list.add(EnumChatFormatting.AQUA + "gui.tooltip.frozen");
		}
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		drawHoveringText(list, x, y, (font == null ? fontRendererObj : font));
	}

	@Override
	protected ResourceLocation getTexture() {
		return SHELF_TEXTURE;
	}
}
