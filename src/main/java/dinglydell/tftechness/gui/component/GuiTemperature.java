package dinglydell.tftechness.gui.component;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.component.property.ComponentPropertyThermometerTier.ThermometerTier;
import dinglydell.tftechness.util.StringUtil;

public class GuiTemperature extends GuiButtonTFT {
	public static final int MAX_SCALE_TEMP = 2000;
	public static final ResourceLocation TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/SliderIndicator.png");
	private static final int TEX_WIDTH = 15;
	private static final int TEX_HEIGHT = 5;
	public ITileTemperature tile;
	private boolean target;
	private boolean mouseDown = false;
	private ThermometerTier tier;

	public GuiTemperature(int id, int x, int y, int width, int height,
			ITileTemperature tile, boolean target, ThermometerTier tier) {
		super(id, x, y, width, height, "");
		this.tile = tile;
		this.target = target;
		this.tier = tier;
		;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (tile == null) {
			return;
		}
		if (target && isHovering(mouseX, mouseY)) {
			int wheelMovement = Mouse.getDWheel() / 120;
			//TFTechness2.logger.info(wheelMovement);
			int t = tier.increment(tile.getTargetTemperature(), wheelMovement);
			tile.setTargetTemperature((int) Math.max(TFTechness2.ABSOLUTE_ZERO,
					t));
		}

		float temp;
		if (target) {
			temp = tile.getTargetTemperature();
		} else {
			temp = tile.getTemperature();
		}
		mc.getTextureManager().bindTexture(TEXTURE);
		//drawTexturedModalRect(0, 0, 0, 0, TEX_WIDTH, TEX_HEIGHT);
		drawTexturedModalRect(this.xPosition - 3,
				(int) (this.yPosition + this.height
						* (1 - temp / MAX_SCALE_TEMP)),
				0,
				0,
				TEX_WIDTH,
				TEX_HEIGHT);

		//max T
		float maxTemp = tile.getMaxTemperature();
		drawTexturedModalRect(this.xPosition - 1,
				(int) (this.yPosition + this.height
						* (1 - maxTemp / MAX_SCALE_TEMP)),
				2,
				0,
				TEX_WIDTH - 4,
				TEX_HEIGHT);

	}

	@Override
	public boolean mousePressed(Minecraft p_146116_1_, int p_146116_2_,
			int p_146116_3_) {
		this.mouseDown = true;
		return super.mousePressed(p_146116_1_, p_146116_2_, p_146116_3_);
	}

	@Override
	protected void mouseDragged(Minecraft mc, int x, int y) {
		if (this.mouseDown && this.target && tile != null) {
			TFTechness2.logger.info(y);
			float scaleY = (y - this.yPosition) / (float) this.height;
			//TFTechness2.logger.info(scaleY);
			tile.setTargetTemperature((int) ((1 - scaleY) * MAX_SCALE_TEMP));

		}

		super.mouseDragged(mc, x, y);
	}

	@Override
	public void mouseReleased(int x, int y) {
		if (target && tile != null) {
			float scaleY = (y - this.yPosition) / (float) this.height;
			//TFTechness2.logger.info(scaleY);
			tile.setTargetTemperature((int) ((1 - scaleY) * MAX_SCALE_TEMP));
			this.mouseDown = false;
		}
		super.mouseReleased(x, y);
	}

	public void addTooltip(List<String> tooltip) {
		if (tile == null) {
			return;
		}
		float temp;
		if (target) {
			temp = tile.getTargetTemperature();
			tooltip.add("Target:");
		} else {
			temp = tile.getTemperature();
		}
		StringUtil.addTemperatureTooltip(tooltip,
				temp,
				tile.getMaxTemperature(),
				tier);

	}

}
