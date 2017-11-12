package dinglydell.tftechness.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import dinglydell.tftechness.util.OreDict;

public class BatchCraftingItem {
	public ItemStack output;
	public ItemStack[] inputs;

	public BatchCraftingItem(ItemStack out) {
		this.output = out;
	}

	public BatchCraftingItem(ItemStack out, ItemStack[] in) {
		this.output = out;
		this.inputs = in;
	}

	public boolean matches(ItemStack out, IRecipe recipe) {

		boolean match = true;
		if (output != null) {
			match &= OreDict.oresMatch(out, output)
					|| (output.getItemDamage() == OreDictionary.WILDCARD_VALUE && out
							.getItem() == output.getItem());
		} else if (inputs == null) {
			return false;
		}

		if (inputs != null && match) {
			if (recipe instanceof ShapedRecipes) {
				ShapedRecipes shaped = (ShapedRecipes) recipe;

				for (int i = 0; i < inputs.length && match; i++) {
					ItemStack it = inputs[i];
					boolean inputMatch = false;
					for (int j = 0; j < shaped.recipeItems.length
							&& !inputMatch; j++) {
						ItemStack rit = shaped.recipeItems[j];
						inputMatch |= OreDictionary.itemMatches(it, rit, false);
					}
					match &= inputMatch;
				}
			} else if (recipe instanceof ShapelessRecipes) {
				ShapelessRecipes shapeless = (ShapelessRecipes) recipe;
				List<ItemStack> its = (List<ItemStack>) shapeless.recipeItems;

				for (int i = 0; i < inputs.length && match; i++) {
					ItemStack it = inputs[i];
					boolean inputMatch = false;

					for (int j = 0; j < its.size() && !inputMatch; j++) {
						ItemStack rit = its.get(j);
						inputMatch |= OreDictionary.itemMatches(it, rit, false);
					}
					match &= inputMatch;
				}
			}

		}
		return match;
	}
}
