package dinglydell.tftechness;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.energy.DieselHandler;
import blusunrize.immersiveengineering.api.energy.DieselHandler.SqueezerRecipe;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.IERecipes;
import blusunrize.immersiveengineering.common.blocks.stone.BlockStoneDecoration;
import blusunrize.immersiveengineering.common.util.compat.minetweaker.Squeezer;
import blusunrize.immersiveengineering.common.util.compat.opencomputers.SqueezerDriver;

import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.Items.Tools.ItemCustomBucketMilk;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCFluids;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import com.bioxx.tfc.Items.Tools.ItemCustomBucket;
import com.bioxx.tfc.Core.Metal.Alloy;

import dinglydell.tftechness.block.BlockTFTMetalSheet;
import dinglydell.tftechness.block.TFTBlocks;
import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.item.TFTMeta;
import dinglydell.tftechness.metal.Material;
import dinglydell.tftechness.metal.MetalStat;
import dinglydell.tftechness.recipe.RemoveBatch;
import dinglydell.tftechness.util.ItemUtil;

@Mod(modid = TFTechness2.MODID, version = TFTechness2.VERSION, dependencies = "required-after:terrafirmacraft")
public class TFTechness2
{
    public static final String MODID = "TFTechness";
    public static final String VERSION = "0.2";
	public static Map<String, MetalStat> statMap = new HashMap();
    
    public static org.apache.logging.log4j.Logger logger = LogManager
			.getLogger("TFTechness");
	private Material[] materials;
    
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		replaceWater();
		initStatMap();
	}
    
    private void initStatMap() {
    	
    		// Stock TFC
    		// For multiple metals with the same stat entry
    		MetalStat blackSteel = new MetalStat(0.35, 1485, 998);
    		MetalStat blueSteel = new MetalStat(0.35, 1540, 975);
    		MetalStat copper = new MetalStat(0.35, 1080, 996);
    		MetalStat redSteel = new MetalStat(0.35, 1540, 1093);
    		MetalStat steel = new MetalStat(0.35, 1540, 844);

    		statMap.put("Bismuth", new MetalStat(0.14, 270, 1087));
    		statMap.put("BismuthBronze", new MetalStat(0.35, 985, 963));
    		statMap.put("BlackBronze", new MetalStat(0.35, 1070, 1313));
    		statMap.put("BlackSteel", blackSteel);
    		statMap.put("BlueSteel", blueSteel);
    		statMap.put("Brass", new MetalStat(0.35, 930, 976));
    		statMap.put("Bronze", new MetalStat(0.35, 950, 947));
    		statMap.put("Copper", copper);
    		statMap.put("Gold", new MetalStat(0.6, 1060, 2147));
    		statMap.put("HCBlackSteel", blackSteel);
    		statMap.put("HCBlueSteel", blueSteel);
    		statMap.put("HCRedSteel", redSteel);
    		statMap.put("Lead", new MetalStat(0.22, 328, 1260));
    		statMap.put("Nickel", new MetalStat(0.48, 1453, 989));
    		statMap.put("PigIron", new MetalStat(0.35, 1500, 900));
    		statMap.put("Platinum", new MetalStat(0.35, 1730, 2383));
    		statMap.put("RedSteel", redSteel);
    		statMap.put("RoseGold", new MetalStat(0.35, 960, 1859));
    		statMap.put("Silver", new MetalStat(0.48, 961, 1111));
    		statMap.put("Steel", steel);
    		statMap.put("SterlingSilver", new MetalStat(0.35, 900, 1082));
    		statMap.put("Tin", new MetalStat(0.14, 230, 811));
    		// TFC uses copper heat properties for all unknown ingots
    		statMap.put("Unknown", copper);
    		statMap.put("WeakRedSteel", redSteel);
    		statMap.put("WeakBlueSteel", blueSteel);
    		statMap.put("WeakSteel", steel);
    		statMap.put("WroughtIron", new MetalStat(0.35, 1535, 889));
    		statMap.put("Zinc", new MetalStat(0.21, 420, 792));

    		// IE
    		statMap.put("Constantan", new MetalStat(0.39, 1210, 987));


    		// TFT
    		//statMap.put("Billon", new MetalStat(0.35, 950, 1024));
    		statMap.put("Uranium", new MetalStat(0.12, 1132, 2122));
    	
		
	}

	@EventHandler
    public void init(FMLInitializationEvent event)
    {
    	registerItems();
    	registerBlocks();
    }
    
	private void registerBlocks() {
		TFTBlocks.metalSheet = new BlockTFTMetalSheet()
		.setBlockName("MetalSheet").setHardness(80);
		
	}

	private void registerItems() {
		materials = new Material[] {
				new Material("Constantan", 5, Alloy.EnumTier.TierIV, TFTMeta.ieConstantanNugget)
		};
		
		for(Material m : materials){
			m.initialise();
		}
		
		TFTItems.woodenBucketCreosote = new ItemStack(new ItemCustomBucket(Blocks.air).setUnlocalizedName("woodenBucketCreosote"), 0, 1);
		GameRegistry.registerItem(TFTItems.woodenBucketCreosote.getItem(), "woodenBucketCreosote");
		
		FluidContainerRegistry.registerFluidContainer(IEContent.fluidCreosote, TFTItems.woodenBucketCreosote, new ItemStack( TFCItems.woodenBucketEmpty));
		
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		removeRecipes();
		addIEMachineRecipes();
		addIERecipes();
	}
    
    
	private void addIERecipes() {
		GameRegistry.addRecipe(new ShapelessOreRecipe(TFTMeta.ieBlastBrickAdv, TFCBlocks.fireBrick, "plateSteel"));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(TFTMeta.ieBlastBrickAdv, TFCBlocks.fireBrick));
		GameRegistry.addRecipe(ItemUtil.clone(TFTMeta.ieCokeBrick, 2), "PXP", "X X", "PXP", 'P', new ItemStack(TFCItems.fireBrick, 1, 1), 'X', new ItemStack(TFCItems.mortar, 1));
		
	}

	private void addIEMachineRecipes() {
		CokeOvenRecipe.removeRecipes(TFTMeta.ieCoalCoke);
		CokeOvenRecipe.removeRecipes(TFTMeta.ieCoalCokeBlock);
		CokeOvenRecipe.addRecipe(TFTMeta.ieCoalCoke, TFTMeta.bituminousCoal, 1800, 500);
		CokeOvenRecipe.addRecipe(TFTMeta.charcoal, TFCItems.logs, 1800, 100);
		
		
		DieselHandler.addSqueezerRecipe(ItemUtil.clone(TFTMeta.graphite, 4), 19200, null, TFTMeta.hopGraphite);
		
		
	}

	private void removeRecipes() {
		RemoveBatch batch = new RemoveBatch();
		
		batch.addCrafting(TFTMeta.ieCokeBrick);
		batch.addCrafting(TFTMeta.ieBlastBrick);
		batch.addCrafting(TFTMeta.ieIronPlate);
		batch.addCrafting(TFTMeta.ieLeadPlate);
		batch.addCrafting(TFTMeta.ieConstantanPlate);
		batch.addCrafting(TFTMeta.ieSteelPlate);
		batch.addCrafting(TFTMeta.ieAluminiumPlate);
		
		batch.Execute();
		
	}

	private void replaceWater() {

		try {
			Field water = FluidRegistry.class.getDeclaredField("WATER");
			finalField(water);
			water.set(null, TFCFluids.FRESHWATER);

			//Field tag = Fluids.class.getDeclaredField("tag");
			//tag.setAccessible(true);
			//finalField(tag);
			//tag.set(Fluids.WATER, "freshwater");

			// FluidRegistry.WATER = TFCFluids.FRESHWATER;

			// FluidContainerRegistry.isContainer(TFTMeta.salt);
			// Field fluidsField =
			// FluidRegistry.class.getDeclaredField("fluids");
			// fluidsField.setAccessible(true);
			// finalField(fluidsField);
			// BiMap<String, Fluid> fluids = (BiMap<String, Fluid>) fluidsField
			// .get(null);
			// fluids.forcePut("water", TFCFluids.FRESHWATER);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	private void finalField(Field field) throws Exception {
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

	}
}
