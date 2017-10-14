package dinglydell.tftechness.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDict {
	
	public static boolean oresMatch(ItemStack itemA, ItemStack itemB) {
		if (OreDictionary.itemMatches(itemA, itemB, true)) {
			return true;
		}
		int[] idsA = OreDictionary.getOreIDs(itemA);
		int[] idsB = OreDictionary.getOreIDs(itemB);
		for (int idA : idsA) {
			for (int idB : idsB) {
				if (idA == idB) {
					return true;
				}
			}
		}
		return false;
	}
	
}
