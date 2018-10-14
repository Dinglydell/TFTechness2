package dinglydell.tftechness.gui;

import net.minecraft.entity.player.InventoryPlayer;
import dinglydell.tftechness.tileentities.TileMachineAnvil;

public class GuiMachineAnvil extends GuiMachine {

	public GuiMachineAnvil(InventoryPlayer player, TileMachineAnvil tile) {
		super(new ContainerMachineAnvil(player, tile), player, tile);
	}

}
