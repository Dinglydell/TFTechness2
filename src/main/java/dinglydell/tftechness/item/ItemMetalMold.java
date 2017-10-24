package dinglydell.tftechness.item;

import net.minecraft.item.ItemStack;

public class ItemMetalMold extends ItemMetal {

	private ItemStack container;

	public ItemMetalMold(String metal, int amt, String name) {
		super(metal, amt, name);
	}

	public ItemMetalMold setContainerItem(ItemStack stack) {
		container = stack;
		return this;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return container;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return container != null;
	}
}
