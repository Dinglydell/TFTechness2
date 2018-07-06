package dinglydell.tftechness.gui.component;

import java.util.List;

import net.minecraft.client.gui.GuiButton;

public abstract class GuiButtonTFT extends GuiButton {
	public GuiButtonTFT(int id, int x, int y, int width, int height, String name) {
		super(id, x, y, width, height, name);
	}

	public GuiButtonTFT(int id, int x, int y, String name) {
		super(id, x, y, name);
	}

	public boolean isHovering(int x, int y) {

		return (x >= this.xPosition && y >= this.yPosition
				&& x < this.xPosition + this.width && y < this.yPosition
				+ this.height);
	}

	public abstract void addTooltip(List<String> tooltip);

}
