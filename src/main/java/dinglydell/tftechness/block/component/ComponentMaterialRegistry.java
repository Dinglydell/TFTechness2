package dinglydell.tftechness.block.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import dinglydell.tftechness.block.component.property.ComponentProperty;

public class ComponentMaterialRegistry {
	//private static Set<ComponentMaterialRegistry<Float>> validBaseMaterials = new HashSet<ComponentMaterialRegistry<Float>>();

	private static Set<ComponentMaterialRegistry> validMaterials = new HashSet<ComponentMaterialRegistry>();

	//public static void registerBaseMaterial(String oreName,
	//		String shelfMaterial, float conductivity, boolean fullRecipe) {
	//	validBaseMaterials.add(new ComponentMaterialRegistry(oreName,
	//			shelfMaterial, conductivity, fullRecipe));
	//}
	//
	//public static void registerBaseMaterial(String oreName, float conductivity,
	//		boolean fullRecipe) {
	//	validBaseMaterials.add(new ComponentMaterialRegistry(oreName, null,
	//			conductivity, fullRecipe));
	//}

	public static ComponentMaterialRegistry registerMaterial(
			ItemStack primaryItem, ItemStack secondaryItem) {
		//if (!validMaterials.containsKey(property)) {
		//	validMaterials.put(property,
		//			new HashSet<ComponentMaterialRegistry>());
		//
		//}
		//validMaterials.get(property)
		//		.add(new ComponentMaterialRegistry(item, null, value, true));
		ComponentMaterialRegistry c = new ComponentMaterialRegistry(
				primaryItem, secondaryItem, true);
		validMaterials.add(c);
		return c;
	}

	public static ComponentMaterialRegistry registerMaterial(
			String primaryItem, ItemStack secondaryItem) {
		ComponentMaterialRegistry c = new ComponentMaterialRegistry(
				primaryItem, secondaryItem, true);
		validMaterials.add(c);
		return c;
	}

	public static ComponentMaterialRegistry registerMaterial(
			ItemStack primaryItem, String secondaryItem) {
		ComponentMaterialRegistry c = new ComponentMaterialRegistry(
				primaryItem, secondaryItem, true);
		validMaterials.add(c);
		return c;
	}

	public static ComponentMaterialRegistry registerMaterial(
			String primaryItem, String secondaryItem) {
		ComponentMaterialRegistry c = new ComponentMaterialRegistry(
				primaryItem, secondaryItem, true);
		validMaterials.add(c);
		return c;
	}

	//public static void registerBaseMaterial(ItemStack item, float conductivity,
	//		boolean fullRecipe) {
	//	validBaseMaterials.add(new ComponentMaterialRegistry(item, null,
	//			conductivity, fullRecipe));
	//}
	//
	//public static void registerBaseMaterial(ItemStack item,
	//		ItemStack shelfMaterial, float conductivity, boolean fullRecipe) {
	//	validBaseMaterials.add(new ComponentMaterialRegistry(item,
	//			shelfMaterial, conductivity, fullRecipe));
	//}

	public static void registerRecipes() {
		for (Component component : Component.components) {
			registerComponentRecipes(component,
					new ArrayList<ComponentMaterialRegistry>(),
					0);
		}

		//for (ComponentMaterialRegistry<Float> material : validBaseMaterials) {
		//	//walls
		//	ItemStack wall = BlockTFTComponent
		//			.getBlockWithNBT(TFTComponents.wall,
		//					material.value,
		//					WireTier.lv);
		//	if (material.fullRecipe) {
		//		GameRegistry.addRecipe(new ShapedOreRecipe(wall, " s ", "sms",
		//				" s ", 's', "plateSteel", 'm', material.material));
		//	} else {
		//		GameRegistry.addRecipe(new ShapelessOreRecipe(wall,
		//				material.material));
		//	}
		//
		//	//heaters
		//	//TODO: rf levels based on wire coil - better coils more efficient at heating
		//	for (WireTier tier : WireTier.values()) {
		//		ItemStack heater = BlockTFTComponent
		//				.getBlockWithNBT(TFTComponents.heater,
		//						material.value,
		//						tier);
		//		GameRegistry.addRecipe(new ShapedOreRecipe(heater, " c ",
		//				"cmc", " c ", 'c', tier.getWire(), 'm',
		//				material.material));
		//	}
		//
		//	//shelves
		//	if (material.shelfMaterial != null) {
		//		ItemStack shelf = BlockTFTComponent
		//				.getBlockWithNBT(TFTComponents.shelf,
		//						material.value,
		//						WireTier.lv);
		//		GameRegistry.addRecipe(new ShapedOreRecipe(shelf, "mm", "mm",
		//				'm', material.shelfMaterial));
		//	}
		//
		//	//coolers
		//	for (WireTier tier : WireTier.values()) {
		//		ItemStack cooler = BlockTFTComponent
		//				.getBlockWithNBT(TFTComponents.cooler,
		//						material.value,
		//						tier);
		//		GameRegistry.addRecipe(new ShapedOreRecipe(cooler, "   ",
		//				"cmc", " c ", 'c', tier.getWire(), 'm',
		//				material.material));
		//	}
		//}
	}

	private static void registerComponentRecipes(Component component,
			List<ComponentMaterialRegistry> materials, int iterationIndex) {
		//build a list of materials for each propset

		//		List<List<ComponentMaterialRegistry>> materials = new ArrayList<List<ComponentMaterialRegistry>>();

		//for (int i = 0; i < component.propertySets.size(); i++){
		//	ComponentProperty[] propSet = component.propertySets.get(i);
		//	List<ComponentMaterialRegistry> setMaterials = new ArrayList<ComponentMaterialRegistry>();
		//	for (ComponentMaterialRegistry material : validMaterials) {
		//		boolean valid = true;
		//		for (ComponentProperty prop : propSet) { // check that the material is valid for these properties
		//			if (!material.validFor.containsKey(prop)) {
		//				valid = false;
		//				break;
		//			}
		//		}
		//		if (valid) {
		//			setMaterials.add(material);
		//		}
		//	}
		//
		//	materials.add(setMaterials);
		//}
		ComponentProperty[] propSet = component.propertySets
				.get(iterationIndex);
		//List<ComponentMaterialRegistry> setMaterials = new ArrayList<ComponentMaterialRegistry>();
		for (ComponentMaterialRegistry material : validMaterials) {
			boolean valid = true;
			for (ComponentProperty prop : propSet) { // check that the material is valid for these properties
				if (!material.validFor.containsKey(prop)) {
					valid = false;
					break;
				}
			}
			if (valid) {
				//setMaterials.add(material);
				//copy list
				List<ComponentMaterialRegistry> mats = new ArrayList<ComponentMaterialRegistry>(
						materials);
				//add my material
				mats.add(material);

				if (iterationIndex == component.propertySets.size() - 1) {//if we are last
					// do registration of recipe
					GameRegistry.addRecipe(component.getRecipe(mats));
				} else {// iterate next part
					registerComponentRecipes(component,
							mats,
							iterationIndex + 1);
				}
			}
		}

		//		materials.add(setMaterials);

	}

	//public T value;
	private boolean fullRecipe;
	public Object material;
	public Object shelfMaterial;
	public Map<ComponentProperty, Object> validFor = new HashMap<ComponentProperty, Object>();

	public ComponentMaterialRegistry(Object material, Object shelfMaterial,
			boolean fullRecipe) {
		this.material = material;
		this.shelfMaterial = shelfMaterial;
		//this.value = value;
		this.fullRecipe = fullRecipe;
	}

	public <T> ComponentMaterialRegistry addProperty(ComponentProperty<T> prop,
			T propValue) {
		validFor.put(prop, propValue);

		return this;

	}

}
