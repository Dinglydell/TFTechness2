package dinglydell.tftechness.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import dinglydell.tftechness.tileentities.TileTFTElectrolyser;

public class ContainerElectrolyser extends Container {

	private TileTFTElectrolyser tile;

	public ContainerElectrolyser(InventoryPlayer player,
			TileTFTElectrolyser tile) {
		this.tile = tile;
		//electrode slots
		addSlotToContainer(new Slot(tile,
				TileTFTElectrolyser.Slots.electrodeA.ordinal(), 155, 9));
		addSlotToContainer(new Slot(tile,
				TileTFTElectrolyser.Slots.electrodeB.ordinal(), 155, 43));

		//redstone
		addSlotToContainer(new Slot(tile,
				TileTFTElectrolyser.Slots.redstone.ordinal(), 49, 43));

		//alumina
		addSlotToContainer(new Slot(tile,
				TileTFTElectrolyser.Slots.alumina.ordinal(), 71, 43));
		//mold
		addSlotToContainer(new Slot(tile,
				TileTFTElectrolyser.Slots.mold.ordinal(), 114, 43));

		//player inventory
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(player, x + y * 9 + 9,
						9 + x * 18, 84 + y * 18));
			}
		}
		//player hotbar
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(player, x, 9 + x * 18, 142));
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return this.tile.isUseableByPlayer(player);
	}

}
