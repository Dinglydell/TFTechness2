package dinglydell.tftechness.block;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dinglydell.tftechness.block.BlockTFTComponent.TFTComponents;
import dinglydell.tftechness.tileentities.TileMachineRF.WireTier;

public class ComponentMaterialRegistry {
	private static Set<ComponentMaterialRegistry> validBaseMaterials = new HashSet<ComponentMaterialRegistry>();

	public static void registerBaseMaterial(String oreName,
			String shelfMaterial, float conductivity, boolean fullRecipe) {
		validBaseMaterials.add(new ComponentMaterialRegistry(oreName,
				shelfMaterial, conductivity, fullRecipe));
	}

	public static void registerBaseMaterial(String oreName, float conductivity,
			boolean fullRecipe) {
		validBaseMaterials.add(new ComponentMaterialRegistry(oreName, null,
				conductivity, fullRecipe));
	}

	public static void registerBaseMaterial(ItemStack item, float conductivity,
			boolean fullRecipe) {
		validBaseMaterials.add(new ComponentMaterialRegistry(item, null,
				conductivity, fullRecipe));
	}

	public static void registerBaseMaterial(ItemStack item,
			ItemStack shelfMaterial, float conductivity, boolean fullRecipe) {
		validBaseMaterials.add(new ComponentMaterialRegistry(item,
				shelfMaterial, conductivity, fullRecipe));
	}

	public static void registerRecipes() {
		for (ComponentMaterialRegistry material : validBaseMaterials) {
			//walls
			ItemStack wall = BlockTFTComponent
					.getBlockWithNBT(TFTComponents.wall,
							material.conductivity,
							WireTier.lv);
			if (material.fullRecipe) {
				GameRegistry.addRecipe(new ShapedOreRecipe(wall, " s ", "sms",
						" s ", 's', "plateSteel", 'm', material.material));
			} else {
				GameRegistry.addRecipe(new ShapelessOreRecipe(wall,
						material.material));
			}

			//heaters
			//TODO: rf levels based on wire coil - better coils more efficient at heating
			for (WireTier tier : WireTier.values()) {
				ItemStack heater = BlockTFTComponent
						.getBlockWithNBT(TFTComponents.heater,
								material.conductivity,
								tier);
				GameRegistry.addRecipe(new ShapedOreRecipe(heater, " c ",
						"cmc", " c ", 'c', tier.getWire(), 'm',
						material.material));
			}

			//shelves
			if (material.shelfMaterial != null) {
				ItemStack shelf = BlockTFTComponent
						.getBlockWithNBT(TFTComponents.shelf,
								material.conductivity,
								WireTier.lv);
				GameRegistry.addRecipe(new ShapedOreRecipe(shelf, "mm", "mm",
						'm', material.shelfMaterial));
			}

			//coolers
			for (WireTier tier : WireTier.values()) {
				ItemStack cooler = BlockTFTComponent
						.getBlockWithNBT(TFTComponents.cooler,
								material.conductivity,
								tier);
				GameRegistry.addRecipe(new ShapedOreRecipe(cooler, "   ",
						"cmc", " c ", 'c', tier.getWire(), 'm',
						material.material));
			}
		}
	}

	private float conductivity;
	private boolean fullRecipe;
	private Object material;
	private Object shelfMaterial;

	public ComponentMaterialRegistry(Object material, Object shelfMaterial,
			float conductivity, boolean fullRecipe) {
		this.material = material;
		this.shelfMaterial = shelfMaterial;
		this.conductivity = conductivity;
		this.fullRecipe = fullRecipe;
	}

}
