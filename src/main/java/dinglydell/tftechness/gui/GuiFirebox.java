package dinglydell.tftechness.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.tileentities.TileMachineFirebox;

public class GuiFirebox extends GuiMachine {
	public static final ResourceLocation FIREBOX_TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/firebox.png");

	protected TileMachineFirebox tile;

	public GuiFirebox(InventoryPlayer player, TileMachineFirebox tile) {
		super(new ContainerFirebox(player, tile), player, tile);
		this.tile = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		super.drawGuiContainerBackgroundLayer(p_146976_1_,
				p_146976_2_,
				p_146976_3_);
		if (tile.isIgnited()) {
			int height = Math.max(1, Math.min(13,
					(int) Math.ceil(13 * tile.getFuelTimeLeft() / 2000f)));
			mc.getTextureManager().bindTexture(getTexture());
			drawTexturedModalRect(guiLeft + 81,
					guiTop + 40 - height,
					TEX_WIDTH + 1,
					13 - height,
					14,
					height);
		}

	}

	@Override
	protected ResourceLocation getTexture() {
		return FIREBOX_TEXTURE;
	}

}
