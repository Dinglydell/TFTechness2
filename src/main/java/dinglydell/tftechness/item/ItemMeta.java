package dinglydell.tftechness.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMeta {
	public Item item;
	public int meta;
	public ItemStack stack;

	public ItemMeta(Item item, int meta) {
		this(new ItemStack(item, 1, meta));

	}

	public ItemMeta(ItemStack stack) {
		this.item = stack.getItem();
		this.meta = stack.getItemDamage();
		this.stack = stack;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ItemMeta)) {
			return false;
		}
		ItemMeta im = (ItemMeta) obj;
		return im.item == this.item && im.meta == this.meta;
	}

	@Override
	public int hashCode() {

		return 256 * item.hashCode() + meta;
	}

}
