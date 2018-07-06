package dinglydell.tftechness.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.component.GuiButtonTFT;
import dinglydell.tftechness.gui.component.GuiRF;
import dinglydell.tftechness.gui.component.GuiTemperature;
import dinglydell.tftechness.gui.component.ITFTComponent;
import dinglydell.tftechness.tileentities.TileMachineComponent;

public class GuiMachine extends GuiContainer {
	public static final ResourceLocation TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/wall.png");
	private static final int TEX_WIDTH = 175;
	private static final int TEX_HEIGHT = 165;

	private GuiTemperature tempControl;
	protected TileMachineComponent tile;

	protected InventoryPlayer player;

	protected List<ITFTComponent> components = new ArrayList<ITFTComponent>();

	public GuiMachine(ContainerMachine container, InventoryPlayer player,
			TileMachineComponent tile) {
		super(container);
		this.player = player;
		this.tile = tile;
	}

	public GuiMachine(InventoryPlayer player, TileMachineComponent tile) {
		super(new ContainerMachine(player, tile));
		this.tile = tile;
	}

	@Override
	public void initGui() {

		super.initGui();
		buttonList.add(new GuiTemperature(0, guiLeft + 15, guiTop + 8, 7, 49,
				tile, false));
		components.clear();
		components.add(new GuiRF(guiLeft + 7, guiTop + 8, 3, 50, tile));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		mc.getTextureManager().bindTexture(getTexture());

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, TEX_WIDTH, TEX_HEIGHT);
		for (ITFTComponent c : components) {
			c.draw();

		}
	}

	protected ResourceLocation getTexture() {

		return TEXTURE;
	}

	@Override
	public void drawScreen(int x, int y, float p_73863_3_) {
		super.drawScreen(x, y, p_73863_3_);
		List<String> tooltip = new ArrayList<String>();
		for (Object b : buttonList) {
			if (b instanceof GuiButtonTFT) {
				GuiButtonTFT ob = ((GuiButtonTFT) b);
				if (ob.isHovering(x, y)) {
					ob.addTooltip(tooltip);
				}
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
}
