package dinglydell.tftechness.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.tileentities.TileMachineComponentItemShelf;

public class GuiMachineShelf extends GuiMachine {
	public static final ResourceLocation SHELF_TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/itemShelf.png");

	public GuiMachineShelf(InventoryPlayer player,
			TileMachineComponentItemShelf tile) {

		super(new ContainerMachineShelf(player, tile), player, tile);
	}

	@Override
	protected ResourceLocation getTexture() {
		return SHELF_TEXTURE;
	}
}
