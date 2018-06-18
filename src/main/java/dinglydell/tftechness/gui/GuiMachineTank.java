package dinglydell.tftechness.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.component.GuiSolutionTank;
import dinglydell.tftechness.tileentities.TileMachineComponentTank;

public class GuiMachineTank extends GuiMachine {
	public static final ResourceLocation SHELF_TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/tank.png");

	private TileMachineComponentTank tile;

	public GuiMachineTank(InventoryPlayer player, TileMachineComponentTank tile) {

		super(new ContainerMachineTank(player, tile), player, tile);
		this.tile = tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		components.add(new GuiSolutionTank(guiLeft + 69, guiTop + 7, 59, 50,
				"Solution", tile.getTank()));
	}

	@Override
	protected ResourceLocation getTexture() {
		return SHELF_TEXTURE;
	}
}
