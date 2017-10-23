package dinglydell.tftechness.gui.component;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;

import com.bioxx.tfc.api.TFC_ItemHeat;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.tileentities.TileTFTElectrolyser;

public class GuiTemperature extends GuiButton {
	public static final int MAX_SCALE_TEMP = 2000;
	public static final ResourceLocation TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/SliderIndicator.png");
	private static final int TEX_WIDTH = 15;
	private static final int TEX_HEIGHT = 5;
	private TileTFTElectrolyser tile;
	private boolean target;
	private boolean mouseDown = false;

	public GuiTemperature(int id, int x, int y, int width, int height,
			TileTFTElectrolyser tile, boolean target) {
		super(id, x, y, width, height, "");
		this.tile = tile;
		this.target = target;
	}

	public boolean isHovering(int x, int y) {

		return (x >= this.xPosition && y >= this.yPosition
				&& x < this.xPosition + this.width && y < this.yPosition
				+ this.height);
	}

	@Override
	public void drawButton(Minecraft mc, int p_146112_2_, int p_146112_3_) {
		if (target) {
			int wheelMovement = Mouse.getDWheel();
			tile.setTargetTemperature((int) Math.max(0,
					tile.getTargetTemperature() + wheelMovement / 120));
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

	}

	@Override
	public boolean mousePressed(Minecraft p_146116_1_, int p_146116_2_,
			int p_146116_3_) {
		this.mouseDown = true;
		return super.mousePressed(p_146116_1_, p_146116_2_, p_146116_3_);
	}

	@Override
	protected void mouseDragged(Minecraft mc, int x, int y) {
		if (this.mouseDown && this.target) {
			TFTechness2.logger.info(y);
			float scaleY = (y - this.yPosition) / (float) this.height;
			//TFTechness2.logger.info(scaleY);
			tile.setTargetTemperature((int) ((1 - scaleY) * MAX_SCALE_TEMP));

		}

		super.mouseDragged(mc, x, y);
	}

	@Override
	public void mouseReleased(int x, int y) {
		float scaleY = (y - this.yPosition) / (float) this.height;
		//TFTechness2.logger.info(scaleY);
		tile.setTargetTemperature((int) ((1 - scaleY) * MAX_SCALE_TEMP));
		this.mouseDown = false;
		super.mouseReleased(x, y);
	}

	public void addTooltip(List<String> tooltip) {
		float temp;
		if (target) {
			temp = tile.getTargetTemperature();
			tooltip.add("Target:");
		} else {
			temp = tile.getTemperature();
		}
		tooltip.add((Math.round(temp)) + TFTechness2.degrees + "C");
		tooltip.add(TFC_ItemHeat.getHeatColor(temp, Float.MAX_VALUE));

	}

}
