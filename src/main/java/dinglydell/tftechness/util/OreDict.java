package dinglydell.tftechness.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import dinglydell.tftechness.TFTechness2;

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

	public static boolean itemMatches(ItemStack item, String oreName) {
		int[] ids = OreDictionary.getOreIDs(item);
		int targetId = OreDictionary.getOreID(oreName);
		for (int id : ids) {
			if (id == targetId) {
				return true;
			}
		}
		return false;
	}

	/** Returns the first item that exists for this orename */
	public static ItemStack getOreItem(String string) {
		List<ItemStack> items = OreDictionary.getOres(string);
		for (ItemStack it : items) {
			UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(it
					.getItem());
			if (ui.modId.equals(TFTechness2.MODID)) {
				return it;
			}

		}
		return items.get(0);
	}

	/** Returns the list of orenames associated with this stack */
	public static List<String> getOreNames(ItemStack stack) {
		int[] ores = OreDictionary.getOreIDs(stack);
		List<String> names = new ArrayList<String>();

		for (int ore : ores) {
			names.add(OreDictionary.getOreName(ore));
		}

		return names;
	}

	/** Returns the first ore name in common in this list of items */
	public static String getOreName(List<ItemStack> ores) {
		List<String> namesInCommon = new ArrayList<String>();
		for (ItemStack ore : ores) {
			if (namesInCommon.size() == 0) {
				namesInCommon.addAll(getOreNames(ore));
				continue;
			}
			List<String> oreNames = getOreNames(ore);
			Iterator<String> iterator = namesInCommon.iterator();

			while (iterator.hasNext()) {
				String name = iterator.next();
				boolean found = false;
				for (String oreName : oreNames) {
					if (oreName.equals(name)) {
						found = true;
						break;
					}
				}
				if (!found) {
					iterator.remove();
				}
			}
		}
		if (namesInCommon.size() == 0) {
			return null;
		}
		return namesInCommon.get(0);

	}

}
