package dinglydell.tftechness.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import dinglydell.tftechness.item.TFTItemPropertyRegistry;

public class SlotPowder extends Slot {

	public SlotPowder(IInventory inv, int slot, int x, int y) {
		super(inv, slot, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return TFTItemPropertyRegistry.isPowder(stack);
	}

}
