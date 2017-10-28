package dinglydell.tftechness;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.ItemCrowbar;
import mods.railcraft.common.items.ItemCrowbarReinforced;
import mods.railcraft.common.items.ItemGear.EnumGear;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.items.ItemMagnifyingGlass;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.LogManager;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.MultiblockHandler;
import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.api.energy.DieselHandler;
import blusunrize.immersiveengineering.common.IEContent;

import com.bioxx.tfc.Core.Metal.Alloy;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.Items.Pottery.ItemPotteryMold;
import com.bioxx.tfc.Items.Tools.ItemCustomBucket;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCFluids;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.Crafting.BarrelManager;
import com.bioxx.tfc.api.Crafting.BarrelRecipe;
import com.bioxx.tfc.api.Crafting.CraftingManagerTFC;
import com.bioxx.tfc.api.Crafting.KilnCraftingManager;
import com.bioxx.tfc.api.Crafting.KilnRecipe;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import dinglydell.tftechness.block.BlockCropTFT;
import dinglydell.tftechness.block.BlockMoltenMetal;
import dinglydell.tftechness.block.BlockTFTMachine;
import dinglydell.tftechness.block.BlockTFTMetalSheet;
import dinglydell.tftechness.block.TFTBlocks;
import dinglydell.tftechness.block.TFTOre;
import dinglydell.tftechness.block.TFTOreRegistry;
import dinglydell.tftechness.config.TFTConfig;
import dinglydell.tftechness.crop.CropIndexStack;
import dinglydell.tftechness.crop.TFTCropManager;
import dinglydell.tftechness.event.TFTDamageHandler;
import dinglydell.tftechness.event.TFTEventHandler;
import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.fluid.TFTFluids;
import dinglydell.tftechness.gui.TFTGuiHandler;
import dinglydell.tftechness.item.TFTItemPropertyRegistry;
import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.item.TFTMeta;
import dinglydell.tftechness.metal.AlloyIngred;
import dinglydell.tftechness.metal.Material;
import dinglydell.tftechness.metal.MetalStat;
import dinglydell.tftechness.multiblock.MultiblockElectrolyser;
import dinglydell.tftechness.network.PacketTFTMachine;
import dinglydell.tftechness.network.TFTMachinePacketHandler;
import dinglydell.tftechness.recipe.RemoveBatch;
import dinglydell.tftechness.recipe.SolutionRecipe;
import dinglydell.tftechness.recipe.SolutionRecipe.EnumState;
import dinglydell.tftechness.recipe.TFTAnvilRecipeHandler;
import dinglydell.tftechness.render.RenderBlockTFT;
import dinglydell.tftechness.tileentities.TETFTMetalSheet;
import dinglydell.tftechness.tileentities.TileMoltenMetal;
import dinglydell.tftechness.tileentities.TileTFTElectrolyser;
import dinglydell.tftechness.util.ItemUtil;
import dinglydell.tftechness.util.StringUtil;

@Mod(modid = TFTechness2.MODID, version = TFTechness2.VERSION, dependencies = "required-after:terrafirmacraft;required-after:ImmersiveEngineering")
public class TFTechness2 {
	public static final String MODID = "TFTechness";
	public static final String VERSION = "0.1";
	/** The degree symbol */
	public static final String degrees = "\u00b0";
	public static Map<String, MetalStat> statMap = new HashMap();

	public static org.apache.logging.log4j.Logger logger = LogManager
			.getLogger("TFTechness");
	public static Material[] materials;
	public static TFTechness2 instance;

	public static final int ingotsPerBlock = 10;

	public static SimpleNetworkWrapper snw;

	public TFTechness2() {
		instance = this;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		TFTConfig.loadConifg(event);
		replaceWater();
		editIEMetalRelations();
		initStatMap();
		registerEventHandlers();
		registerPacketHandlers();

		addOres();

	}

	private void addCrops() {
		TFTCropManager.getInstance().registerCrop(new CropIndexStack(
				TFTConfig.HEMP_ID, "hemp", 1, 40, 5, 1, -5, 0.25f, null,
				new int[] { 0, 10, 10 }).setOutput1(TFTMeta.ieHempFibre, 2));

	}

	private void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new TFTEventHandler());

		MinecraftForge.EVENT_BUS.register(new TFTDamageHandler());

		TFTDamageHandler.INSTANCE.registerDamageSources();

	}

	private void addOres() {
		TFTOreRegistry.registerOre(new TFTOre("Bauxite", "veins", "medium", 50,
				new String[] { "gneiss", "basalt", "shale" }, 5, 128, 60, 80));

	}

	private void editIEMetalRelations() {
		IEApi.prefixToIngotMap.put("nugget", new Integer[] { 1, 10 });
		IEApi.prefixToIngotMap.put("plate", new Integer[] { 2, 1 });
		IEApi.prefixToIngotMap.put("block", new Integer[] { 10, 1 });

	}

	private void registerPacketHandlers() {
		snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		snw.registerMessage(TFTMachinePacketHandler.class,
				PacketTFTMachine.class,
				0,
				Side.SERVER);
		snw.registerMessage(TFTMachinePacketHandler.class,
				PacketTFTMachine.class,
				1,
				Side.CLIENT);
	}

	private void initStatMap() {

		// Stock TFC
		// For multiple metals with the same stat entry
		MetalStat blackSteel = new MetalStat(0.35, 1485, 8982);
		MetalStat blueSteel = new MetalStat(0.35, 1540, 8775);
		MetalStat copper = new MetalStat(0.35, 1080, 8960);
		MetalStat redSteel = new MetalStat(0.35, 1540, 9837);
		MetalStat steel = new MetalStat(0.35, 1540, 8000);

		statMap.put("Bismuth", new MetalStat(0.14, 270, 10000));
		statMap.put("BismuthBronze", new MetalStat(0.35, 985, 963));
		statMap.put("BlackBronze", new MetalStat(0.35, 1070, 8626));
		statMap.put("BlackSteel", blackSteel);
		statMap.put("BlueSteel", blueSteel);
		statMap.put("Brass", new MetalStat(0.35, 930, 8500));
		statMap.put("Bronze", new MetalStat(0.35, 950, 8523));
		statMap.put("Copper", copper);
		statMap.put("Gold", new MetalStat(0.6, 1060, 18000));
		statMap.put("HCBlackSteel", blackSteel);
		statMap.put("HCBlueSteel", blueSteel);
		statMap.put("HCRedSteel", redSteel);
		statMap.put("Lead", new MetalStat(0.22, 328, 11000));
		statMap.put("Nickel", new MetalStat(0.48, 1453, 8200));
		statMap.put("PigIron", new MetalStat(0.35, 1500, 8100));
		statMap.put("Platinum", new MetalStat(0.35, 1730, 20000));
		statMap.put("RedSteel", redSteel);
		statMap.put("RoseGold", new MetalStat(0.35, 960, 16731));
		statMap.put("Silver", new MetalStat(0.48, 961, 10000));
		statMap.put("Steel", steel);
		statMap.put("SterlingSilver", new MetalStat(0.35, 900, 9738));
		statMap.put("Tin", new MetalStat(0.14, 230, 7000));
		// TFC uses copper heat properties for all unknown ingots
		statMap.put("Unknown", copper);
		statMap.put("WeakRedSteel", redSteel);
		statMap.put("WeakBlueSteel", blueSteel);
		statMap.put("WeakSteel", steel);
		statMap.put("WroughtIron", new MetalStat(0.35, 1535, 7500));
		statMap.put("Zinc", new MetalStat(0.21, 420, 7000));

		// IE
		statMap.put("Constantan", new MetalStat(0.39, 1210, 9870));
		statMap.put("Electrum", new MetalStat(0.181, 650, 14460));
		//the world shudders in silence as a thousand dictionaries crumble in despair
		statMap.put("Aluminum", new MetalStat(0.91, 660, 2700));

		// TFT
		//statMap.put("Billon", new MetalStat(0.35, 950, 1024));
		statMap.put("Uranium", new MetalStat(0.12, 1132, 19100));
		statMap.put("DullElectrum", new MetalStat(0.181, 650, 14460));

		//non-metals
		statMap.put("Redstone", new MetalStat(1.136, 1000, 3000));
		statMap.put("Alumina", new MetalStat(0.451, 2072, 3900));
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		registerItems();
		addCrops();
		registerFluids();
		registerItemProps();
		registerBlocks();
		registerGui();
		registerRenderers();
		registerIEMultiblocks();
		registerTileEntities();
	}

	private void registerRenderers() {
		RenderingRegistry
				.registerBlockHandler(RenderBlockTFT.renderCrops = RenderingRegistry
						.getNextAvailableRenderId(),
						new RenderBlockTFT());

	}

	private void registerItemProps() {
		ItemStack redstone = new ItemStack(Items.redstone);
		TFTItemPropertyRegistry.registerPowder(redstone);
		ItemStack alumina = new ItemStack(TFTItems.alumina);
		TFTItemPropertyRegistry.registerPowder(alumina);
		FluidMoltenMetal moltRed = TFTFluids.moltenMetal.get("Redstone");
		TFTItemPropertyRegistry.registerSolute(alumina, moltRed, 250);

		TFTItemPropertyRegistry.registerMolten(redstone, moltRed);
		TFTItemPropertyRegistry.registerMolten(alumina,
				TFTFluids.moltenMetal.get("Alumina"));
		TFTItemPropertyRegistry.registerNumMoles(alumina, 250);
		TFTItemPropertyRegistry.registerDensity(alumina, 255);
		TFTItemPropertyRegistry.registerVolume(alumina, 0.085f);
	}

	private void registerFluids() {
		for (Material m : materials) {
			FluidMoltenMetal f = new FluidMoltenMetal(m.name,
					m.heatRaw.meltTemp);
			TFTFluids.moltenMetal.put(m.name, f);
			FluidRegistry.registerFluid(f);

			TFTItemPropertyRegistry.registerMolten(new ItemStack(m.ingot), f);
		}

		//TODO: make this less of an awkward special case
		FluidMoltenMetal redstone = new FluidMoltenMetal("Redstone",
				statMap.get("Redstone").heat.meltTemp);
		TFTFluids.moltenMetal.put("Redstone", redstone);
		FluidRegistry.registerFluid(redstone);

		FluidMoltenMetal alumina = new FluidMoltenMetal("Alumina",
				statMap.get("Alumina").heat.meltTemp);
		TFTFluids.moltenMetal.put("Alumina", alumina);
		FluidRegistry.registerFluid(alumina);
	}

	private void registerGui() {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new TFTGuiHandler());

	}

	private void registerIEMultiblocks() {
		MultiblockHandler.registerMultiblock(MultiblockElectrolyser.instance);

	}

	private void registerTileEntities() {
		GameRegistry.registerTileEntity(TETFTMetalSheet.class, "TFTMetalSheet");

		GameRegistry.registerTileEntity(TileTFTElectrolyser.class,
				"TFTElectrolyser");

		GameRegistry.registerTileEntity(TileMoltenMetal.class, "MoltenMetal");

	}

	private void registerBlocks() {
		TFTBlocks.metalSheet = new BlockTFTMetalSheet()
				.setBlockName("MetalSheet").setHardness(80);

		GameRegistry.registerBlock(TFTBlocks.metalSheet, "MetalSheet");

		TFTBlocks.machine = new BlockTFTMachine().setBlockName("Machine")
				.setHardness(80);

		GameRegistry.registerBlock(TFTBlocks.machine, "Machine");

		for (Material m : materials) {
			BlockMoltenMetal moltenMetal = new BlockMoltenMetal(
					TFTFluids.moltenMetal.get(m.name));
			TFTBlocks.moltenMetal.put(m.name, moltenMetal);

			GameRegistry.registerBlock(moltenMetal,
					moltenMetal.getUnlocalizedName());
		}
		BlockMoltenMetal moltenRedstone = new BlockMoltenMetal(
				TFTFluids.moltenMetal.get("Redstone"));
		TFTBlocks.moltenMetal.put("Redstone", moltenRedstone);
		GameRegistry.registerBlock(moltenRedstone,
				moltenRedstone.getUnlocalizedName());

		TFTOreRegistry.registerAllOreBlocks();

		TFTBlocks.crops = new BlockCropTFT().setBlockName("crops");

		GameRegistry.registerBlock(TFTBlocks.crops,
				TFTBlocks.crops.getUnlocalizedName());

		// pls don't tell
		TFCBlocks.crops = TFTBlocks.crops;
		//TODO: Fix this. this is bad. do not leave this here. find another way
	}

	private void registerItems() {
		materials = new Material[] { new Material("Constantan", 5,
				Alloy.EnumTier.TierIV, false)
				.setNugget(TFTMeta.ieConstantanNugget)
				.setAlloyRecipe(new AlloyIngred[] { new AlloyIngred("Copper",
						50, 60), new AlloyIngred("Nickel", 40, 50) })
				.setBlock(IEContent.blockStorage, 0),
				new Material("DullElectrum", 5, Alloy.EnumTier.TierIV, false)
						.setAlloyRecipe(new AlloyIngred[] { new AlloyIngred(
								"Gold", 50, 60),
								new AlloyIngred("Silver", 40, 50) }),
				new Material("Electrum", 5, Alloy.EnumTier.TierIV, false)
						.setNugget(TFTMeta.ieElectrumNugget),
				//.setAlloyRecipe(new AlloyIngred[] { new AlloyIngred(
				//	"Gold", 50, 60),
				//new AlloyIngred("Silver", 40, 50) }),
				//my eyes honestly hurt
				new Material("Aluminum", 3, Alloy.EnumTier.TierIII, false)
						.setNugget(TFTMeta.ieAluminiumNugget)
						.setOreName("Aluminum"),

				new Material("WroughtIron", 3, Alloy.EnumTier.TierIII, true)
				//.setUnshaped(TFCItems.wroughtIronUnshaped)
				//.setIngot(TFCItems.wroughtIronIngot)
				//.setIngot2x(TFCItems.wroughtIronIngot2x)
				//.setSheet(TFCItems.wroughtIronSheet)
				//.setSheet2x(TFCItems.wroughtIronSheet2x)
						.setNugget(TFTMeta.ieIronNugget).setOreName("Iron"),
				new Material("Lead", 2, true)//.setIngot(TFCItems.leadIngot)
						//.setUnshaped(TFCItems.leadUnshaped)
						//.setSheet(TFCItems.leadSheet)
						.setNugget(TFTMeta.ieLeadNugget),
				new Material("Steel", 4, true)//.setIngot(TFCItems.steelIngot)
						.setNugget(TFTMeta.ieSteelNugget)
						//.setUnshaped(TFCItems.steelUnshaped)
						.setBlock(IEContent.blockStorage, 7),
				new Material("Copper", 1, true)//.setIngot(TFCItems.copperIngot)
						.setNugget(TFTMeta.ieCopperNugget)
				//.setUnshaped(TFCItems.copperUnshaped)
				,
				new Material("Tin", 0, true)//.setIngot(TFCItems.tinIngot)
				//.setUnshaped(TFCItems.tinUnshaped)
				,
				new Material("Bronze", 2, true)//.setIngot(TFCItems.bronzeIngot)
				//.setUnshaped(TFCItems.bronzeUnshaped)
				,
				new Material("Silver", 2, true)//.setIngot(TFCItems.silverIngot)
						.setNugget(TFTMeta.ieSilverNugget),
				//.setUnshaped(TFCItems.silverUnshaped),
				new Material("Nickel", 4, true)//.setIngot(TFCItems.nickelIngot)
						.setNugget(TFTMeta.ieNickelNugget),
				new Material("Gold", 2, true).setNugget(new ItemStack(
						Items.gold_nugget))

		};
		TFTItems.nuggetMold = new ItemPotteryMold()
				.setMetaNames(new String[] { "Clay Nugget Mold",
						"Ceramic Nugget Mold" })
				.setUnlocalizedName("nuggetMold");

		//TFTItems.nuggetMold.setContainerItem(TFTItems.nuggetMold);
		GameRegistry.registerItem(TFTItems.nuggetMold, "nuggetMold");

		for (Material m : materials) {
			m.initialise();
		}

		TFTItems.woodenBucketCreosote = new ItemStack(new ItemCustomBucket(
				Blocks.air).setUnlocalizedName("woodenBucketCreosote"), 1);
		GameRegistry.registerItem(TFTItems.woodenBucketCreosote.getItem(),
				"woodenBucketCreosote");

		FluidContainerRegistry.registerFluidContainer(IEContent.fluidCreosote,
				TFTItems.woodenBucketCreosote,
				new ItemStack(TFCItems.woodenBucketEmpty));

		TFTItems.crushedBauxite = new ItemTerra().setSize(EnumSize.TINY)
				.setWeight(EnumWeight.MEDIUM)
				.setUnlocalizedName("crushedBauxite");
		GameRegistry.registerItem(TFTItems.crushedBauxite, "crushedBauxite");

		TFTItems.alumina = new ItemTerra().setSize(EnumSize.TINY)
				.setWeight(EnumWeight.MEDIUM).setUnlocalizedName("alumina");
		GameRegistry.registerItem(TFTItems.alumina, "alumina");

		TFTItems.basicRailbed = new ItemTerra().setSize(EnumSize.SMALL)
				.setWeight(EnumWeight.LIGHT).setUnlocalizedName("basicRailbed");
		GameRegistry.registerItem(TFTItems.basicRailbed,
				TFTItems.basicRailbed.getUnlocalizedName());

		//TFTItems.seedHemp = new ItemCustomSeeds(TFTConfig.HEMP_ID)
		//	.setUnlocalizedName("hempSeed");

		//GameRegistry.registerItem(TFTItems.seedHemp,
		//	TFTItems.seedHemp.getUnlocalizedName());

		//OreDictionary.registerOre("seedHemp", TFTItems.seedHemp);

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new TFTAnvilRecipeHandler());

		removeRecipes();
		addIEMachineRecipes();
		addIERecipes();
		addRailcraftRecipes();
		addRailcraftMachineRecipes();
		addTFTRecipes();
		tfcAlloyRecipes();
		tfcClayRecipes();
		tfcHeatRecipes();
		tfcKilnRecipes();
		tfcBarrelRecipes();
		for (Material m : materials) {
			m.addMachineRecipes();
			m.addCraftingRecipes();
		}
		logger.info(IEApi.modPreference);
	}

	private void addRailcraftMachineRecipes() {

		removeRollingMachineRecipes();

		RollingMachineCraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(new ItemStack(
						RailcraftItem.rail.item(), 8), "I I", "I I", "I I",
						'I', "ingotIron"));

		RollingMachineCraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(
						ItemUtil.clone(TFTMeta.railAdvanced, 8), "IRG", "IRG",
						"IRG", 'I', TFTMeta.railStandard, 'R', "dustRedstone",
						'G', "ingotGold"));

		//electric rail
		RollingMachineCraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(
						ItemUtil.clone(TFTMeta.railElectric, 6), "RWR", "RWR",
						"RWR", 'R', TFTMeta.railStandard, 'W', TFTMeta.ieMvWire));

		//wire coils
		RollingMachineCraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(ItemUtil.clone(TFTMeta.ieLvWire, 4),
						" m ", "msm", " m ", 'm', "ingotCopper", 's',
						TFTMeta.ieTreatedStick));

		RollingMachineCraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(ItemUtil.clone(TFTMeta.ieMvWire, 4),
						" m ", "msm", " m ", 'm', "ingotElectrum", 's',
						TFTMeta.ieTreatedStick));
		RollingMachineCraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(ItemUtil.clone(TFTMeta.ieHvWire, 4),
						" a ", "msm", " a ", 'm', "ingotSteel", 'a',
						"ingotAluminum", 's', TFTMeta.ieTreatedStick));

	}

	private void removeRollingMachineRecipes() {
		RemoveBatch rollingBatch = new RemoveBatch(
				RollingMachineCraftingManager.getInstance().getRecipeList());

		rollingBatch.addCrafting(TFTMeta.railElectric);

		rollingBatch.Execute();

	}

	private void addRailcraftRecipes() {
		//wooden tie
		for (FluidContainerData c : FluidContainerRegistry
				.getRegisteredFluidContainerData()) {
			if (FluidContainerRegistry.containsFluid(c.filledContainer,
					new FluidStack(IEContent.fluidCreosote, 1000))) {
				GameRegistry.addRecipe(new ShapedOreRecipe(RailcraftItem.tie
						.getStack(), " c ", "www", 'c', c.filledContainer, 'w',
						"woodLumber"));
			}
		}

		//crowbar
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemCrowbar.getItem(),
				"stickIron", "dyeRed"));
		//reinforced crowbar
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemCrowbarReinforced
				.getItem(), "stickSteel", "dyeRed"));

		//cargo cart
		GameRegistry.addRecipe(new ShapelessOreRecipe(EnumCart.CARGO
				.getCartItem(), "chestWood", Items.minecart));

		//TODO: maybe edit the block textures for recipes that have changed as they no longer look like what they're made of
		//detectors - half of them use items unobtainable with tfc
		//these recipes are mostly placeholders
		//tank
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.detectorTank, "bbb",
				"bpb", "bbb", 'b', new ItemStack(TFCItems.stoneBrick, 1,
						OreDictionary.WILDCARD_VALUE), 'p',
				Blocks.stone_pressure_plate));
		//player
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.detectorPlayer,
				"sss", "sps", "sss", 's', "itemRock", 'p',
				Blocks.stone_pressure_plate));
		//mob - placeholder
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.detectorMob, "sss",
				"sps", "sss", 's', "stoneBricks", 'p',
				Blocks.stone_pressure_plate));

		//explosive
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.detectorExplosive,
				"sss", "sps", "sss", 's', "woodLumber", 'p',
				Blocks.stone_pressure_plate));
		//animal
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.detectorAnimal,
				"www", "wpw", "www", 'w', "logWood", 'p',
				Blocks.stone_pressure_plate));
		//age
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.detectorAge, "www",
				"wpw", "www", 'w', "plankTreatedWood", 'p',
				Blocks.stone_pressure_plate));
		//routing
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.detectorRouting,
				"www", "wpw", "www", 'w', "ingotIron", 'p',
				Blocks.stone_pressure_plate));
		//sheep
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.detectorSheep,
				"www", "wpw", "www", 'w', "materialCloth", 'p',
				Blocks.stone_pressure_plate));
		//locomotive
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.detectorLocomotive,
				"www", "wpw", "www", 'w', TFTMeta.ieCokeBrick, 'p',
				Blocks.stone_pressure_plate));
		//train
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.detectorTrain,
				"www", "wpw", "www", 'w', TFCBlocks.fireBrick, 'p',
				Blocks.stone_pressure_plate));

		//adv item loader (requires shovel by default)
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.advItemLoader,
				"srs", "rlr", "srs", 's', "ingotSteel", 'r', "dustRedstone",
				'l', TFTMeta.itemLoader));
		// adv item unloader
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.advItemUnloader,
				"srs", "rlr", "srs", 's', "ingotSteel", 'r', "dustRedstone",
				'l', TFTMeta.itemUnloader));

		//dispenser (used for railcraft things)
		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.dispenser, "ccc",
				"cbc", "crc", 'c', "cobblestone", 'b', TFCItems.bow, 'r',
				"dustRedstone"));

		//engraving bench
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.engravingBench,
				"csb", "sws", "psp", 'c', TFTMeta.ieLvWire, 's', "plateSteel",
				'b', Items.book, 'p', Blocks.piston, 'w', "craftingTableWood"));

		//firebox (solid)
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.fireboxSolid, "bsb",
				"s s", "bsb", 'b', new ItemStack(TFCItems.fireBrick, 1, 1),
				's', "plateSteel"));
		//firebox (liquid)
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.fireboxLiquid,
				"bsb", "sts", "bsb", 'b', new ItemStack(TFCItems.fireBrick, 1,
						1), 's', "plateSteel", 't', TFTMeta.tankGaugeIron));
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.fireboxLiquid,
				"bsb", "sts", "bsb", 'b', new ItemStack(TFCItems.fireBrick, 1,
						1), 's', "plateSteel", 't', TFTMeta.tankGaugeSteel));

		//trackman's goggles
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemGoggles.getItem(),
				"gcg", "s s", "lll", 'g', "paneGlass", 's', "ingotSteel", 'c',
				TFTMeta.circuitReceiver, 'l', TFCItems.leather));
		//electric locomotive
		ItemStack cartStack = EnumCart.LOCO_ELECTRIC.getCartItem();
		//ItemLocomotive.setItemColorData(cartStack,
		//		EnumColor.YELLOW,
		//		EnumColor.BLACK);
		GameRegistry.addRecipe(new ShapedOreRecipe(cartStack, " s ", "scs",
				"gmg", 'g', RailcraftItem.gear.getRecipeObject(EnumGear.STEEL),
				's', "plateSteel", 'c', TFTMeta.ieMvWireBlock, 'm',
				Items.minecart));

		//circuits (will probably change)
		//controller
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.circuitController,
				" c ", "rwr", " d ", 'c', TFTMeta.ieLvWire, 'r',
				"dustRedstone", 'w', "plankTreatedWood", 'd', "blockRedstone"));

		//receiver
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.circuitReceiver,
				" c ", "rwr", " d ", 'c', TFTMeta.ieLvWire, 'r',
				"dustRedstone", 'w', "plankTreatedWood", 'd',
				Blocks.redstone_torch));

		//signal
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.circuitSignal,
				" c ", "rwr", " d ", 'c', TFTMeta.ieLvWire, 'r',
				"dustRedstone", 'w', "plankTreatedWood", 'd', "dustRedstone"));

		// magnifying glass
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemMagnifyingGlass
				.getItem(), " g", "s ", 'g', "paneGlass", 's', "stickIron"));

		//flux transformer
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.fluxTransformer,
				"crc", "rbr", "crc", 'c', "plateCopper", 'r', "dustRedstone",
				'b', TFTMeta.ieMvWireBlock));

		//wooden rail
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemUtil
				.clone(TFTMeta.railWooden, 6), "woodLumber", "itemSaw"));
		// basic railbed
		GameRegistry.addRecipe(new ShapelessOreRecipe(TFTItems.basicRailbed,
				"stickWood", "stickWood", "stickWood", "stickWood"));

		//wooden track
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(EnumTrack.SLOW.getItem(), 32), "r r", "rbr", "r r", 'r',
				TFTMeta.railWooden, 'b', TFTItems.basicRailbed));

		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(EnumTrack.SLOW_BOOSTER.getItem(), 16), "r r", "gbg",
				"rsr", 'r', TFTMeta.railWooden, 'b', TFTItems.basicRailbed,
				'g', "ingotGold", 's', "dustRedstone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(EnumTrack.SLOW_JUNCTION.getItem(), 16), "rrr", "rbr",
				"rrr", 'r', TFTMeta.railWooden, 'b', TFTItems.basicRailbed));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(EnumTrack.SLOW_WYE.getItem(), 16), "rrr", "rrb", "rrr",
				'r', TFTMeta.railWooden, 'b', TFTItems.basicRailbed));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(EnumTrack.SLOW_SWITCH.getItem(), 16), "rbr", "rrr",
				"rrr", 'r', TFTMeta.railWooden, 'b', TFTItems.basicRailbed));

		//boiler tank
		//low pressure
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.boilerTankLow, "m",
				"m", 'm', "plateSteel"));
		//high pressure
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.boilerTankHigh, "m",
				"m", 'm', "plateBlackSteel"));

	}

	private void tfcBarrelRecipes() {
		BarrelManager manager = BarrelManager.getInstance();

		//treated wood
		//there's probably a better way (barrels do not support oredict)
		for (ItemStack plank : OreDictionary.getOres("plankWood")) {
			manager.addRecipe(new BarrelRecipe(plank.copy(), new FluidStack(
					IEContent.fluidCreosote, 100), new ItemStack(
					IEContent.blockTreatedWood), new FluidStack(
					IEContent.fluidCreosote, 100), 4));
		}

		for (ItemStack stick : OreDictionary.getOres("stickWood")) {
			manager.addRecipe(new BarrelRecipe(stick.copy(), new FluidStack(
					IEContent.fluidCreosote, 25), TFTMeta.ieTreatedStick,
					new FluidStack(IEContent.fluidCreosote, 25), 1));
		}

	}

	private void addTFTRecipes() {
		// Alumina (solute) -> Aluminium (l)
		SolutionRecipe.addRecipe(new SolutionRecipe(new ItemStack(
				TFTItems.ingots.get("Aluminum")), 20, EnumState.solute,
				new ItemStack(TFTItems.alumina), 1, SolutionRecipe.electrodes));

		//Alumina (l) -> Aluminium (l)
		SolutionRecipe
				.addRecipe(new SolutionRecipe(new ItemStack(TFTItems.ingots
						.get("Aluminum")), 1, EnumState.liquid, new ItemStack(
						TFTItems.alumina), 17, SolutionRecipe.electrodes));

	}

	private void tfcHeatRecipes() {
		// crushed bauxite -> alumina
		HeatRegistry manager = HeatRegistry.getInstance();
		manager.addIndex(new HeatIndex(
				new ItemStack(TFTItems.crushedBauxite, 1), 1, 1100,
				new ItemStack(TFTItems.alumina, 1)));

		//Alumina
		manager.addIndex(new HeatIndex(new ItemStack(TFTItems.alumina, 1),
				TFTechness2.statMap.get("Alumina").heat));
		//Redstone
		manager.addIndex(new HeatIndex(new ItemStack(Items.redstone, 1),
				TFTechness2.statMap.get("Redstone").heat));
	}

	private void tfcKilnRecipes() {
		KilnCraftingManager manager = KilnCraftingManager.getInstance();

		manager.addRecipe(new KilnRecipe(new ItemStack(TFTItems.nuggetMold, 1,
				0), 0, new ItemStack(TFTItems.nuggetMold, 1, 1)));

	}

	private void tfcClayRecipes() {
		//nugget mold
		CraftingManagerTFC.getInstance().addRecipe(new ItemStack(
				TFTItems.nuggetMold, 2),
				new Object[] { "     ",
						" # # ",
						"  #  ",
						" # # ",
						"     ",
						'#',
						new ItemStack(TFCItems.flatClay, 1, 1) });

	}

	private void tfcAlloyRecipes() {
		for (Material m : materials) {
			m.initAlloyRecipe();
		}

	}

	private void addIERecipes() {

		GameRegistry.addRecipe(new ShapelessOreRecipe(Blocks.hopper,
				TFCBlocks.hopper, "dustRedstone"));

		GameRegistry.addRecipe(new ShapelessOreRecipe(TFTMeta.ieBlastBrickAdv,
				TFCBlocks.fireBrick, "plateSteel"));

		GameRegistry.addRecipe(ItemUtil.clone(TFTMeta.ieCokeBrick, 2),
				"PXP",
				"X X",
				"PXP",
				'P',
				new ItemStack(TFCItems.fireBrick, 1, 1),
				'X',
				new ItemStack(TFCItems.mortar, 1));

		GameRegistry.addRecipe(new ShapelessOreRecipe(TFTMeta.ieHammer,
				TFCItems.steelHammerHead, TFCItems.stick, Items.string));

		// conveyor belt

		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieConveyor, 8), "lll", "wrw", 'l',
				TFCItems.leather, 'w', "ingotIron", 'r', "dustRedstone"));

		//wire coils
		//GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
		//		.clone(TFTMeta.ieLvWire, 4), " m ", "msm", " m ", 'm',
		//		"ingotCopper", 's', TFTMeta.ieTreatedStick));
		//GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
		//		.clone(TFTMeta.ieMvWire, 4), " m ", "msm", " m ", 'm',
		//		"ingotElectrum", 's', TFTMeta.ieTreatedStick));
		//GameRegistry
		//		.addRecipe(new ShapedOreRecipe(ItemUtil.clone(TFTMeta.ieHvWire,
		//				4), " a ", "msm", " a ", 'm', "ingotSteel", 'a',
		//				"ingotAluminum", 's', TFTMeta.ieTreatedStick));
		//GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
		//		.clone(TFTMeta.ieHempWire, 4), " m ", "msm", " m ", 'm',
		//		TFTMeta.ieHempFibre, 's', TFTMeta.ieTreatedStick));
		//GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
		//		.clone(TFTMeta.ieSteelWire, 4), " m ", "msm", " m ", 'm',
		//		"ingotSteel", 's', TFTMeta.ieTreatedStick));
		//
		//engineering blocks
		GameRegistry.addRecipe((new ShapedOreRecipe(TFTMeta.ieLightEngineering,
				"scs", "psp", "scs", 's', "ingotSteel", 'c',
				TFTMeta.ieComponentSteel, 'p', Blocks.piston)));

		GameRegistry.addRecipe((new ShapedOreRecipe(TFTMeta.ieHeavyEngineering,
				"scs", "psp", "scs", 's', "ingotBlackSteel", 'c',
				TFTMeta.ieComponentSteel, 'p', Blocks.piston)));

		//fluid pump
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.ieFluidPump, " w ",
				"mcm", "ppp", 'm', "ingotIron", 'c', TFTMeta.ieComponentIron,
				'p', TFTMeta.ieFluidPipe, 'w', TFTMeta.ieLvWire));

		//Industrial Squeezer
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieSqueezer, 2), "mwm", "cwc", "mgm", 'm',
				"ingotIron", 'c', TFTMeta.ieComponentIron, 'p', Blocks.piston,
				'w', TFTMeta.ieLvWire, 'g', "dyeGreen"));

		//Fermenter
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieFermenter, 2), "mwm", "cwc", "mgm", 'm',
				"ingotIron", 'c', TFTMeta.ieComponentIron, 'p', Blocks.piston,
				'w', TFTMeta.ieLvWire, 'g', "dyeBlue"));

		//broken recipes (recipes with things that are unobtainable with TFC)

		//LV wire connector
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.ieLvConnector,
				"wmw", " m ", "wmw", 'w', "plankTreatedWood", 'm',
				"ingotCopper"));

		//MV wire connector
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.ieMvConnector,
				"wmw", " m ", "wmw", 'w', "plankTreatedWood", 'm',
				"ingotElectrum"));

		//HV wire connector
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.ieHvConnector,
				"wmw", " m ", "wmw", 'w', "plankTreatedWood", 'm',
				"ingotAluminum"));

		//breaker switch
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.ieBreakerSwitch,
				" l ", "wcw", 'w', "plankTreatedWood", 'c', "ingotCopper", 'l',
				Blocks.lever));

		//powered lantern
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.iePoweredLantern,
				"wgw", "gcg", "iri", 'w', "plankTreatedWood", 'c',
				TFTMeta.ieLvWire, 'g', "paneGlass", 'i', "ingotIron", 'r',
				"dustRedstone"));

		//current transformer
		GameRegistry.addRecipe(new ShapedOreRecipe(
				TFTMeta.ieCurrentTransformer, " v ", "wcw", "ici", 'w',
				"plankTreatedWood", 'c', TFTMeta.ieLvWireBlock, 'v',
				TFTMeta.ieVoltmeter, 'i', "ingotIron", 'r', "dustRedstone"));

		//radiator block
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieRadiatorBlock, 2), "scs", "cbc", "scs", 's',
				"ingotSteel", 'c', "ingotCopper", 'b',
				TFCItems.redSteelBucketWater));

		//balloon
		GameRegistry.addRecipe(new ShapedOreRecipe(IEContent.blockClothDevice,
				" f ", "ftf", " w ", 'f', TFTMeta.ieToughFabric, 't',
				"blockTorch", 'w', "slabTreatedWood"));

		//jerry can
		GameRegistry.addRecipe(new ShapedOreRecipe(IEContent.itemJerrycan,
				" mm", "mbr", "mrb", 'm', "ingotIron", 'r',
				TFCItems.redSteelBucketEmpty, 'b',
				TFCItems.blueSteelBucketEmpty));
		GameRegistry.addRecipe(new ShapedOreRecipe(IEContent.itemJerrycan,
				" mm", "mrb", "mbr", 'm', "ingotIron", 'r',
				TFCItems.redSteelBucketEmpty, 'b',
				TFCItems.blueSteelBucketEmpty));

		//chemical thrower

		GameRegistry.addRecipe(new ShapedOreRecipe(IEContent.itemChemthrower,
				" ag", " hg", "prb", 'a', TFTMeta.ieAirTank, 'g',
				TFTMeta.ieWoodenGrip, 'h', TFTMeta.ieHeavyEngineering, 'r',
				TFCItems.redSteelBucketEmpty, 'b',
				TFCItems.blueSteelBucketEmpty, 'p', TFTMeta.ieFluidPipe));
	}

	private void addIEMachineRecipes() {
		CokeOvenRecipe.removeRecipes(TFTMeta.ieCoalCoke);
		CokeOvenRecipe.removeRecipes(TFTMeta.ieCoalCokeBlock);
		CokeOvenRecipe.addRecipe(TFTMeta.ieCoalCoke,
				TFTMeta.bituminousCoal,
				1800,
				500);
		CokeOvenRecipe.removeRecipes(new ItemStack(Items.coal, 1, 1));
		CokeOvenRecipe.addRecipe(TFTMeta.charcoal, "logWood", 1800, 100);

		MetalPressRecipe.removeRecipes(TFTMeta.ieIronPlate);
		MetalPressRecipe.removeRecipes(TFTMeta.ieLeadPlate);
		MetalPressRecipe.removeRecipes(TFTMeta.ieConstantanPlate);
		MetalPressRecipe.removeRecipes(TFTMeta.ieSteelPlate);
		MetalPressRecipe.removeRecipes(TFTMeta.ieAluminiumPlate);
		MetalPressRecipe.removeRecipes(TFTMeta.ieIronRod);
		MetalPressRecipe.removeRecipes(TFTMeta.ieSteelRod);
		MetalPressRecipe.removeRecipes(TFTMeta.ieAluminiumRod);

		// blast furnace
		BlastFurnaceRecipe.removeRecipes(TFTMeta.ieSteelIngot);
		BlastFurnaceRecipe.addRecipe(new ItemStack(TFCItems.pigIronIngot),
				"ingotIron",
				1200,
				TFTMeta.ieSlag);

		//arc furnace
		ArcFurnaceRecipe.removeRecipes(TFTMeta.ieSteelIngot);
		ArcFurnaceRecipe.addRecipe(new ItemStack(TFCItems.steelIngot),
				"ingotIron",
				TFTMeta.ieSlag,
				400,
				512,
				"dustCoke");

		DieselHandler.addSqueezerRecipe(ItemUtil.clone(TFTMeta.graphite, 4),
				180,
				null,
				TFTMeta.hopGraphite);

		for (ItemStack seed : TFTMeta.seeds) {
			DieselHandler.addSqueezerRecipe(seed, 80, new FluidStack(
					IEContent.fluidPlantoil, 80), null);
		}

		//electrum processing
		int electrumTime = 200;
		int electrumRf = 512;

		ArcFurnaceRecipe.addRecipe(new ItemStack(TFTItems.ingots
				.get("Electrum")),
				"ingotDullElectrum",
				null,
				electrumTime,
				electrumRf,
				"dustRedstone");
		ArcFurnaceRecipe.addRecipe(new ItemStack(TFTItems.ingots2x
				.get("Electrum")),
				"ingotDoubleDullElectrum",
				null,
				electrumTime * 2,
				electrumRf,
				"dustRedstone",
				"dustRedstone");
		ArcFurnaceRecipe.addRecipe(new ItemStack(TFTItems.sheets
				.get("Electrum")),
				"plateDullElectrum",
				null,
				electrumTime * 2,
				electrumRf,
				"dustRedstone",
				"dustRedstone");
		ArcFurnaceRecipe.addRecipe(new ItemStack(TFTItems.sheets2x
				.get("Electrum")),
				"plateDoubleDullElectrum",
				null,
				electrumTime * 4,
				electrumRf,
				"dustRedstone",
				"dustRedstone",
				"dustRedstone",
				"dustRedstone");

		//Aluminium refinement - stage 1
		CrusherRecipe.addRecipe(new ItemStack(TFTItems.crushedBauxite, 5),
				"oreBauxite",
				3600);
		CrusherRecipe.addRecipe(new ItemStack(TFTItems.crushedBauxite, 3),
				"orePoorBauxite",
				3200);
		CrusherRecipe.addRecipe(new ItemStack(TFTItems.crushedBauxite, 7),
				"oreRichBauxite",
				4000);

	}

	private void removeRecipes() {
		RemoveBatch batch = new RemoveBatch(CraftingManager.getInstance()
				.getRecipeList());

		for (Material m : materials) {
			m.batchRemove(batch);
		}

		batch.addCrafting(TFTMeta.ieCokeBrick);
		batch.addCrafting(TFTMeta.ieBlastBrick);
		batch.addCrafting(TFTMeta.ieIronPlate);
		batch.addCrafting(TFTMeta.ieLeadPlate);
		batch.addCrafting(TFTMeta.ieConstantanPlate);
		batch.addCrafting(TFTMeta.ieSteelPlate);
		batch.addCrafting(TFTMeta.ieAluminiumPlate);
		batch.addCrafting(TFTMeta.ieIronRod);
		batch.addCrafting(TFTMeta.ieSteelRod);
		batch.addCrafting(TFTMeta.ieAluminiumRod);

		batch.addCrafting(TFTMeta.ieHammer);

		batch.addCrafting(TFTMeta.ieLvWire);
		batch.addCrafting(TFTMeta.ieMvWire);
		batch.addCrafting(TFTMeta.ieHvWire);
		batch.addCrafting(TFTMeta.ieHempWire);
		batch.addCrafting(TFTMeta.ieSteelWire);

		batch.addCrafting(new ItemStack(IEContent.blockTreatedWood));
		batch.addCrafting(TFTMeta.ieTreatedStick);

		batch.addCrafting(ItemCrowbar.getItem());
		batch.addCrafting(ItemCrowbarReinforced.getItem());

		batch.addCrafting(new ItemStack(Blocks.rail),
				new ItemStack[] { new ItemStack(TFCItems.wroughtIronIngot) });

		batch.addCrafting(new ItemStack(Blocks.golden_rail),
				new ItemStack[] { new ItemStack(TFCItems.goldIngot) });
		for (EnumPlate plate : EnumPlate.values()) {
			batch.addOreFix(new ItemStack(RailcraftItem.plate.item(), 1, plate
					.ordinal()),
					"plate"
							+ StringUtil.capitaliseFirst((plate.name()
									.toLowerCase())));
		}

		batch.addOreFix(new ItemStack(Items.gold_ingot), "ingotGold");
		batch.addOreFix(new ItemStack(Items.iron_ingot), "ingotIron");

		batch.addCrafting(ItemMagnifyingGlass.getItem());

		batch.addCrafting(TFTMeta.fluxTransformer);

		batch.addCrafting(TFTMeta.railWooden);

		batch.addCrafting(TFTMeta.ieLightEngineering);
		batch.addCrafting(TFTMeta.ieHeavyEngineering);

		batch.addCrafting(TFTMeta.ieFluidPump);

		batch.addCrafting(TFTMeta.boilerTankLow);
		batch.addCrafting(TFTMeta.boilerTankHigh);

		batch.addCrafting(TFTMeta.ieSqueezer);

		batch.Execute();

	}

	private void replaceWater() {

		try {
			Field water = FluidRegistry.class.getDeclaredField("WATER");
			finalField(water);
			water.set(null, TFCFluids.FRESHWATER);

			Field tag = Fluids.class.getDeclaredField("tag");
			tag.setAccessible(true);
			finalField(tag);
			tag.set(Fluids.WATER, "freshwater");

			Field waterBlock = Blocks.class.getDeclaredField("water");
			finalField(waterBlock);
			waterBlock.set(null, TFCBlocks.freshWaterStationary);

			//Field fluidsField = FluidRegistry.class.getDeclaredField("fluids");
			//fluidsField.setAccessible(true);
			//finalField(fluidsField);
			//BiMap<String, Fluid> fluids = (BiMap<String, Fluid>) fluidsField
			//		.get(null);
			//fluids.forcePut("water", TFCFluids.FRESHWATER);

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
