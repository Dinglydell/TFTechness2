package dinglydell.tftechness;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.LogManager;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.MultiblockHandler;
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
import com.bioxx.tfc.api.Crafting.CraftingManagerTFC;
import com.bioxx.tfc.api.Crafting.KilnCraftingManager;
import com.bioxx.tfc.api.Crafting.KilnRecipe;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import dinglydell.tftechness.block.BlockMoltenMetal;
import dinglydell.tftechness.block.BlockTFTMachine;
import dinglydell.tftechness.block.BlockTFTMetalSheet;
import dinglydell.tftechness.block.TFTBlocks;
import dinglydell.tftechness.block.TFTOre;
import dinglydell.tftechness.block.TFTOreRegistry;
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
import dinglydell.tftechness.tileentities.TETFTMetalSheet;
import dinglydell.tftechness.tileentities.TileMoltenMetal;
import dinglydell.tftechness.tileentities.TileTFTElectrolyser;
import dinglydell.tftechness.util.ItemUtil;

@Mod(modid = TFTechness2.MODID, version = TFTechness2.VERSION, dependencies = "required-after:terrafirmacraft;required-after:ImmersiveEngineering")
public class TFTechness2 {
	public static final String MODID = "TFTechness";
	public static final String VERSION = "0.2";
	/** The degree symbol */
	public static final String degrees = "\u00b0";
	public static Map<String, MetalStat> statMap = new HashMap();

	public static org.apache.logging.log4j.Logger logger = LogManager
			.getLogger("TFTechness");
	public static Material[] materials;
	public static TFTechness2 instance;

	public static final int ingotsPerBlock = 10;

	public static SimpleNetworkWrapper snw;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		instance = this;
		replaceWater();
		editIEMetalRelations();
		initStatMap();
		registerEventHandlers();
		registerPacketHandlers();
		addOres();
	}

	private void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new TFTEventHandler());

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

		//non-metals
		statMap.put("Redstone", new MetalStat(1.136, 1000, 3000));
		statMap.put("Alumina", new MetalStat(0.451, 2072, 3900));
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		registerItems();

		registerFluids();
		registerItemProps();
		registerBlocks();
		registerGui();
		registerIEMultiblocks();
		registerTileEntities();
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
			FluidMoltenMetal f = new FluidMoltenMetal(m.name);
			TFTFluids.moltenMetal.put(m.name, f);
			FluidRegistry.registerFluid(f);
		}

		//TODO: make this less of an awkward special case
		FluidMoltenMetal redstone = new FluidMoltenMetal("Redstone");
		TFTFluids.moltenMetal.put("Redstone", redstone);
		FluidRegistry.registerFluid(redstone);

		FluidMoltenMetal alumina = new FluidMoltenMetal("Alumina");
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
	}

	private void registerItems() {
		materials = new Material[] { new Material("Constantan", 5,
				Alloy.EnumTier.TierIV, false)
				.setNugget(TFTMeta.ieConstantanNugget)
				.setAlloyRecipe(new AlloyIngred[] { new AlloyIngred("Copper",
						50, 60), new AlloyIngred("Nickel", 40, 50) })
				.setBlock(IEContent.blockStorage, 0),
				new Material("Electrum", 5, Alloy.EnumTier.TierIV, false)
						.setNugget(TFTMeta.ieElectrumNugget)
						.setAlloyRecipe(new AlloyIngred[] { new AlloyIngred(
								"Gold", 50, 60),
								new AlloyIngred("Silver", 40, 50) }),
				//my eyes honestly hurt
				new Material("Aluminum", 3, Alloy.EnumTier.TierIII, false)
						.setNugget(TFTMeta.ieAluminiumNugget)
						.setOreName("Aluminum"),

				new Material("WroughtIron", 3, Alloy.EnumTier.TierIII, true)
						.setUnshaped(TFCItems.wroughtIronUnshaped)
						.setIngot(TFCItems.wroughtIronIngot)
						.setIngot2x(TFCItems.wroughtIronIngot2x)
						.setSheet(TFCItems.wroughtIronSheet)
						.setSheet2x(TFCItems.wroughtIronSheet2x)
						.setNugget(TFTMeta.ieIronNugget).setOreName("Iron"),
				new Material("Lead", 2, true).setIngot(TFCItems.leadIngot)
						.setUnshaped(TFCItems.leadUnshaped)
						.setNugget(TFTMeta.ieLeadNugget),
				new Material("Steel", 4, true).setIngot(TFCItems.steelIngot)
						.setNugget(TFTMeta.ieSteelNugget)
						.setUnshaped(TFCItems.steelUnshaped)
						.setBlock(IEContent.blockStorage, 7),
				new Material("Copper", 1, true).setIngot(TFCItems.copperIngot)
						.setNugget(TFTMeta.ieCopperNugget)
						.setUnshaped(TFCItems.copperUnshaped),
				new Material("Tin", 0, true).setIngot(TFCItems.tinIngot)
						.setUnshaped(TFCItems.tinUnshaped),
				new Material("Bronze", 2, true).setIngot(TFCItems.bronzeIngot)
						.setUnshaped(TFCItems.bronzeUnshaped),
				new Material("Silver", 2, true).setIngot(TFCItems.silverIngot)
						.setNugget(TFTMeta.ieSilverNugget)
						.setUnshaped(TFCItems.silverUnshaped),
				new Material("Nickel", 4, true).setIngot(TFCItems.nickelIngot)
						.setNugget(TFTMeta.ieNickelNugget)
						.setUnshaped(TFCItems.nickelUnshaped)

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
				Blocks.air).setUnlocalizedName("woodenBucketCreosote"), 0, 1);
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

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new TFTAnvilRecipeHandler());

		removeRecipes();
		addIEMachineRecipes();
		addIERecipes();
		addTFTRecipes();
		tfcAlloyRecipes();
		tfcClayRecipes();
		tfcHeatRecipes();
		tfcKilnRecipes();
		for (Material m : materials) {
			m.addMachineRecipes();
			m.addMoldRecipes();
		}
		logger.info(IEApi.modPreference);
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

		GameRegistry.addRecipe(new ShapelessOreRecipe(TFTMeta.ieBlastBrickAdv,
				TFCBlocks.fireBrick));
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
				new ItemStack(TFCItems.leather, 1, 1), 'w', "ingotIron", 'r',
				"dustRedstone"));

		//wire coils
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieLvWire, 4), " m ", "msm", " m ", 'm',
				"ingotCopper", 's', TFTMeta.ieTreatedStick));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieMvWire, 4), " m ", "msm", " m ", 'm',
				"ingotElectrum", 's', TFTMeta.ieTreatedStick));
		GameRegistry
				.addRecipe(new ShapedOreRecipe(ItemUtil.clone(TFTMeta.ieHvWire,
						4), " a ", "msm", " a ", 'm', "ingotSteel", 'a',
						"ingotAluminum", 's', TFTMeta.ieTreatedStick));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieHempWire, 4), " m ", "msm", " m ", 'm',
				TFTMeta.ieHempFibre, 's', TFTMeta.ieTreatedStick));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieSteelWire, 4), " m ", "msm", " m ", 'm',
				"ingotSteel", 's', TFTMeta.ieTreatedStick));

	}

	private void addIEMachineRecipes() {
		CokeOvenRecipe.removeRecipes(TFTMeta.ieCoalCoke);
		CokeOvenRecipe.removeRecipes(TFTMeta.ieCoalCokeBlock);
		CokeOvenRecipe.addRecipe(TFTMeta.ieCoalCoke,
				TFTMeta.bituminousCoal,
				1800,
				500);
		CokeOvenRecipe.addRecipe(TFTMeta.charcoal, TFCItems.logs, 1800, 100);

		MetalPressRecipe.removeRecipes(TFTMeta.ieIronPlate);
		MetalPressRecipe.removeRecipes(TFTMeta.ieLeadPlate);
		MetalPressRecipe.removeRecipes(TFTMeta.ieConstantanPlate);
		MetalPressRecipe.removeRecipes(TFTMeta.ieSteelPlate);
		MetalPressRecipe.removeRecipes(TFTMeta.ieAluminiumPlate);

		DieselHandler.addSqueezerRecipe(ItemUtil.clone(TFTMeta.graphite, 4),
				19200,
				null,
				TFTMeta.hopGraphite);

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
		RemoveBatch batch = new RemoveBatch();

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

		batch.addCrafting(TFTMeta.ieHammer);

		batch.addCrafting(TFTMeta.ieLvWire);
		batch.addCrafting(TFTMeta.ieMvWire);
		batch.addCrafting(TFTMeta.ieHvWire);
		batch.addCrafting(TFTMeta.ieHempWire);
		batch.addCrafting(TFTMeta.ieSteelWire);

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
