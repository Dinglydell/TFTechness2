package dinglydell.tftechness.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.tileentities.TileMachineElectrode;

public class GuiMachineElectrode extends GuiMachine {
	public static final ResourceLocation ELECTRODE_TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/electrode.png");

	public GuiMachineElectrode(InventoryPlayer player, TileMachineElectrode tile) {
		super(new ContainerMachineElectrode(player, tile), player, tile);
	}

	@Override
	protected ResourceLocation getTexture() {
		return ELECTRODE_TEXTURE;
	}

}
