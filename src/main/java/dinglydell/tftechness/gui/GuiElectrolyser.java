package dinglydell.tftechness.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.component.GuiRF;
import dinglydell.tftechness.gui.component.GuiSolutionTank;
import dinglydell.tftechness.gui.component.GuiTank;
import dinglydell.tftechness.gui.component.GuiTemperature;
import dinglydell.tftechness.gui.component.ITFTComponent;
import dinglydell.tftechness.tileentities.TileTFTElectrolyser;

public class GuiElectrolyser extends GuiContainer {

	public static final ResourceLocation TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/electrolyser.png");
	private static final int TEX_WIDTH = 175;
	private static final int TEX_HEIGHT = 165;

	private GuiTemperature tempControl;
	private TileTFTElectrolyser tile;

	protected List<ITFTComponent> components = new ArrayList<ITFTComponent>();

	public GuiElectrolyser(InventoryPlayer player, TileTFTElectrolyser tile) {
		super(new ContainerElectrolyser(player, tile));
		this.tile = tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (width - TEX_WIDTH) / 2;
		guiTop = (height - TEX_HEIGHT) / 2;
		//	buttonList.add(new GuiTemperature(0, guiLeft + 15, guiTop + 8, 7, 49,
		//			tile, true));
		//	buttonList.add(new GuiTemperature(0, guiLeft + 28, guiTop + 8, 7, 49,
		//			tile, false));

		components.clear();
		components.add(new GuiRF(guiLeft + 7, guiTop + 8, 3, 50, tile));
		components.add(new GuiSolutionTank(guiLeft + 69, guiTop + 9, 34, 48,
				"Solution", tile.getCryoliteTank()));
		components.add(new GuiTank(guiLeft + 112, guiTop + 9, 16, 48,
				"Product", tile.getAluminiumTank()));

	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int x, int y, float p_73863_3_) {
		super.drawScreen(x, y, p_73863_3_);
		List<String> tooltip = new ArrayList<String>();
		for (Object b : buttonList) {
			GuiTemperature ob = ((GuiTemperature) b);
			if (ob.isHovering(x, y)) {
				ob.addTooltip(tooltip);
			}
		}
		for (ITFTComponent c : components) {
			//c.draw();
			if (c.isHovering(x, y)) {
				c.addTooltip(tooltip);
			}
		}
		this.drawHoveringText(tooltip, x, y, fontRendererObj);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {

		mc.getTextureManager().bindTexture(TEXTURE);

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, TEX_WIDTH, TEX_HEIGHT);
		for (ITFTComponent c : components) {
			c.draw();
			//if (c.isHovering(x, y)) {
			//c.addTooltip(tooltip);
			//}
		}
	}

	@Override
	public void handleMouseInput() {

		super.handleMouseInput();

	}

	@Override
	protected void mouseClicked(int x, int y, int type) {

		super.mouseClicked(x, y, type);
		//if(tempControl.xPosition < x && tempControl.)
	}

}
