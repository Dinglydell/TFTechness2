package dinglydell.tftechness.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.component.GuiSolutionTank;
import dinglydell.tftechness.tileentities.TileMachineComponentTank;

public class GuiMachineTank extends GuiMachine {
	public static final ResourceLocation SHELF_TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/tank.png");

	protected static final String seal = "Seal";
	protected static final String unseal = "Unseal";

	private TileMachineComponentTank tile;

	private GuiButton sealButton;

	public GuiMachineTank(InventoryPlayer player, TileMachineComponentTank tile) {

		super(new ContainerMachineTank(player, tile), player, tile);
		this.tile = tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		components.add(new GuiSolutionTank(guiLeft + 69, guiTop + 8, 57, 51,
				"Solution", tile.getTank()));
		sealButton = new GuiButton(buttonList.size(), width - guiLeft - 46,
				guiTop + 21, 40, 20, "Seal");
		buttonList.add(sealButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button == sealButton) {
			tile.toggleSeal();
		}
	}

	@Override
	public void updateScreen() {

		super.updateScreen();
		sealButton.displayString = tile.isSealed() ? unseal : seal;

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {

		super.drawGuiContainerBackgroundLayer(p_146976_1_,
				p_146976_2_,
				p_146976_3_);
		if (tile.isSealed()) {
			String str = StatCollector
					.translateToLocal("gui.machine.tank.sealed");
			fontRendererObj.drawString(str,
					guiLeft + 97 - fontRendererObj.getStringWidth(str) / 2,
					guiTop + 65,
					0x555555);
			//drawString(fontRendererObj,
			//	StatCollector.translateToLocal("gui.machine.tank.sealed"),
			//guiLeft + 97,
			//guiTop + 65,
			//0x555555);
		}
	}

	@Override
	protected ResourceLocation getTexture() {
		return SHELF_TEXTURE;
	}
}
