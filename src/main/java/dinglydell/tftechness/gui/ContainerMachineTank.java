package dinglydell.tftechness.gui;

import net.minecraft.entity.player.InventoryPlayer;
import dinglydell.tftechness.tileentities.TileMachineComponentTank;

public class ContainerMachineTank extends ContainerMachineInventory {

	public ContainerMachineTank(InventoryPlayer player,
			TileMachineComponentTank tile) {
		super(player, tile);
		addSlotToContainer(new SlotPowder(tile, 0, 46, 23));
	}

}
