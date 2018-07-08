package dinglydell.tftechness;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.LogManager;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.MultiblockHandler;
import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.api.energy.DieselHandler;
import blusunrize.immersiveengineering.common.IEContent;

import com.bioxx.tfc.Core.FluidBaseTFC;
import com.bioxx.tfc.Core.WeatherManager;
import com.bioxx.tfc.Core.Metal.Alloy;
import com.bioxx.tfc.Core.Metal.AlloyManager;
import com.bioxx.tfc.Core.Metal.AlloyMetal;
import com.bioxx.tfc.Core.Metal.AlloyMetalCompare;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.Items.Pottery.ItemPotteryMold;
import com.bioxx.tfc.Items.Tools.ItemCustomBucket;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.Metal;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCFluids;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.Crafting.BarrelManager;
import com.bioxx.tfc.api.Crafting.BarrelRecipe;
import com.bioxx.tfc.api.Crafting.CraftingManagerTFC;
import com.bioxx.tfc.api.Crafting.KilnCraftingManager;
import com.bioxx.tfc.api.Crafting.KilnRecipe;
import com.bioxx.tfc.api.Crafting.QuernManager;
import com.bioxx.tfc.api.Crafting.QuernRecipe;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
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
import dinglydell.tftechness.block.BlockTreatedBarrel;
import dinglydell.tftechness.block.TFTBlocks;
import dinglydell.tftechness.block.TFTOre;
import dinglydell.tftechness.block.TFTOreRegistry;
import dinglydell.tftechness.block.component.BlockTFTComponent;
import dinglydell.tftechness.block.component.Component;
import dinglydell.tftechness.block.component.ComponentMaterial;
import dinglydell.tftechness.block.component.property.ComponentProperty;
import dinglydell.tftechness.block.component.property.ComponentPropertySet;
import dinglydell.tftechness.block.component.property.ComponentPropertyThermometerTier.ThermometerTier;
import dinglydell.tftechness.config.TFTConfig;
import dinglydell.tftechness.crop.CropIndexStack;
import dinglydell.tftechness.crop.TFTCropManager;
import dinglydell.tftechness.event.TFTDamageHandler;
import dinglydell.tftechness.event.TFTEventHandler;
import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.fluid.FluidStackFloat;
import dinglydell.tftechness.fluid.Gas;
import dinglydell.tftechness.fluid.Mixture;
import dinglydell.tftechness.fluid.TFTFluids;
import dinglydell.tftechness.gui.TFTGuiHandler;
import dinglydell.tftechness.item.ItemBlockMachineComponent;
import dinglydell.tftechness.item.ItemBlockTreatedBarrel;
import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.item.TFTMeta;
import dinglydell.tftechness.item.TFTPropertyRegistry;
import dinglydell.tftechness.metal.AlloyIngred;
import dinglydell.tftechness.metal.Material;
import dinglydell.tftechness.metal.MetalSnatcher;
import dinglydell.tftechness.metal.MetalStat;
import dinglydell.tftechness.multiblock.MultiblockElectrolyser;
import dinglydell.tftechness.network.MachineComponentPacketHandler;
import dinglydell.tftechness.network.PacketMachineComponent;
import dinglydell.tftechness.network.PacketTFTMachine;
import dinglydell.tftechness.network.TFTMachinePacketHandler;
import dinglydell.tftechness.recipe.RemoveBatch;
import dinglydell.tftechness.recipe.SolutionRecipe;
import dinglydell.tftechness.recipe.SolutionRecipe.EnumState;
import dinglydell.tftechness.recipe.TFTAlloyRecipe;
import dinglydell.tftechness.recipe.TFTAnvilRecipeHandler;
import dinglydell.tftechness.render.RenderBlockTFT;
import dinglydell.tftechness.render.RenderItemMetal;
import dinglydell.tftechness.tileentities.TETFTMetalSheet;
import dinglydell.tftechness.tileentities.TileMachineComponent;
import dinglydell.tftechness.tileentities.TileMachineComponentItemShelf;
import dinglydell.tftechness.tileentities.TileMachineComponentTank;
import dinglydell.tftechness.tileentities.TileMachineCoolingElement;
import dinglydell.tftechness.tileentities.TileMachineElectrode;
import dinglydell.tftechness.tileentities.TileMachineHeatingElement;
import dinglydell.tftechness.tileentities.TileMachineMonitor;
import dinglydell.tftechness.tileentities.TileMachineRF.WireTier;
import dinglydell.tftechness.tileentities.TileMoltenMetal;
import dinglydell.tftechness.tileentities.TileTFTElectrolyser;
import dinglydell.tftechness.tileentities.TileTreatedBarrel;
import dinglydell.tftechness.util.ItemUtil;
import dinglydell.tftechness.util.MathsUtils;
import dinglydell.tftechness.util.OreDict;
import dinglydell.tftechness.util.StringUtil;
import dinglydell.tftechness.world.WeatherManagerTFT;

@Mod(modid = TFTechness2.MODID, version = TFTechness2.VERSION, dependencies = "required-after:terrafirmacraft;required-after:ImmersiveEngineering;after:advancedRocketry;after:libVulpes")
public class TFTechness2 {
	public static final String MODID = "TFTechness";
	public static final String VERSION = "0.3";
	/** The degree symbol */
	public static final String degrees = "\u00b0";
	public static Map<String, MetalStat> statMap = new HashMap();
	public static final float ABSOLUTE_ZERO = -273f;
	public static final float STANDARD_PRESSURE = 1e5f;
	public static org.apache.logging.log4j.Logger logger = LogManager
			.getLogger("TFTechness");
	public static List<Material> materials;
	public static Map<String, Material> materialMap;
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
		hackARLocalisation();
		hackTFCWeather();
		editIEMetalRelations();
		initStatMap();
		registerEventHandlers();
		registerPacketHandlers();

		registerTFTMachineComponents();

		addOres();

	}

	private void registerTFTMachineComponents() {
		//wall
		Component.registerComponent(new Component("wall",
				TileMachineComponent.class,
				new Object[] { " a ", "aAa", " a " }));

		//heater (b is wire coil)
		Component.registerComponent(new Component("heater",
				TileMachineHeatingElement.class, new Object[] { " b ",
						"bAb",
						" b " })
				.registerPropertySet(ComponentPropertySet.WIRE_TIER));

		// item shelf
		Component.registerComponent(new Component("shelf",
				TileMachineComponentItemShelf.class, new Object[] { "aaa",
						"   ",
						"aaa" }));

		// cooler (b is wire coil)
		Component.registerComponent(new Component("cooler",
				TileMachineCoolingElement.class, new Object[] { " a ",
						"bAb",
						" b " })
				.registerPropertySet(ComponentPropertySet.WIRE_TIER)
				.registerAdditionalIcon("cool"));

		//Tank
		Component.registerComponent(new Component("tank",
				TileMachineComponentTank.class, new Object[] { "aaa",
						"a a",
						"aaa" }).registerAdditionalIcon("open"));

		//Electrode
		Component.registerComponent(new Component("electrode",
				TileMachineElectrode.class,
				new Object[] { "a a", "b b", "a a" })
				.registerPropertySet(ComponentPropertySet.WIRE_TIER));

		//Monitor
		Component.registerComponent(new Component("monitor",
				TileMachineMonitor.class, new Object[] { "aaa", "aba", "aaa" })
				.registerPropertySet(ComponentPropertySet.THERMOMETER_TIER));

	}

	private void hackTFCWeather() {
		try {
			Field manager = WeatherManager.class.getDeclaredField("INSTANCE");
			finalField(manager);
			manager.setAccessible(true);
			manager.set(null, new WeatherManagerTFT());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void hackARLocalisation() {
		AdvancedRocketryBlocks.blockArcFurnace.setBlockName("advArcFurnace");
		AdvancedRocketryBlocks.blockRollingMachine
				.setBlockName("advRollingMachine");

	}

	private void addCrops() {
		TFTCropManager.getInstance().registerCrop(new CropIndexStack(
				TFTConfig.HEMP_ID, "hemp", 1, 40, 5, 1, -5, 0.25f, null,
				new int[] { 0, 10, 10 }).setOutput1(TFTMeta.ieHempFibre, 2));

	}

	private void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new TFTEventHandler());
		FMLCommonHandler.instance().bus().register(new TFTEventHandler());
		MinecraftForge.EVENT_BUS.register(new TFTDamageHandler());

		TFTDamageHandler.INSTANCE.registerDamageSources();

	}

	private void addOres() {
		TFTOreRegistry.registerOre(new TFTOre("Bauxite", "veins", "medium", 50,
				new String[] { "gneiss", "basalt", "shale" }, 5, 150, 60, 80));

		TFTOreRegistry.registerOre(new TFTOre("Rutile", "veins", "medium", 100,
				new String[] { "basalt",
						"gneiss",
						"marble",
						"schist",
						"phyllite",
						"slate",
						"quartzite",
						"dacite",
						"andesite",
						"rhyolite",
						"gabbro",
						"granite",
						"diorite",
						//this part did not seem to work
						"igneous extrusive",
						"metamorphic",
						"igneous intrusive" }, 5, 150, 60, 80));

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
		snw.registerMessage(MachineComponentPacketHandler.class,
				PacketMachineComponent.class,
				2,
				Side.SERVER);
		snw.registerMessage(MachineComponentPacketHandler.class,
				PacketMachineComponent.class,
				3,
				Side.CLIENT);
	}

	private void initStatMap() {

		// Stock TFC
		// For multiple metals with the same stat entry
		MetalStat blackSteel = new MetalStat(0.35, 1485, 8982, 0.113f, 7.6e8f);
		MetalStat blueSteel = new MetalStat(0.35, 1540, 8775, 0.115f, 8e8f);
		MetalStat copper = new MetalStat(0.35, 1080, 8960, 0.772f, 7e7f);
		MetalStat redSteel = new MetalStat(0.35, 1540, 9837, 0.115f, 8e8f);
		MetalStat steel = new MetalStat(0.35, 1540, 8000, 0.108f, 2.5e8f);

		statMap.put("Bismuth", new MetalStat(0.14, 270, 10000, 0.017f, 4e6f));
		statMap.put("Bismuth Bronze", new MetalStat(0.35, 985, 963, 0.50445f,
				4e6f));
		statMap.put("Black Bronze", new MetalStat(0.35, 1070, 8626, 0.752f,
				6.5e7f));
		statMap.put("Black Steel", blackSteel);
		statMap.put("Blue Steel", blueSteel);
		statMap.put("Brass", new MetalStat(0.35, 930, 8500, 0.222f, 1.24e8f));
		statMap.put("Bronze", new MetalStat(0.35, 950, 8523, 0.052f, 1.38e8f));
		statMap.put("Copper", copper);
		statMap.put("Gold", new MetalStat(0.6, 1060, 18000, 0.63f, 1.15e8f));
		statMap.put("HC Black Steel", blackSteel);
		statMap.put("HC Blue Steel", blueSteel);
		statMap.put("HC Red Steel", redSteel);
		statMap.put("Lead", new MetalStat(0.22, 328, 11000, 0.07f, 1e7f));
		statMap.put("Nickel", new MetalStat(0.48, 1453, 8200, 0.18f, 7e7f));
		statMap.put("Pig Iron", new MetalStat(0.35, 1500, 8100, 0.115f, 1.3e8f));
		statMap.put("Platinum", new MetalStat(0.35, 1730, 20000, 0.146f,
				1.25e8f));
		statMap.put("Red Steel", redSteel);
		statMap.put("Rose Gold", new MetalStat(0.35, 960, 16731, 0.6584f,
				1.06e8f));
		statMap.put("Silver", new MetalStat(0.48, 961, 10000, 0.814f, 5e7f));
		//statMap.put("Stirling Silver", new MetalStat(0.245, 893, 10370, 0.814f));
		statMap.put("Steel", steel);
		statMap.put("Sterling Silver", new MetalStat(0.35, 900, 9738, 0.8014f,
				1.24e8f));
		statMap.put("Tin", new MetalStat(0.14, 230, 7000, 0.13f, 5e6f));
		// TFC uses copper heat properties for all unknown ingots
		statMap.put("Unknown", copper);
		statMap.put("Weak Red Steel", redSteel);
		statMap.put("Weak Blue Steel", blueSteel);
		statMap.put("Weak Steel", steel);
		statMap.put("Wrought Iron", new MetalStat(0.35, 1535, 7500, 0.118f,
				1.59e8f));
		statMap.put("Zinc", new MetalStat(0.21, 420, 7000, 0.232f, 7.5e7f));

		// IE
		statMap.put("Constantan", new MetalStat(0.39, 1210, 9870, 0.0424f,
				1.44e8f));
		statMap.put("Electrum", new MetalStat(0.181, 1025, 14460, 0.9f, 8.5e7f));
		//the world shudders in silence as a thousand dictionaries crumble in despair
		statMap.put("Aluminum", new MetalStat(0.91, 660, 2700, 0.408f, 4e7f));

		//AdvancedRocketry
		statMap.put("Titanium", new MetalStat(0.54, 1668, 4110, 0.042f, 7.8e8f));
		statMap.put("TitaniumAluminide", new MetalStat(0.79, 1460, 3123,
				0.2982f, 8.97e8f));
		statMap.put("Iridium", new MetalStat(0.31, 2446, 20000, 0.294f, 2e8f));
		// could not find any real data on this
		statMap.put("TitaniumIridium", new MetalStat(0.43, 2057, 12055, 0.168f,
				7e8f));

		// TFT
		//statMap.put("Billon", new MetalStat(0.35, 950, 1024));
		statMap.put("Uranium",
				new MetalStat(0.12, 1132, 19100, 0.048f, 2.95e8f));
		statMap.put("DullElectrum", new MetalStat(0.181, 650, 14460, 0.722f,
				8.3e7f));

		//non-metals
		statMap.put("Redstone", new MetalStat(1.136, 1000, 3000, 0.5f, 2e4f));
		statMap.put("Alumina", new MetalStat(0.451, 2072, 3900, 0.05f, 6.9e7f));
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		registerItems(event.getSide() == Side.CLIENT);
		addCrops();
		registerFluids();
		registerItemProps();
		registerBlocks();
		registerGui();
		registerRenderers();
		registerIEMultiblocks();
		registerTileEntities();

		//Waila support
		FMLInterModComms.sendMessage("Waila",
				"register",
				"dinglydell.tftechness.waila.TFTWaila.callbackRegister");
	}

	private void registerRenderers() {
		RenderingRegistry
				.registerBlockHandler(RenderBlockTFT.renderCrops = RenderingRegistry
						.getNextAvailableRenderId(),
						new RenderBlockTFT());

	}

	private void registerItemProps() {
		ItemStack redstone = new ItemStack(Items.redstone);
		TFTPropertyRegistry.registerPowder(redstone);
		TFTPropertyRegistry.registerDensity(redstone, 100);

		ItemStack alumina = new ItemStack(TFTItems.alumina);
		TFTPropertyRegistry.registerPowder(alumina);
		FluidMoltenMetal moltRed = TFTFluids.moltenMetal.get("Redstone");
		TFTPropertyRegistry.registerSolute(alumina, moltRed, 250);

		TFTPropertyRegistry.registerMolten(redstone, moltRed);
		TFTPropertyRegistry.registerDensity(redstone, 10f);
		TFTPropertyRegistry.registerVolume(redstone, 0.1f);
		TFTPropertyRegistry.registerMolten(alumina,
				TFTFluids.moltenMetal.get("Alumina"));
		TFTPropertyRegistry.registerNumMoles(alumina, 250);
		TFTPropertyRegistry.registerDensity(alumina, 25.49f);
		TFTPropertyRegistry.registerVolume(alumina, 0.006396f);

		TFTPropertyRegistry.registerNumMoles(TFCFluids.FRESHWATER, 1000f / 18f);
		TFTPropertyRegistry.registerBoil(TFCFluids.FRESHWATER, Gas.STEAM);
		TFTPropertyRegistry.registerConductivity(TFCFluids.FRESHWATER, 0.01f);
		TFTPropertyRegistry.registerSpecificHeat(TFCFluids.FRESHWATER, 4118);

		//salty!
		TFTPropertyRegistry.registerSolute(TFTMeta.salt,
				TFCFluids.FRESHWATER,
				36f);
		TFTPropertyRegistry.registerDensity(TFTMeta.salt, 0.032f);
		TFTPropertyRegistry.registerVolume(TFTMeta.salt, 1.5e-5f);
		TFTPropertyRegistry.registerNumMoles(TFTMeta.salt, 0.5475f);
		TFTPropertyRegistry.registerMixture(new Mixture(new FluidStack(
				TFCFluids.SALTWATER, 1000)).addFluid(new FluidStack(
				TFCFluids.FRESHWATER, 984)).addSolute(TFTMeta.salt, 600));

		TFTPropertyRegistry.registerMolten(TFTMeta.iceFresh,
				TFCFluids.FRESHWATER,
				0);

		//carbon

		TFTPropertyRegistry.registerCarbonContentByMass(new ItemStack(
				TFCItems.coal), 192f);
		TFTPropertyRegistry.registerCarbonContentByMass(TFTMeta.charcoal, 192f);

		TFTPropertyRegistry.registerCarbonContentByMass(TFTMeta.ieCoalCoke,
				180f);
		TFTPropertyRegistry
				.registerCarbonContentByMass(TFTMeta.ieCoalCokeBlock, 180f * 9);
		for (ItemStack log : OreDictionary.getOres("logWood")) {
			TFTPropertyRegistry.registerCarbonContentByMass(log, 57f);
		}

	}

	private void registerFluids() {
		for (Material m : materials) {
			FluidMoltenMetal f = new FluidMoltenMetal(m.name,
					m.heatRaw.meltTemp);
			TFTFluids.moltenMetal.put(m.name, f);
			FluidRegistry.registerFluid(f);

			m.registerMolten(f);

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

		//H2SO4
		TFTFluids.sulfuricAcid = new FluidBaseTFC("sulfuricAcid")
				.setBaseColor(0xf3f7d0).setUnlocalizedName("sulfuricAcid");
		FluidRegistry.registerFluid(TFTFluids.sulfuricAcid);

		//HCL
		TFTFluids.hydrochloricAcid = new FluidBaseTFC("hydrochloricAcid")
				.setBaseColor(0xd0e9f7).setUnlocalizedName("hydrochloricAcid");
		FluidRegistry.registerFluid(TFTFluids.hydrochloricAcid);

		TFTFluids.ferrousChloride = new FluidBaseTFC("ferrousChloride")
				.setBaseColor(0xA1580A).setUnlocalizedName("ferrousChloride");
		FluidRegistry.registerFluid(TFTFluids.ferrousChloride);
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

		GameRegistry.registerTileEntity(TileTreatedBarrel.class,
				"TreatedBarrel");

		GameRegistry.registerTileEntity(TileMachineComponent.class,
				"TFTMachineComponent");
		GameRegistry.registerTileEntity(TileMachineHeatingElement.class,
				"TFTMachineHeatingElement");
		GameRegistry.registerTileEntity(TileMachineComponentItemShelf.class,
				"TFTMachineItemShelf");
		GameRegistry.registerTileEntity(TileMachineCoolingElement.class,
				"TFTMachineCoolingElement");
		GameRegistry.registerTileEntity(TileMachineComponentTank.class,
				"TFTMachineTank");
		GameRegistry.registerTileEntity(TileMachineElectrode.class,
				"TFTMachineElectrode");

		GameRegistry.registerTileEntity(TileMachineMonitor.class,
				"TFTMachineMonitor");

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

		TFTBlocks.crops = new BlockCropTFT().setBlockName("crops")
				.setHardness(0.3F).setStepSound(Block.soundTypeGrass);

		GameRegistry.registerBlock(TFTBlocks.crops,
				TFTBlocks.crops.getUnlocalizedName());

		// pls don't tell
		TFCBlocks.crops = TFTBlocks.crops;
		//TODO: Fix this. this is bad. do not leave this here. find another way

		TFTBlocks.barrel = new BlockTreatedBarrel()
				.setBlockName("treatedBarrel").setHardness(2);
		GameRegistry.registerBlock(TFTBlocks.barrel,
				ItemBlockTreatedBarrel.class,
				TFTBlocks.barrel.getUnlocalizedName());
		TFTBlocks.treatedBarrel = new ItemStack(TFTBlocks.barrel, 1, 3);

		//machines

		//Add all component blocks
		for (Component c : Component.components) {
			BlockTFTComponent comp = (BlockTFTComponent) new BlockTFTComponent(
					c).setBlockName("machineComponent" + c.name);
			GameRegistry.registerBlock(comp,
					ItemBlockMachineComponent.class,
					"machineComponent" + c.name);
			TFTBlocks.components.put(c, comp);
		}

	}

	private void registerItems(boolean client) {
		materials = new ArrayList<Material>(
				Arrays.asList(new Material[] { new Material("Constantan", 5,
						Alloy.EnumTier.TierIV, false)
						.setNugget(TFTMeta.ieConstantanNugget)
						.setAlloyRecipe(new AlloyIngred[] { new AlloyIngred(
								"Copper", 50, 60),
								new AlloyIngred("Nickel", 40, 50) })
						.setBlock(IEContent.blockStorage, 0),
						new Material("DullElectrum", 5, Alloy.EnumTier.TierIV,
								false)
								.setAlloyRecipe(new AlloyIngred[] { new AlloyIngred(
										"Gold", 50, 60),
										new AlloyIngred("Silver", 40, 50) })
								.setColour(0xA0A582),
						new Material("Electrum", 5, Alloy.EnumTier.TierIV,
								false).setNugget(TFTMeta.ieElectrumNugget)
								.setColour(0xD8D387),
						//.setAlloyRecipe(new AlloyIngred[] { new AlloyIngred(
						//	"Gold", 50, 60),
						//new AlloyIngred("Silver", 40, 50) }),
						//my eyes honestly hurt
						new Material("Aluminum", 3, Alloy.EnumTier.TierIII,
								false).setNugget(TFTMeta.ieAluminiumNugget)
								.setOreName("Aluminum").setColour(0xE1EBFA),

						new Material("Wrought Iron", 3, Alloy.EnumTier.TierIII,
								true)
								.setColour(0xAAAAAA)
								//.setUnshaped(TFCItems.wroughtIronUnshaped)
								//.setIngot(TFCItems.wroughtIronIngot)
								//.setIngot2x(TFCItems.wroughtIronIngot2x)
								//.setSheet(TFCItems.wroughtIronSheet)
								//.setSheet2x(TFCItems.wroughtIronSheet2x)
								.setNugget(TFTMeta.ieIronNugget)
								.setOreName("Iron"),
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

				}));

		//TODO: make this less of a hack
		//HashSet<String> mats = new HashSet<String>();
		materialMap = new HashMap<String, Material>();
		for (Material m : materials) {
			materialMap.put(m.name, m);
			if (m.oreName != m.name) {
				materialMap.put(m.oreName, m);
			}
		}
		for (Metal m : MetalSnatcher.getMetals().values()) {
			if (!materialMap.containsKey(m.name)) {
				Material mat = new Material(m.name, 2, true);
				materials.add(mat);
				materialMap.put(mat.name, mat);
			}
		}
		for (zmaster587.libVulpes.api.material.Material m : MaterialRegistry
				.getAllMaterials()) {
			if (!materialMap.containsKey(m.getUnlocalizedName())
					&& statMap.containsKey(m.getUnlocalizedName())) {
				Material mat = new Material(m.getUnlocalizedName(), 4,
						Alloy.EnumTier.TierVI, false).setOreName(m
						.getOreDictNames()[0]);
				materials.add(mat);
				materialMap.put(mat.oreName, mat);

			}
		}
		materialMap.get("TitaniumAluminide")
				.setAlloyRecipe(new AlloyIngred[] { new AlloyIngred("Titanium",
						25, 35), new AlloyIngred("Aluminum", 65, 75) });
		materialMap.get("TitaniumIridium")
				.setAlloyRecipe(new AlloyIngred[] { new AlloyIngred("Titanium",
						45, 55), new AlloyIngred("Iridium", 45, 55) });
		TFTItems.nuggetMold = new ItemPotteryMold()
				.setMetaNames(new String[] { "Clay Nugget Mold",
						"Ceramic Nugget Mold" })
				.setUnlocalizedName("nuggetMold");

		//TFTItems.nuggetMold.setContainerItem(TFTItems.nuggetMold);
		GameRegistry.registerItem(TFTItems.nuggetMold, "nuggetMold");

		RenderItemMetal render = client ? new RenderItemMetal() : null;
		for (Material m : materials) {
			m.initialise(render);
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

		TFTItems.controlCircuitUnfinished = new ItemTerra()
				.setSize(EnumSize.SMALL).setWeight(EnumWeight.LIGHT)
				.setUnlocalizedName("controlCircuitUnfinished");
		GameRegistry.registerItem(TFTItems.controlCircuitUnfinished,
				TFTItems.controlCircuitUnfinished.getUnlocalizedName());

		TFTItems.ioCircuitUnfinished = new ItemTerra().setSize(EnumSize.SMALL)
				.setWeight(EnumWeight.LIGHT)
				.setUnlocalizedName("ioCircuitUnfinished");

		GameRegistry.registerItem(TFTItems.ioCircuitUnfinished,
				TFTItems.ioCircuitUnfinished.getUnlocalizedName());

		TFTItems.liquidIoCircuitUnfinished = new ItemTerra()
				.setSize(EnumSize.SMALL).setWeight(EnumWeight.LIGHT)
				.setUnlocalizedName("liquidIoCircuitUnfinished");

		GameRegistry.registerItem(TFTItems.liquidIoCircuitUnfinished,
				TFTItems.liquidIoCircuitUnfinished.getUnlocalizedName());

		//treated lumber
		//TODO: make this placeable
		TFTItems.lumberTreatedWood = new ItemTerra().setSize(EnumSize.MEDIUM)
				.setWeight(EnumWeight.LIGHT)
				.setUnlocalizedName("lumberTreatedWood");

		GameRegistry.registerItem(TFTItems.lumberTreatedWood,
				TFTItems.lumberTreatedWood.getUnlocalizedName());

		OreDictionary.registerOre("lumberTreatedWood",
				TFTItems.lumberTreatedWood);

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
		addARRecipes();
		addARMachineRecipes();
		logger.info(IEApi.modPreference);
	}

	private void addARRecipes() {
		GameRegistry.addRecipe(new ShapedOreRecipe(
				AdvancedRocketryBlocks.blockArcFurnace, " u ", "sbs", " a ",
				'u', AdvancedRocketryItems.itemMisc, 's', "plateBlackSteel",
				'b', TFCBlocks.fireBrick, 'a', "ingotAluminum"));

		GameRegistry.addRecipe(new ShapelessOreRecipe(
				AdvancedRocketryBlocks.blockBlastBrick, "plateBlackSteel",
				TFCBlocks.fireBrick));

		//circuits
		GameRegistry.addRecipe(new ShapedOreRecipe(
				TFTItems.controlCircuitUnfinished, " p ", "rcr", " w ", 'p',
				Items.paper, 'r', "dustRedstone", 'c', "plateCopper", 'w',
				"slabTreatedWood"));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				TFTItems.ioCircuitUnfinished, "rpr", " c ", " w ", 'p',
				Items.paper, 'r', "dustRedstone", 'c', "plateCopper", 'w',
				"slabTreatedWood"));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				TFTItems.liquidIoCircuitUnfinished, " p ", " c ", "rwr", 'p',
				Items.paper, 'r', "dustRedstone", 'c', "plateCopper", 'w',
				"slabTreatedWood"));

		//Cutting machine
		GameRegistry.addRecipe(new ShapedOreRecipe(
				AdvancedRocketryBlocks.blockCuttingMachine, "gug", "imc",
				" s ", 'g', "gearSteel", 'u', TFTMeta.arUi, 'i',
				TFTMeta.arCircuitIO, 'm', LibVulpesBlocks.blockStructureBlock,
				'c', TFTMeta.arCircuitControl, 's', "plateSteel"));

		//crystalliser
		GameRegistry.addRecipe(new ShapedOreRecipe(
				AdvancedRocketryBlocks.blockCrystallizer, " u ", "imc", "rsr",
				'g', "gearSteel", 'u', TFTMeta.arUi, 'i', TFTMeta.arCircuitIO,
				'm', LibVulpesBlocks.blockStructureBlock, 'c',
				TFTMeta.arCircuitControl, 's', "plateSteel", 'r',
				Items.repeater));

		//UI
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.arUi, " s ", "rgr",
				's', "plateRedSteel", 'r', "dustRedstone", 'g',
				Blocks.glass_pane));

		//optical sensor
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.arOpticalSensor,
				"ggg", " w ", " s ", 's', "plateGold", 'w', TFTMeta.ieLvWire,
				'g', Blocks.glass_pane));

		//carbon brick
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.arCarbonBrick, "cc",
				"cc", "cc", 'c', TFTMeta.charcoal));

		//Liquid fuel tank
		GameRegistry.addRecipe(new ShapedOreRecipe(
				AdvancedRocketryBlocks.blockFuelTank, "r r", "s s", "r r", 'r',
				"stickRedSteel", 's', "plateBlueSteel"));
		//advanced fuel engine
		GameRegistry.addRecipe(new ShapedOreRecipe(
				AdvancedRocketryBlocks.blockEngine, "iii", " s ", "s s", 'i',
				"ingotBlueSteel", 's', "plateTitanium"));
		//warp core
		GameRegistry.addRecipe(new ShapedOreRecipe(
				AdvancedRocketryBlocks.blockWarpCore, "tct", "bwb", "tct", 't',
				"plateTitanium", 'c', TFTMeta.arCircuitAdv, 'b',
				"plateBlueSteel", 'w', TFTMeta.ieHvWireBlock));

		//drill
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				AdvancedRocketryBlocks.blockDrill,
				LibVulpesBlocks.blockStructureBlock, IEContent.itemDrillhead));
	}

	private void addARMachineRecipes() {
		RecipesMachine arRecipeManager = RecipesMachine.getInstance();

		List<IRecipe> arcRecipes = arRecipeManager.recipeList
				.get(TileElectricArcFurnace.class);

		removeARMachineRecipes(arcRecipes, new String[] { "ingotTitanium" });

		//sand -> silicon
		arRecipeManager.addRecipe(TileElectricArcFurnace.class,
				OreDict.getOreItem("ingotSilicon"),
				12000,
				1,
				"blockSand");

		//rutile -> titanium
		ItemStack rutile = OreDict.getOreItem("oreNormalRutile");
		arRecipeManager.addRecipe(TileElectricArcFurnace.class,
				new ItemStack(TFTItems.ingots.get("Titanium")),
				200,
				512,
				ItemUtil.clone(rutile, 4));

		//poor rutile -> titanium
		ItemStack rutilePoor = OreDict.getOreItem("orePoorRutile");
		arRecipeManager.addRecipe(TileElectricArcFurnace.class,
				new ItemStack(TFTItems.nuggets.get("Titanium"), 3),
				100,
				512,
				ItemUtil.clone(rutilePoor, 2));
		//rich rutile -> titanium
		ItemStack rutileRich = OreDict.getOreItem("oreRichRutile");
		arRecipeManager.addRecipe(TileElectricArcFurnace.class,
				new ItemStack(TFTItems.nuggets.get("Titanium"), 7),
				100,
				512,
				ItemUtil.clone(rutileRich, 2));
		arRecipeManager.addRecipe(TileElectricArcFurnace.class,
				new ItemStack(TFTItems.ingots.get("Titanium"), 7),
				1000,
				512,
				ItemUtil.clone(rutileRich, 20));

		//		for (ArcFurnaceRecipe recipe : ArcFurnaceRecipe.recipeList) {
		//			List<Object> inputs = new ArrayList<Object>();
		//			inputs.add(getOreObj(recipe.input));
		//			for (Object additive : recipe.additives) {
		//				inputs.add(getOreObj(additive));
		//			}
		//
		//			arRecipeManager.getInstance()
		//					.addRecipe(TileElectricArcFurnace.class,
		//							recipe.output,
		//							recipe.time,
		//							recipe.energyPerTick,
		//							inputs.toArray());
		//		}

		//alloys
		for (Alloy alloy : AlloyManager.INSTANCE.alloys) {
			if (alloy.alloyIngred.size() == 1) {
				continue;
			}
			int gcd = 0;
			if (alloy.outputType.name.equals("Bronze")) {
				int x = 1;
			}
			for (AlloyMetal input : alloy.alloyIngred) {
				int m = (int) (100 * input.metal);
				if (input instanceof AlloyMetalCompare) {
					AlloyMetalCompare a = (AlloyMetalCompare) input;
					m = (int) (100 * (a.getMetalMax() + a.getMetalMin())) / 2;
				}
				if (gcd == 0) {
					gcd = m;
					continue;
				}

				gcd = MathsUtils.gcd(gcd, m);

			}

			List<ItemStack> inputs = new ArrayList<ItemStack>();
			List<FluidStackFloat> inputFluids = new ArrayList<FluidStackFloat>();
			int total = 0;
			for (AlloyMetal input : alloy.alloyIngred) {
				int m = (int) (100 * input.metal) / gcd;
				if (input instanceof AlloyMetalCompare) {
					AlloyMetalCompare a = (AlloyMetalCompare) input;
					m = (int) (100 * (a.getMetalMax() + a.getMetalMin()))
							/ (gcd * 2);
				}
				total += m;
				inputs.add(new ItemStack(input.metalType.ingot, m));
				Fluid inputFluid = TFTPropertyRegistry
						.getMolten(input.metalType);
				inputFluids.add(new FluidStackFloat(inputFluid, m, 0));
			}

			//ItemStack first = inputs.remove(0);
			if (inputs.size() > 1) {
				arRecipeManager.addRecipe(TileElectricArcFurnace.class,
						new ItemStack(alloy.outputType.ingot, total),
						200,
						512,
						inputs.toArray());
				FluidStackFloat outputFluid = new FluidStackFloat(
						TFTPropertyRegistry.getMolten(alloy.outputType), total,
						0);
				TFTAlloyRecipe.addRecipe(outputFluid, inputFluids);
			}
		}

		arRecipeManager.addRecipe(TileRollingMachine.class,
				new ItemStack(LibVulpesBlocks.blockAdvStructureBlock),
				400,
				200,
				"stickTitanium",
				"plateTitanium",
				"stickTitanium",
				"plateTitanium",
				"stickTitanium",
				"plateTitanium",
				"stickTitanium",
				"plateTitanium",
				"stickTitanium");

		for (net.minecraft.item.crafting.IRecipe recipe : RollingMachineCraftingManager
				.getInstance().getRecipeList()) {
			//logger.info(recipe.getClass().getName());
			//TODO: work out proper time/rf
			arRecipeManager.addRecipe(TileRollingMachine.class,
					recipe.getRecipeOutput(),
					80,
					1,
					getInput(recipe));
		}

		//precision assembler
		//liquid IO circuit board
		arRecipeManager.addRecipe(TilePrecisionAssembler.class,
				TFTMeta.arCircuitLiquidIO,
				200,
				10,
				"plateSilicon",
				"dyeBlue",
				TFTMeta.arCircuitBasic,
				"dustRedstone");

		//circuit plates
		arRecipeManager.addRecipe(TilePrecisionAssembler.class,
				TFTMeta.arCircuitPlateBasic,
				9000,
				100,
				"ingotGold",
				"dustRedstone",
				TFTMeta.arWaferSilicon);
		arRecipeManager.addRecipe(TilePrecisionAssembler.class,
				TFTMeta.arCircuitPlateAdv,
				9000,
				100,
				"ingotElectrum",
				"dustRedstone",
				TFTMeta.arWaferSilicon);

		//data storage chip
		arRecipeManager.addRecipe(TilePrecisionAssembler.class,
				TFTMeta.arCircuitPlateAdv,
				9000,
				100,
				TFTMeta.arCircuitBasic,
				"dustRedstone");

		//tracking circuit
		arRecipeManager.addRecipe(TilePrecisionAssembler.class,
				TFTMeta.arCircuitTracking,
				9000,
				100,
				TFTMeta.arCircuitBasic,
				"dustRedstone");
	}

	private Object[] getInput(net.minecraft.item.crafting.IRecipe recipe) {
		if (recipe instanceof ShapedOreRecipe) {
			ShapedOreRecipe oreRecipe = (ShapedOreRecipe) recipe;
			List<Object> inputs = new ArrayList<Object>();
			for (Object in : oreRecipe.getInput()) {
				inputs.add(getOreObj(in));

			}
			return inputs.toArray();
		} else if (recipe instanceof ShapedRecipes) {
			return ((ShapedRecipes) recipe).recipeItems;
		}

		return null;

	}

	private Object getOreObj(Object obj) {
		if (obj instanceof ArrayList<?>) {
			String ore = OreDict.getOreName((List<ItemStack>) obj);
			return ore;
		}
		return obj;
	}

	private void removeARMachineRecipes(List<IRecipe> recipeList,
			String[] toRemove) {
		Iterator<IRecipe> iterator = recipeList.iterator();
		while (iterator.hasNext()) {
			IRecipe recipe = iterator.next();
			boolean found = false;
			for (String o : toRemove) {

				//oredict
				String oreName = (String) o;
				for (ItemStack out : recipe.getOutput()) {
					if (OreDict.itemMatches(out, oreName)) {
						found = true;
						iterator.remove();
						break;
					}
				}
				if (found) {
					break;
				}
			}
		}

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

		//rebar
		RollingMachineCraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(RailcraftItem.rebar.getStack(4),
						"  s", " s ", "s  ", 's', "ingotBlackSteel"));

		//machine structure (AR)
		RollingMachineCraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(LibVulpesBlocks.blockStructureBlock,
						"rsr", "s s", "rsr", 'r', "stickSteel", 's',
						"plateSteel"));

		//small battery (AR)
		RollingMachineCraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(LibVulpesItems.itemBattery, " s ",
						"prp", "prp", 's', "stickTin", 'p', "plateTin", 'r',
						"dustRedstone"));

	}

	private void removeRollingMachineRecipes() {
		RemoveBatch rollingBatch = new RemoveBatch(
				RollingMachineCraftingManager.getInstance().getRecipeList());

		rollingBatch.addCrafting(TFTMeta.railElectric);
		rollingBatch.addCrafting(RailcraftItem.rebar.getStack());

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

		for (ItemStack plank : OreDictionary.getOres("woodLumber")) {
			manager.addRecipe(new BarrelRecipe(plank.copy(), new FluidStack(
					IEContent.fluidCreosote, 25), new ItemStack(
					TFTItems.lumberTreatedWood), new FluidStack(
					IEContent.fluidCreosote, 25), 4));
		}

		//H2SO4
		manager.addRecipe(new BarrelRecipe(TFTMeta.sulfur, new FluidStack(
				TFCFluids.FRESHWATER, 1000), null, new FluidStack(
				TFTFluids.sulfuricAcid, 1000), 0).setSealedRecipe(false)
				.setRemovesLiquid(false).setAllowAnyStack(false));

		//HCL
		manager.addRecipe(new BarrelRecipe(TFTMeta.salt, new FluidStack(
				TFTFluids.sulfuricAcid, 1000), null, new FluidStack(
				TFTFluids.hydrochloricAcid, 1000), 0).setSealedRecipe(false)
				.setRemovesLiquid(false).setAllowAnyStack(false));

		setupFeClRecipes(manager, "oreNormalIron", 250);
		setupFeClRecipes(manager, "orePoorIron", 150);
		setupFeClRecipes(manager, "oreRichIron", 350);
		setupFeClRecipes(manager, "oreSmallIron", 100);

		//circuits
		FluidStack fecl = new FluidStack(TFTFluids.ferrousChloride, 100);
		manager.addRecipe(new BarrelRecipe(new ItemStack(
				TFTItems.controlCircuitUnfinished), fecl,
				TFTMeta.arCircuitControl, fecl, 1));
		manager.addRecipe(new BarrelRecipe(new ItemStack(
				TFTItems.ioCircuitUnfinished), fecl, TFTMeta.arCircuitIO, fecl,
				1));
		manager.addRecipe(new BarrelRecipe(new ItemStack(
				TFTItems.liquidIoCircuitUnfinished), fecl,
				TFTMeta.arCircuitLiquidIO, fecl, 1));

	}

	private void setupFeClRecipes(BarrelManager manager, String oreName, int amt) {
		for (ItemStack iron : OreDictionary.getOres(oreName)) {
			manager.addRecipe(new BarrelRecipe(iron, new FluidStack(
					TFTFluids.hydrochloricAcid, amt), null, new FluidStack(
					TFTFluids.ferrousChloride, amt), 0).setSealedRecipe(false)
					.setRemovesLiquid(false).setAllowAnyStack(false));
		}

	}

	private void addTFTRecipes() {
		// Alumina (solute) -> Aluminium (l)
		SolutionRecipe
				.addRecipe(new SolutionRecipe(new ItemStack(TFTItems.ingots
						.get("Aluminum")), 1.35f, EnumState.solute,
						new ItemStack(TFTItems.alumina), 25,
						SolutionRecipe.electrodes));

		//Alumina (l) -> Aluminium (l)
		SolutionRecipe
				.addRecipe(new SolutionRecipe(new ItemStack(TFTItems.ingots
						.get("Aluminum")), 1.35f, EnumState.liquid,
						new ItemStack(TFTItems.alumina), 25,
						SolutionRecipe.electrodes));

		//treated plank -> treated lumber
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(
				TFTItems.lumberTreatedWood, 4), "plankTreatedWood", "itemSaw"));
		//treated lumber -> treated blank
		GameRegistry.addRecipe(new ShapedOreRecipe(IEContent.blockTreatedWood,
				"LL", "LL", 'L', "lumberTreatedWood"));

		//treated barrel
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTBlocks.treatedBarrel,
				"l l", "l l", "lll", 'l', "lumberTreatedWood"));

		//components
		//GameRegistry.addShapelessRecipe(BlockTFTComponent
		//	.getBlockWithNBT(TFTComponents.wall, 0.05f), Blocks.dirt);
		//ComponentMaterialRegistry.registerBaseMaterial("stoneBricks",
		//	0.01f,
		//false);
		//TODO: make this less hacky? make it so the property stored is yield strength and that's translated to max pressure for display on a per component basis
		float tankThickness = 0.01f;
		//register stone with the conductivity property
		ComponentMaterial
				.registerMaterial("stone",
						"stoneBricks",
						new ItemStack(TFCItems.stoneBrick, 1,
								OreDictionary.WILDCARD_VALUE))
				.addProperty(ComponentProperty.CONDUCTIVITY, 0.05f)
				.addProperty(ComponentProperty.SPECIFIC_HEAT, 790f)
				.addProperty(ComponentProperty.MAXIMUM_PRESSURE,
						tankThickness * 4.8e7f)
				.addProperty(ComponentProperty.MAXIMUM_TEMPERATURE, 1250f);

		//fire brick 0.0005
		ComponentMaterial
				.registerMaterial("fireBrick",
						new ItemStack(TFCBlocks.fireBrick),
						new ItemStack(TFCItems.fireBrick, 1, 1))
				.addProperty(ComponentProperty.CONDUCTIVITY, 0.0025f)
				.addProperty(ComponentProperty.SPECIFIC_HEAT, 1050f)
				.addProperty(ComponentProperty.MAXIMUM_PRESSURE,
						tankThickness * 4.8e7f)
				.addProperty(ComponentProperty.MAXIMUM_TEMPERATURE, 1648f);

		for (Material m : materials) {
			// register metal materials with conductivity property
			if (m.sheet != null && m.sheet2x != null) {
				ComponentMaterial
						.registerMaterial(m.name,
								"plateDouble" + m.oreName,
								"plate" + m.oreName)
						.addProperty(ComponentProperty.CONDUCTIVITY,
								statMap.get(m.name).conductivity)
						.addProperty(ComponentProperty.SPECIFIC_HEAT,
								statMap.get(m.name).getSISpecificHeat())
						.addProperty(ComponentProperty.MAXIMUM_PRESSURE,
								tankThickness * statMap.get(m.name).yieldStress)
						.addProperty(ComponentProperty.MAXIMUM_TEMPERATURE,
								statMap.get(m.name).heat.meltTemp);
			}
		}

		//wires
		for (WireTier wire : WireTier.values()) {
			//register a wire with wire tier property
			ComponentMaterial.registerMaterial(wire.name(),
					wire.getWire(),
					wire.getWire()).addProperty(ComponentProperty.WIRE_TIER,
					wire);
		}
		//TODO: make a thermometer item for this
		ComponentMaterial.getMaterial("lv")
				.addProperty(ComponentProperty.THERMOMETER_TIER,
						ThermometerTier.fuzzy);
		ComponentMaterial.getMaterial("mv")
				.addProperty(ComponentProperty.THERMOMETER_TIER,
						ThermometerTier.rounded);
		ComponentMaterial.getMaterial("hv")
				.addProperty(ComponentProperty.THERMOMETER_TIER,
						ThermometerTier.precise);

		ComponentMaterial.registerRecipes();
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

		//HOP graphite dust -> HOP graphite ingot (placeholder)
		manager.addIndex(new HeatIndex(TFTMeta.hopGraphiteDust, 1, 1000,
				TFTMeta.hopGraphiteIngot));

		//for (Component c : Component.components) {
		//	manager.addIndex(new HeatIndex(new ItemStack(
		//			TFTBlocks.components.get(c)), 1, 9999, null));
		//}
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
				TFCItems.leather, 'w', "ingotSteel", 'r', TFTMeta.ieLvWire));

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
		GameRegistry
				.addRecipe(new ShapedOreRecipe(TFTMeta.ieToughFabric, "fff",
						"fsf", "fff", 'f', "fibreHemp", 's', "stickTreatedWood"));

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
				.clone(TFTMeta.ieSqueezer, 2), "mwm", "cgc", "mpm", 'm',
				"ingotIron", 'c', TFTMeta.ieComponentIron, 'p', Blocks.piston,
				'w', TFTMeta.ieLvWire, 'g', "dyeGreen"));

		//Fermenter
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieFermenter, 2), "mwm", "cgc", "mpm", 'm',
				"ingotIron", 'c', TFTMeta.ieComponentIron, 'p', Blocks.piston,
				'w', TFTMeta.ieLvWire, 'g', "dyeBlue"));

		//broken recipes (recipes with things that are unobtainable with TFC)

		//LV wire connector
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieLvConnector, 8), "wmw", " m ", "wmw", 'w',
				"plankTreatedWood", 'm', "ingotCopper"));

		//MV wire connector
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieMvConnector, 8), "wmw", " m ", "wmw", 'w',
				"plankTreatedWood", 'm', "ingotElectrum"));

		//HV wire connector
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUtil
				.clone(TFTMeta.ieHvConnector, 4), "wmw", "wmw", "wmw", 'w',
				"plankTreatedWood", 'm', "ingotAluminum"));

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

		// concrete
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.ieConcrete, "scs",
				"gbg", "scs", 's', TFTMeta.ieSlag, 'g', "blockGravel", 'b',
				TFCItems.redSteelBucketWater, 'c', "lumpClay"));

		//hempcrete
		GameRegistry.addRecipe(new ShapedOreRecipe(TFTMeta.ieHempcrete, "ccc",
				"fff", "ccc", 'c', "lumpClay", 'f', "fiberHemp"));
	}

	private void addIEMachineRecipes() {
		CokeOvenRecipe.removeRecipes(TFTMeta.ieCoalCoke);
		//CokeOvenRecipe.removeRecipes(TFTMeta.ieCoalCokeBlock);
		CokeOvenRecipe.addRecipe(TFTMeta.ieCoalCoke, TFCItems.coal, 1800, 500);
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
		ArcFurnaceRecipe.removeRecipes(TFTMeta.ieElectrumIngot);
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
				TFTMeta.hopGraphiteDust);

		for (ItemStack seed : TFTMeta.seeds) {
			DieselHandler.addSqueezerRecipe(seed, 80, new FluidStack(
					IEContent.fluidPlantoil, 80), null);
		}

		//alloy recipes

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
				"oreNormalBauxite",
				3600);
		CrusherRecipe.addRecipe(new ItemStack(TFTItems.crushedBauxite, 3),
				"orePoorBauxite",
				3200);
		CrusherRecipe.addRecipe(new ItemStack(TFTItems.crushedBauxite, 7),
				"oreRichBauxite",
				4000);

		//quern to crusher recipes
		for (QuernRecipe recipe : QuernManager.getInstance().getRecipes()) {
			CrusherRecipe
					.addRecipe(recipe.getResult(), recipe.getInItem(), 500);
		}

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
		batch.addCrafting(TFTMeta.ieToughFabric);

		batch.addCrafting(TFTMeta.ieLvWire);
		batch.addCrafting(TFTMeta.ieMvWire);
		batch.addCrafting(TFTMeta.ieHvWire);
		batch.addCrafting(TFTMeta.ieHempWire);
		batch.addCrafting(TFTMeta.ieSteelWire);

		batch.addCrafting(TFTMeta.ieConcrete);
		batch.addCrafting(TFTMeta.ieHempcrete);

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
		batch.addCrafting(TFTMeta.ieFermenter);

		batch.addCrafting(TFTMeta.tinGearBushing);
		batch.addCrafting(TFTMeta.ironGear);
		batch.addCrafting(TFTMeta.steelGear);

		batch.addCrafting(new ItemStack(AdvancedRocketryBlocks.blockArcFurnace));

		batch.addCrafting(new ItemStack(LibVulpesBlocks.blockStructureBlock));
		batch.addCrafting(new ItemStack(LibVulpesBlocks.blockAdvStructureBlock));
		AllowedProducts stick = AllowedProducts.getProductByName("STICK");

		batch.addCrafting(new ItemStack(MaterialRegistry
				.getItemStackFromMaterialAndType("Titanium", stick).getItem(),
				1, OreDictionary.WILDCARD_VALUE));
		batch.addCrafting(new ItemStack(MaterialRegistry
				.getItemStackFromMaterialAndType("TitaniumAluminide", stick)
				.getItem(), 1, OreDictionary.WILDCARD_VALUE));

		batch.addCrafting(new ItemStack(LibVulpesItems.itemBattery));

		batch.addCrafting(TFTMeta.arUi);

		batch.addCrafting(new ItemStack(
				AdvancedRocketryBlocks.blockCuttingMachine));
		batch.addCrafting(new ItemStack(
				AdvancedRocketryBlocks.blockCrystallizer));

		batch.addCrafting(TFTMeta.arOpticalSensor);
		batch.addCrafting(new ItemStack(AdvancedRocketryBlocks.blockEngine));
		batch.addCrafting(new ItemStack(AdvancedRocketryBlocks.blockFuelTank));
		batch.addCrafting(new ItemStack(AdvancedRocketryBlocks.blockWarpCore));
		//batch.addCrafting(new ItemStack(AdvancedRocketryItems.itemSawBlade,1));
		//batch.addCrafting(new ItemStack(AdvancedRocketryItems.block,1));
		//broken recipes (recipes that were unobtainable due to using items that are not available with TFC)

		batch.addCrafting(TFTMeta.ieLvConnector);
		batch.addCrafting(TFTMeta.ieMvConnector);
		batch.addCrafting(TFTMeta.ieHvConnector);
		batch.addCrafting(TFTMeta.iePoweredLantern);
		batch.addCrafting(TFTMeta.ieCurrentTransformer);
		batch.addCrafting(TFTMeta.ieRadiatorBlock);
		batch.addCrafting(new ItemStack(IEContent.blockClothDevice));
		batch.addCrafting(new ItemStack(IEContent.itemJerrycan));
		batch.addCrafting(new ItemStack(IEContent.itemChemthrower));
		batch.addCrafting(TFTMeta.detectorTank);
		batch.addCrafting(TFTMeta.detectorPlayer);
		batch.addCrafting(TFTMeta.detectorMob);
		batch.addCrafting(TFTMeta.detectorExplosive);
		batch.addCrafting(TFTMeta.detectorAnimal);
		batch.addCrafting(TFTMeta.detectorAge);
		batch.addCrafting(TFTMeta.detectorRouting);
		batch.addCrafting(TFTMeta.detectorSheep);
		batch.addCrafting(TFTMeta.detectorLocomotive);
		batch.addCrafting(TFTMeta.detectorTrain);
		batch.addCrafting(TFTMeta.advItemLoader);
		batch.addCrafting(TFTMeta.advItemUnloader);
		batch.addCrafting(new ItemStack(Blocks.dispenser));
		batch.addCrafting(TFTMeta.engravingBench);
		batch.addCrafting(TFTMeta.fireboxSolid);
		batch.addCrafting(TFTMeta.fireboxLiquid);
		batch.addCrafting(ItemGoggles.getItem());
		batch.addCrafting(EnumCart.LOCO_ELECTRIC.getCartItem());
		batch.addCrafting(TFTMeta.circuitController);
		batch.addCrafting(TFTMeta.circuitReceiver);
		batch.addCrafting(TFTMeta.circuitSignal);
		batch.addCrafting(ItemMagnifyingGlass.getItem());
		batch.addCrafting(TFTMeta.ieConveyor);

		batch.Execute();

	}

	private void replaceWater() {

		try { // this, as you may well have noticed, is a bit of a hack
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

	public static void finalField(Field field) throws Exception {
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

	}
}
