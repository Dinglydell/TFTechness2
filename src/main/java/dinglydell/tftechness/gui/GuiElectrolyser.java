package dinglydell.tftechness.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import dinglydell.tftechness.TFTechness2;

public class GuiElectrolyser extends GuiScreen {
	public static final ResourceLocation TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/electrolyser.png");
	private static final int TEX_WIDTH = 256;
	private static final int TEX_HEIGHT = 256;

	public GuiElectrolyser() {

	}

	@Override
	public void initGui() {

		super.initGui();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(TEXTURE);

		int offsetFromScreenLeft = (width - TEX_WIDTH) / 2;
		drawTexturedModalRect(offsetFromScreenLeft,
				2,
				0,
				0,
				TEX_WIDTH,
				TEX_HEIGHT);
		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
	}
}
