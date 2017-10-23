package dinglydell.tftechness.gui.component;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.tileentities.TileTFTMachineBase;

public class GuiRF extends Gui implements ITFTComponent {
	public static final ResourceLocation TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/RFIndicator.png");
	private TileTFTMachineBase tile;
	private int height;
	private int width;
	private int y;
	private int x;

	public GuiRF(int x, int y, int width, int height, TileTFTMachineBase tile) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.tile = tile;
	}

	public void draw() {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		//drawTexturedModalRect(0, 0, 0, 0, TEX_WIDTH, TEX_HEIGHT);
		int height = (int) (tile.getEnergyStored(ForgeDirection.UNKNOWN)
				/ (float) tile.getMaxEnergyStored(ForgeDirection.UNKNOWN) * this.height);
		drawTexturedModalRect(this.x,
				this.y + this.height - height,
				0,
				0,
				this.width,
				height);
	}

	public void addTooltip(List<String> tooltip) {

		tooltip.add(tile.getEnergyStored(ForgeDirection.UNKNOWN) + "/"
				+ tile.getMaxEnergyStored(ForgeDirection.UNKNOWN) + "RF");
		tooltip.add(tile.getEnergyConsumptionRate() + "RF/t");

	}

	public boolean isHovering(int x, int y) {

		return (x >= this.x && y >= this.y && x < this.x + this.width && y < this.y
				+ this.height);
	}
}
