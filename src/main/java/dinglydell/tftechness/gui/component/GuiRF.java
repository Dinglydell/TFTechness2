package dinglydell.tftechness.gui.component;

import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraftforge.common.util.ForgeDirection;
import dinglydell.tftechness.tileentities.TileTFTMachineBase;

public class GuiRF extends Gui implements ITFTComponent {

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

	}

	public void addTooltip(List<String> tooltip) {

		tooltip.add(tile.getEnergyStored(ForgeDirection.UNKNOWN) + "RF");

	}

	public boolean isHovering(int x, int y) {

		return (x >= this.x && y >= this.y && x < this.x + this.width && y < this.y
				+ this.height);
	}
}
