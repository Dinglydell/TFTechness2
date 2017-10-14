package dinglydell.tftechness.util;

import net.minecraft.item.ItemStack;

public class ItemUtil {
	public static ItemStack clone(ItemStack stack, int newSize){
		ItemStack newStack = stack.copy();
		newStack.stackSize = newSize;
		return newStack;
	}
}
