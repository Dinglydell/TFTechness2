package dinglydell.tftechness.recipe;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import dinglydell.tftechness.TFTechness2;

public class RemoveBatch {
	private ArrayList<BatchCraftingItem> crafting = new ArrayList<BatchCraftingItem>();

	
	
	public void addCrafting(ItemStack output) {
		crafting.add(new BatchCraftingItem(output));

	}

	public void addCrafting(ItemStack output, ItemStack[] inputs) {
		crafting.add(new BatchCraftingItem(output, inputs));
	}

	public void Execute() {
		ExecuteCrafting();

	}

	private void ExecuteCrafting() {
		TFTechness2.logger.info("Removing crafting recipes");

		Iterator<IRecipe> iterator = CraftingManager.getInstance()
				.getRecipeList().iterator();
		while (iterator.hasNext()) {
			IRecipe recipe = iterator.next();
			if (recipe == null)
				continue;
			ItemStack output = recipe.getRecipeOutput();

			if (output != null) {
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

	}

}
