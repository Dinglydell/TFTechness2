package dinglydell.tftechness.metal;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;

import com.bioxx.tfc.Core.Recipes;
import com.bioxx.tfc.Core.Metal.Alloy;
import com.bioxx.tfc.Core.Metal.AlloyManager;
import com.bioxx.tfc.Core.Metal.MetalRegistry;
import com.bioxx.tfc.Items.ItemIngot;
import com.bioxx.tfc.Items.ItemMeltedMetal;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.HeatRaw;
import com.bioxx.tfc.api.Metal;
import com.bioxx.tfc.api.Crafting.AnvilManager;
import com.bioxx.tfc.api.Crafting.AnvilRecipe;
import com.bioxx.tfc.api.Crafting.AnvilReq;
import com.bioxx.tfc.api.Crafting.CraftingManagerTFC;
import com.bioxx.tfc.api.Enums.EnumSize;

import cpw.mods.fml.common.registry.GameRegistry;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.item.ItemRod;
import dinglydell.tftechness.item.ItemTFTMetalSheet;
import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.item.TFTMeta;
import dinglydell.tftechness.recipe.RemoveBatch;
import dinglydell.tftechness.recipe.TFTAnvilRecipeHandler;
import dinglydell.tftechness.util.ItemUtil;

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
	public ItemStack nugget;
	public Block block;

	public HeatRaw heatRaw;
	public int tier;
	public String name;
	public Metal metal;
	public Alloy.EnumTier alloyTier;
	public String oreName;
	private AlloyIngred[] alloyRecipe;

	public Material(String name, int tier, boolean isTFC) {
		this.isTFC = isTFC;
		this.heatRaw = TFTechness2.statMap.get(name).heat;
		this.name = name;
		this.oreName = name;
		this.tier = tier;
		//this.nugget = nugget;
	}

	public Material(String name, int tier, Alloy.EnumTier alloyTier,
			boolean isTFC) {
		this(name, tier, isTFC);
		this.alloyTier = alloyTier;
	}

	public void initialise() {
		if (isTFC) {
			this.metal = MetalRegistry.instance.getMetalFromItem(ingot);
		} else {
			addUnshaped();
			addIngots();
			registerMetal();
			addSheets();
		}
		addMolds();
		addRod();
	}

	private void addMolds() {
		nuggetMold = new ItemTerra().setUnlocalizedName(metal.name
				+ "NuggetMold");
		nuggetMold.setContainerItem(TFTItems.nuggetMold);
		GameRegistry.registerItem(nuggetMold, metal.name + "NuggetMold");

	}

	private void registerMetal() {
		metal = new Metal(name, unshaped, ingot);
		TFTMetals.metals.put(name, metal);
		MetalRegistry.instance.addMetal(metal, alloyTier);

	}

	private void addRod() {
		rod = new ItemRod(metal.name);

		TFTItems.rods.put(name, rod);
		GameRegistry.registerItem(rod, name + "Rod");
		OreDictionary.registerOre("rod" + name, rod);

	}

	private void addSheets() {
		//single
		sheet = ((ItemTFTMetalSheet) new ItemTFTMetalSheet(name)
				.setUnlocalizedName(name + "Sheet")).setMetal(name, 200);

		TFTItems.sheets.put(name, sheet);
		GameRegistry.registerItem(sheet, name + "Sheet");
		OreDictionary.registerOre("plate" + name, sheet);

		sheet2x = ((ItemTFTMetalSheet) new ItemTFTMetalSheet(name)
				.setUnlocalizedName(name + "Sheet2x")).setMetal(name, 400);

		//double
		TFTItems.sheets2x.put(name, sheet2x);
		GameRegistry.registerItem(sheet2x, name + "Sheet2x");
		OreDictionary.registerOre("plateDouble" + name, sheet2x);

	}

	private void addIngots() {
		//single
		ingot = new ItemIngot().setUnlocalizedName(name + "Ingot");

		TFTItems.ingots.put(name, ingot);
		GameRegistry.registerItem(ingot, name + "Ingot");
		OreDictionary.registerOre("ingot" + name, ingot);

		//double
		ingot2x = ((ItemIngot) new ItemIngot().setUnlocalizedName(name
				+ "Ingot2x")).setSize(EnumSize.LARGE).setMetal(name, 200);

		TFTItems.ingots2x.put(name, ingot2x);
		GameRegistry.registerItem(ingot2x, name + "Ingot2x");
		OreDictionary.registerOre("ingotDouble" + name, ingot2x);
	}

	private void addUnshaped() {
		unshaped = new ItemMeltedMetal().setUnlocalizedName(name + "Unshaped");

		TFTItems.unshaped.put(name, unshaped);
		GameRegistry.registerItem(unshaped, "Unshaped" + name);

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

	public Material setBlock(Block block) {
		this.block = block;
		return this;
	}

	public Material setNugget(ItemStack nugget) {
		this.nugget = nugget;
		return this;
	}

	public void initAlloyRecipe() {
		if (this.alloyRecipe != null) {
			Alloy alloy = new Alloy(metal, alloyTier);
			for (AlloyIngred ing : alloyRecipe) {
				alloy.addIngred(ing.metal, ing.min, ing.max);
			}
			AlloyManager.INSTANCE.addAlloy(alloy);
		}

	}

	public void batchRemove(RemoveBatch batch) {
		if (this.nugget != null) {
			batch.addCrafting(this.nugget);
		}
		if (this.block != null) {
			batch.addCrafting(new ItemStack(this.block));
		}

	}

	public void addMoldRecipes() {
		CraftingManagerTFC.getInstance()
				.addRecipe(new ItemStack(nuggetMold, 1),
						new Object[] { "12",
								'1',
								Recipes.getStackTemp(new ItemStack(unshaped, 1,
										1)),
								'2',
								new ItemStack(TFTItems.nuggetMold, 1, 1) });

		GameRegistry.addShapelessRecipe(ItemUtil.clone(nugget, 10),
				Recipes.getStackNoTemp(new ItemStack(nuggetMold, 1)));
	}

	public void addMachineRecipes() {
		//sheet
		MetalPressRecipe.addRecipe(new ItemStack(sheet, 1), new ItemStack(
				ingot, 2), TFTMeta.ieMoldPlate, 2400);
		MetalPressRecipe.addRecipe(new ItemStack(sheet, 1), "ingotDouble"
				+ metal.name, TFTMeta.ieMoldPlate, 2400);

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
