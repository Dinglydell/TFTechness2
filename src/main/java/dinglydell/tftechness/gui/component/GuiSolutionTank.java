package dinglydell.tftechness.gui.component;

import java.util.List;

import net.minecraft.client.gui.Gui;
import dinglydell.tftechness.fluid.SolutionTank;

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
		tooltip.add(name);
		tank.addTooltip(tooltip);

	}

	@Override
	public void draw() {
		//if (tank.getFluid() != null) {
		//	int height = (int) (tank.getFluidAmount()
		//			/ (float) tank.getCapacity() * this.height);
		//	drawTexturedModelRectFromIcon(x, y + this.height - height, tank
		//			.getFluid().getFluid().getIcon(), width, height);
		//}
	}

	@Override
	public boolean isHovering(int x, int y) {

		return (x >= this.x && y >= this.y && x < this.x + this.width && y < this.y
				+ this.height);
	}

}
