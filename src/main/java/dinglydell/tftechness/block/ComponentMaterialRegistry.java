package dinglydell.tftechness.block;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dinglydell.tftechness.block.BlockTFTComponent.TFTComponents;
import dinglydell.tftechness.item.TFTMeta;

public class ComponentMaterialRegistry {
	private static Set<ComponentMaterialRegistry> validBaseMaterials = new HashSet<ComponentMaterialRegistry>();

	public static void registerBaseMaterial(String oreName, float conductivity,
			boolean fullRecipe) {
		validBaseMaterials.add(new ComponentMaterialRegistry(oreName,
				conductivity, fullRecipe));
	}

	public static void registerBaseMaterial(ItemStack item, float conductivity,
			boolean fullRecipe) {
		validBaseMaterials.add(new ComponentMaterialRegistry(item,
				conductivity, fullRecipe));
	}

	public static void registerRecipes() {
		for (ComponentMaterialRegistry material : validBaseMaterials) {
			//walls
			ItemStack wall = BlockTFTComponent
					.getBlockWithNBT(TFTComponents.wall, material.conductivity);
			if (material.fullRecipe) {
				GameRegistry.addRecipe(new ShapedOreRecipe(wall, " s ", "sms",
						" s ", 's', "plateSteel", 'm', material.material));
			} else {
				GameRegistry.addRecipe(new ShapelessOreRecipe(wall,
						material.material));
			}

			//heaters
			//TODO: rf levels based on wire coil - better coils more efficient at heating
			ItemStack heater = BlockTFTComponent
					.getBlockWithNBT(TFTComponents.heater,
							material.conductivity);
			GameRegistry.addRecipe(new ShapedOreRecipe(heater, " c ", "cmc",
					" c ", 'c', TFTMeta.ieLvWire, 'm', material.material));
		}
	}

	private float conductivity;
	private boolean fullRecipe;
	private Object material;

	public ComponentMaterialRegistry(Object material, float conductivity,
			boolean fullRecipe) {
		this.material = material;
		this.conductivity = conductivity;
		this.fullRecipe = fullRecipe;
	}

}
