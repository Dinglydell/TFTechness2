package dinglydell.tftechness.metal;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;

import com.bioxx.tfc.Core.Recipes;
import com.bioxx.tfc.Core.Metal.Alloy;
import com.bioxx.tfc.Core.Metal.AlloyManager;
import com.bioxx.tfc.Core.Metal.MetalRegistry;
import com.bioxx.tfc.Items.ItemIngot;
import com.bioxx.tfc.Items.ItemMeltedMetal;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRaw;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.Metal;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.Crafting.AnvilManager;
import com.bioxx.tfc.api.Crafting.AnvilRecipe;
import com.bioxx.tfc.api.Crafting.AnvilReq;
import com.bioxx.tfc.api.Crafting.CraftingManagerTFC;
import com.bioxx.tfc.api.Enums.EnumSize;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.item.ItemMetal;
import dinglydell.tftechness.item.ItemMetalMold;
import dinglydell.tftechness.item.ItemTFTMetalSheet;
import dinglydell.tftechness.item.TFTItemPropertyRegistry;
import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.item.TFTMeta;
import dinglydell.tftechness.recipe.RemoveBatch;
import dinglydell.tftechness.recipe.TFTAnvilRecipeHandler;
import dinglydell.tftechness.render.RenderItemMetal;
import dinglydell.tftechness.util.StringUtil;

public class Material {
	/** Whether this metal exists in vanilla TFC */
	public boolean isTFC;
	public Item ingot;
	public Item ingot2x;
	public Item sheet;
	public Item sheet2x;
	public Item unshaped;
	public Item rod;
	public Item nuggetMold;
	public ItemStack oldNugget;
	public Item nugget;
	public Block block;
	public int blockMeta;

	public HeatRaw heatRaw;
	public int tier;
	public String name;

	public Metal metal;
	public Alloy.EnumTier alloyTier;
	public String oreName;
	private AlloyIngred[] alloyRecipe;
	public int colour;

	public Material(String name, int tier, boolean isTFC) {
		this(name, tier, Alloy.EnumTier.TierI, isTFC);
	}

	public Material setColour(int colour) {
		this.colour = colour;
		return this;
	}

	//haha! this doesn't exist!
	private Item get(String type) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = TFCItems.class.getDeclaredField(StringUtil
				.lowerCaseFirst(name.replaceAll(" ", "")) + type);

		return (Item) field.get(null);
	}

	public Material(String name, int tier, Alloy.EnumTier alloyTier,
			boolean isTFC) {

		this.alloyTier = alloyTier;
		this.isTFC = isTFC;
		this.heatRaw = TFTechness2.statMap.get(name).heat;
		this.name = name;
		this.oreName = name;
		this.tier = tier;
		//this.nugget = nugget;
		if (isTFC) {
			//no! don't look!
			try {
				sheet = get("Sheet");
				sheet2x = get("Sheet2x");
				ingot = get("Ingot");
				ingot2x = get("Ingot2x");
				unshaped = get("Unshaped");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void initialise(RenderItemMetal render) {
		if (isTFC) {
			this.metal = MetalRegistry.instance.getMetalFromItem(ingot);
		} else {
			addUnshaped();
			addIngots();
			registerMetal();
			addSheets();
		}
		addMolds();
		addNuggets();
		addRod(render);
		registerItemProps();
	}

	private void registerItemProps() {
		TFTItemPropertyRegistry.registerDensity(new ItemStack(ingot),
				TFTechness2.statMap.get(this.name).density);
		if (ingot2x != null) {
			TFTItemPropertyRegistry.registerDensity(new ItemStack(ingot2x),
					TFTechness2.statMap.get(this.name).density * 2);
		}
		if (sheet != null) {
			TFTItemPropertyRegistry.registerDensity(new ItemStack(sheet),
					TFTechness2.statMap.get(this.name).density * 2);
		}
		if (sheet2x != null) {
			TFTItemPropertyRegistry.registerDensity(new ItemStack(sheet2x),
					TFTechness2.statMap.get(this.name).density * 4);
		}

		TFTItemPropertyRegistry.registerDensity(new ItemStack(rod),
				TFTechness2.statMap.get(this.name).density * 0.5f);
		TFTItemPropertyRegistry.registerDensity(new ItemStack(nugget),
				TFTechness2.statMap.get(this.name).density * 0.1f);
		if (unshaped != null) {
			TFTItemPropertyRegistry.registerDensity(new ItemStack(unshaped),
					TFTechness2.statMap.get(this.name).density);
		}

	}

	private void addNuggets() {
		nugget = ((ItemMetal) new ItemMetal(metal.name, 10, "nugget")
				.setUnlocalizedName("metal_nugget" + name))
				.setSize(EnumSize.TINY);

		TFTItems.nuggets.put(name, nugget);
		GameRegistry.registerItem(nugget, name + "Nugget");
		OreDictionary.registerOre("nugget" + oreName, nugget);

		addHeatIndex(nugget, 1, 91);

	}

	private void addMolds() {

		nuggetMold = new ItemMetalMold(metal.name, 100, "nuggetMold")
				.setContainerItem(new ItemStack(TFTItems.nuggetMold, 1, 1))
				.setUnlocalizedName(metal.name + "NuggetMold");
		//		nuggetMold.setContainerItem(TFTItems.nuggetMold);
		GameRegistry.registerItem(nuggetMold, metal.name + "NuggetMold");

	}

	private void registerMetal() {
		metal = new Metal(name, unshaped, ingot);
		TFTMetals.metals.put(name, metal);
		MetalRegistry.instance.addMetal(metal, alloyTier);

	}

	private void addRod(RenderItemMetal render) {

		rod = new ItemMetal(metal.name, 50, "metalRod").setUnlocalizedName(name
				+ "Rod");

		TFTItems.rods.put(name, rod);
		GameRegistry.registerItem(rod, name + "Rod");
		OreDictionary.registerOre("stick" + oreName, rod);

		addHeatIndex(rod, 0, 50);
		if (render != null) {
			//MinecraftForgeClient.registerItemRenderer(rod, render);
		}
	}

	private void addSheets() {
		//single
		sheet = ((ItemTFTMetalSheet) new ItemTFTMetalSheet(name)
				.setUnlocalizedName(name + "Sheet")).setMetal(name, 200);

		TFTItems.sheets.put(name, sheet);
		GameRegistry.registerItem(sheet, name + "Sheet");
		OreDictionary.registerOre("plate" + oreName, sheet);

		addHeatIndex(sheet, 2, 0);

		//double

		sheet2x = ((ItemTFTMetalSheet) new ItemTFTMetalSheet(name)
				.setUnlocalizedName(name + "Sheet2x")).setMetal(name, 400);

		TFTItems.sheets2x.put(name, sheet2x);
		GameRegistry.registerItem(sheet2x, name + "Sheet2x");
		OreDictionary.registerOre("plateDouble" + oreName, sheet2x);

		addHeatIndex(sheet2x, 4, 0);
	}

	private void addIngots() {
		//single
		ingot = new ItemIngot().setUnlocalizedName(name + "Ingot");

		TFTItems.ingots.put(name, ingot);
		GameRegistry.registerItem(ingot, name + "Ingot");
		OreDictionary.registerOre("ingot" + oreName, ingot);

		addHeatIndex(ingot, 1, 0);
		//double
		ingot2x = ((ItemIngot) new ItemIngot().setUnlocalizedName(name
				+ "Ingot2x")).setSize(EnumSize.LARGE).setMetal(name, 200);

		TFTItems.ingots2x.put(name, ingot2x);
		GameRegistry.registerItem(ingot2x, name + "Ingot2x");
		OreDictionary.registerOre("ingotDouble" + oreName, ingot2x);
		addHeatIndex(ingot2x, 2, 0);
	}

	private void addUnshaped() {
		unshaped = new ItemMeltedMetal().setUnlocalizedName(name + "Unshaped");

		TFTItems.unshaped.put(name, unshaped);
		GameRegistry.registerItem(unshaped, "Unshaped" + name);
		addHeatIndex(unshaped, 1, 0);

	}

	private void addHeatIndex(Item item, int quantity, int meta) {
		addHeatIndex(new ItemStack(item, 1), quantity, meta);

	}

	private void addHeatIndex(ItemStack stack, int quantity, int meta) {
		HeatRegistry manager = HeatRegistry.getInstance();
		manager.addIndex(new HeatIndex(stack, TFTechness2.statMap
				.get(this.name).heat, new ItemStack(unshaped, quantity, meta)));

	}

	public Material setOreName(String oreName) {
		this.oreName = oreName;
		return this;
	}

	public Material setAlloyRecipe(AlloyIngred[] alloyIngreds) {
		this.alloyRecipe = alloyIngreds;
		return this;
	}

	public Material setUnshaped(Item unshaped) {
		this.unshaped = unshaped;
		return this;
	}

	public Material setIngot(Item ingot) {
		this.ingot = ingot;
		return this;
	}

	public Material setIngot2x(Item ingot2x) {
		this.ingot2x = ingot2x;
		return this;
	}

	public Material setSheet(Item sheet) {
		this.sheet = sheet;
		return this;
	}

	public Material setSheet2x(Item sheet2x) {
		this.sheet2x = sheet2x;
		return this;
	}

	public Material setBlock(Block block, int meta) {
		this.block = block;
		this.blockMeta = meta;
		return this;
	}

	public Material setNugget(ItemStack nugget) {
		this.oldNugget = nugget;
		return this;
	}

	public void initAlloyRecipe() {
		if (this.alloyRecipe != null) {
			Alloy alloy = new Alloy(metal, alloyTier);
			for (AlloyIngred ing : alloyRecipe) {
				alloy.addIngred(MetalRegistry.instance
						.getMetalFromString(ing.metal), ing.min, ing.max);
			}
			AlloyManager.INSTANCE.addAlloy(alloy);
		}

	}

	public void batchRemove(RemoveBatch batch) {
		if (this.oldNugget != null) {
			batch.addCrafting(this.oldNugget);
		}
		batchRemoveOreDict(batch, "ingot");
		batchRemoveOreDict(batch, "block");
		//if (this.block != null) {
		//	batch.addCrafting(new ItemStack(this.block));
		//}

	}

	private void batchRemoveOreDict(RemoveBatch batch, String orePrefix) {
		List<ItemStack> ores = OreDictionary.getOres(orePrefix + oreName);
		if (name.equals("Copper")) {
			TFTechness2.logger.info("hi");
		}
		for (ItemStack ore : ores) {
			String rl = GameData.getItemRegistry().getNameForObject(ore);
			UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(ore
					.getItem());

			if (ui != null && ui.modId != TFTechness2.MODID
					&& !ui.modId.equals("terrafirmacraft")) {
				batch.addCrafting(ore).setExact(true);
			}
		}

	}

	public void addCraftingRecipes() {
		CraftingManagerTFC.getInstance()
				.addRecipe(new ItemStack(nuggetMold, 1),
						new Object[] { "12",
								'1',
								Recipes.getStackTemp(new ItemStack(unshaped, 1,
										1)),
								'2',
								new ItemStack(TFTItems.nuggetMold, 1, 1) });

		GameRegistry.addShapelessRecipe(new ItemStack(nugget, 10),
				Recipes.getStackNoTemp(new ItemStack(nuggetMold, 1)));

		if (!isTFC) {
			//unshaped.setContainerItem(TFCItems.ceramicMold);
			// unshaped -> ingot
			GameRegistry.addShapelessRecipe(new ItemStack(ingot),
					Recipes.getStackNoTemp(new ItemStack(unshaped)));

			//ingot -> unshaped
			GameRegistry.addShapelessRecipe(new ItemStack(unshaped),
					ingot,
					new ItemStack(TFCItems.ceramicMold, 0, 1));
		}

		//block
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ingot, 9),
				"block" + oreName));

		//TODO: better way of obtaining metal blocks
		List<ItemStack> ores = OreDictionary.getOres("block" + oreName);
		if (ores.size() != 0) {
			ItemStack metalBlock = ores.get(0);
			GameRegistry.addRecipe(new ShapedOreRecipe(metalBlock, "iii",
					"iii", "iii", 'i', "ingot" + oreName));

			addHeatIndex(metalBlock, 9, 0);
		}

	}

	public void addMachineRecipes() {

		MetalPressRecipe.removeRecipes(new ItemStack(sheet));
		//sheet
		MetalPressRecipe.addRecipe(new ItemStack(sheet, 1), new ItemStack(
				ingot, 2), TFTMeta.ieMoldPlate, 2400);
		MetalPressRecipe.addRecipe(new ItemStack(sheet, 1), "ingotDouble"
				+ oreName, TFTMeta.ieMoldPlate, 2400);

		//sheet2x
		MetalPressRecipe.addRecipe(new ItemStack(sheet2x, 1), new ItemStack(
				ingot2x, 2), TFTMeta.ieMoldPlate, 2400);
		MetalPressRecipe.addRecipe(new ItemStack(sheet2x, 1), new ItemStack(
				ingot, 4), TFTMeta.ieMoldPlate, 4800);

		//rod
		//MetalPressRecipe.addRecipe(new ItemStack(rod, 1), "ingot" + metal.name, TFTMeta.ieMoldRod, 2400);
	}

	public void addAnvilRecipes() {
		AnvilManager manager = AnvilManager.getInstance();
		AnvilReq req = AnvilReq.getReqFromInt(tier);
		//rod

		manager.addRecipe(new AnvilRecipe(new ItemStack(ingot), null,
				TFTAnvilRecipeHandler.rodPlan, req, new ItemStack(rod)));

		if (!isTFC) {
			//ingot2x
			manager.addWeldRecipe(new AnvilRecipe(new ItemStack(ingot),
					new ItemStack(ingot), AnvilReq.getReqFromInt(tier - 1),
					new ItemStack(ingot2x, 1)));

			//sheet
			manager.addRecipe(new AnvilRecipe(new ItemStack(ingot2x), null,
					TFTAnvilRecipeHandler.sheetPlan, req, new ItemStack(sheet)));

			//sheet2x
			manager.addWeldRecipe(new AnvilRecipe(new ItemStack(sheet),
					new ItemStack(sheet), req, new ItemStack(sheet2x, 1)));

		}
	}

}
