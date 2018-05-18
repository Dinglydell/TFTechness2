package dinglydell.tftechness.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import dinglydell.tftechness.tileentities.TileMachineComponentItemShelf;

public class ContainerMachineShelf extends ContainerMachine {

	public ContainerMachineShelf(InventoryPlayer player,
			TileMachineComponentItemShelf tile) {
		super(player, tile);
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(tile, i, 62 + i % 3 * 18,
					7 + i / 3 * 18));
		}
	}

}
