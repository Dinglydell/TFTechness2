package dinglydell.tftechness.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.item.ItemMeta;

public class RemoveBatch {
	private ArrayList<BatchCraftingItem> crafting = new ArrayList<BatchCraftingItem>();

	private Map<ItemMeta, String> oreFix = new HashMap<ItemMeta, String>();

	public void addCrafting(ItemStack output) {
		crafting.add(new BatchCraftingItem(output));

	}

	public void addCrafting(ItemStack output, ItemStack[] inputs) {
		crafting.add(new BatchCraftingItem(output, inputs));
	}

	public void addOreFix(ItemStack toFix, String oreName) {
		oreFix.put(new ItemMeta(toFix), oreName);
	}

	public void Execute() {
		ExecuteCrafting();

	}

	private void ExecuteCrafting() {
		TFTechness2.logger.info("Removing crafting recipes");

		Iterator<IRecipe> iterator = CraftingManager.getInstance()
				.getRecipeList().iterator();
		List<IRecipe> fixedRecipes = new ArrayList<IRecipe>();
		while (iterator.hasNext()) {
			IRecipe recipe = iterator.next();
			if (recipe == null)
				continue;
			ItemStack output = recipe.getRecipeOutput();
			if (output != null) {
				IRecipe fixed = fix(recipe);
				if (fixed != null) {
					fixedRecipes.add(fixed);
				}
				boolean found = false;
				for (int i = 0; i < crafting.size() && !found; i++) {

					found = crafting.get(i).matches(output, recipe);
					if (found) {
						iterator.remove();
						break;
						// TFTechness.logger.info("Removed: " + output.getDisplayName());
					}
				}

			}
		}
		for (IRecipe r : fixedRecipes) {
			GameRegistry.addRecipe(r);
		}

	}

	// a big mess - don't look
	private IRecipe fix(IRecipe recipe) {
		if (recipe instanceof ShapedRecipes) {
			ShapedRecipes shaped = (ShapedRecipes) recipe;
			boolean needsFix = false;
			for (ItemStack is : shaped.recipeItems) {
				if (is != null && oreFix.containsKey(new ItemMeta(is))) {
					needsFix = true;
					break;
				}
			}
			if (!needsFix) {
				return null;
			}

			String[] rows = new String[3];
			Map<Character, Object> chars = new HashMap<Character, Object>();
			for (int j = 0; j < shaped.recipeHeight; j++) {
				StringBuilder col = new StringBuilder();
				for (int i = 0; i < shaped.recipeWidth; i++) {

					ItemStack is = shaped.recipeItems[i + j
							* shaped.recipeWidth];
					if (is == null) {
						col.append(' ');
						continue;
					}
					char c = (char) (65 + i + j * shaped.recipeWidth);
					col.append(c);
					Object o;
					ItemMeta im = new ItemMeta(is);
					if (oreFix.containsKey(im)) {
						o = oreFix.get(im);
					} else {
						o = is;
					}
					chars.put(c, o);
				}
				rows[j] = col.toString();
			}
			List<Object> objRecipe = new ArrayList<Object>();
			for (String row : rows) {
				if (row != null) {
					objRecipe.add(row);
				}
			}
			for (Entry<Character, Object> entry : chars.entrySet()) {
				objRecipe.add(entry.getKey());
				objRecipe.add(entry.getValue());
			}

			return (new ShapedOreRecipe(recipe.getRecipeOutput(),
					objRecipe.toArray()));
		}
		return null;
	}

}
