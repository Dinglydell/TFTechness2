package dinglydell.tftechness.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUtil {
	public static ItemStack clone(ItemStack stack, int newSize) {
		ItemStack newStack = stack.copy();
		newStack.stackSize = newSize;
		return newStack;
	}

	public static ItemStack getStack(Object o) {
		if (o instanceof ItemStack) {
			return (ItemStack) o;
		}
		if (o instanceof String) {
			return OreDict.getOreItem((String) o);
		}
		if (o instanceof Item) {
			return new ItemStack((Item) o);
		}
		if (o instanceof Block) {
			return new ItemStack((Block) o);
		}
		return null;
	}
}
