package dinglydell.tftechness.gui.component;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;
import dinglydell.tftechness.fluid.FluidStackFloat;
import dinglydell.tftechness.fluid.SolutionTank;
import dinglydell.tftechness.item.ItemMeta;

public class GuiSolutionTank extends Gui implements ITFTComponent {
	private int x;
	private int y;
	private int width;
	private int height;
	private SolutionTank tank;
	private String name;

	public GuiSolutionTank(int x, int y, int width, int height, String name,
			SolutionTank tank) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.width = width;
		this.height = height;
		this.tank = tank;
	}

	@Override
	public void addTooltip(List<String> tooltip) {
		//tooltip.add(name);
		tank.addTooltip(tooltip);

	}

	@Override
	public void draw() {
		int dy = 0;
		TextureManager texture = Minecraft.getMinecraft().getTextureManager();
		//		tank.getSolute(item)
		int dx = 0;
		int iconSepX = 3;
		int iconSepY = 3;
		int iconWidth = 8;
		int iconHeight = 8;
		for (ItemMeta im : tank.getSolids()) {
			float amt = tank.getSolidVolume(im);
			//float total = amt * this.height;
			//int height = (int) (total / tank.getCapacity());
			//int mWidth = (int) ((total % tank.getCapacity())
			//	/ tank.getCapacity() * width);
			int numIcons = (int) (width * height / (iconSepX * iconSepY) * amt / tank
					.getCapacity());
			texture.bindTexture(texture.getResourceLocation(im.item
					.getSpriteNumber()));
			for (int i = 0; i < numIcons; i++) {

				drawTexturedModelRectFromIcon(x + dx - 1,
						y + dy + this.height - iconHeight + 1,
						im.stack.getIconIndex(),
						iconWidth,
						iconHeight);
				dx += iconSepX;
				if (dx + iconWidth / 2 > width) {
					dx = 0;
					dy -= iconSepY;
				}
			}
			//dy -= height;
		}
		for (FluidStackFloat f : tank.getDensitySortedFluids(-1)) {

			int height = (int) (f.amount / (float) tank.getCapacity() * this.height);

			texture.bindTexture(texture.getResourceLocation(f.getFluid()
					.getSpriteNumber()));
			//drawTexturedModalRect(x,
			//		y + dy + this.height - height,
			//		0,
			//		0,
			//		width,
			//		height);
			drawTexturedModelRectFromIcon(x, y + dy + this.height - height, f
					.getFluid().getIcon(f.getStack()), width, height);
			dy -= height;

		}
	}

	@Override
	public boolean isHovering(int x, int y) {

		return (x >= this.x && y >= this.y && x < this.x + this.width && y < this.y
				+ this.height);
	}

}
