package dinglydell.tftechness.item;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.items.ItemGear.EnumGear;
import mods.railcraft.common.items.RailcraftItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import blusunrize.immersiveengineering.common.IEContent;

import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCItems;

import dinglydell.tftechness.TFTechness2;

/** A help class to get items/blocks with metadata */
public class TFTMeta {

	/** List of all TFC-style seeds */
	public static List<ItemStack> seeds = new ArrayList<ItemStack>();

	static {
		//yea, don't look
		try {
			Field[] declaredFields = TFCItems.class.getDeclaredFields();
			for (Field field : declaredFields) {
				if (Modifier.isStatic(field.getModifiers())
						&& field.getName().startsWith("seeds")) {
					Item item = (Item) field.get(null);
					seeds.add(new ItemStack(item));
				}
			}
		} catch (Exception ex) {
			TFTechness2.logger.error("Failed to find TFC seeds");
		}

	}

	public static ItemStack ieCokeBrick = new ItemStack(
			IEContent.blockStoneDecoration, 1, 1);
	public static ItemStack ieBlastBrick = new ItemStack(
			IEContent.blockStoneDecoration, 1, 2);
	public static ItemStack ieBlastBrickAdv = new ItemStack(
			IEContent.blockStoneDecoration, 1, 6);
	public static ItemStack ieCoalCoke = new ItemStack(IEContent.itemMaterial,
			1, 6);
	public static ItemStack ieCoalCokeBlock = new ItemStack(
			IEContent.blockStoneDecoration, 1, 3);

	public static ItemStack hopGraphiteDust = new ItemStack(
			IEContent.itemMetal, 1, 19);
	public static ItemStack hopGraphiteIngot = new ItemStack(
			IEContent.itemMetal, 1, 20);

	public static ItemStack ieIronPlate = new ItemStack(IEContent.itemMetal, 1,
			30);
	public static ItemStack ieAluminiumPlate = new ItemStack(
			IEContent.itemMetal, 1, 32);
	public static ItemStack ieLeadPlate = new ItemStack(IEContent.itemMetal, 1,
			33);
	public static ItemStack ieConstantanPlate = new ItemStack(
			IEContent.itemMetal, 1, 36);
	public static ItemStack ieSteelPlate = new ItemStack(IEContent.itemMetal,
			1, 38);

	public static ItemStack ieConstantanNugget = new ItemStack(
			IEContent.itemMetal, 1, 27);
	public static ItemStack ieElectrumNugget = new ItemStack(
			IEContent.itemMetal, 1, 28);
	public static ItemStack ieAluminiumNugget = new ItemStack(
			IEContent.itemMetal, 1, 23);
	public static ItemStack ieIronNugget = new ItemStack(IEContent.itemMetal,
			1, 21);
	public static ItemStack ieLeadNugget = new ItemStack(IEContent.itemMetal,
			1, 24);
	public static ItemStack ieSteelNugget = new ItemStack(IEContent.itemMetal,
			1, 29);
	public static ItemStack ieCopperNugget = new ItemStack(IEContent.itemMetal,
			1, 22);
	public static ItemStack ieSilverNugget = new ItemStack(IEContent.itemMetal,
			1, 25);
	public static ItemStack ieNickelNugget = new ItemStack(IEContent.itemMetal,
			1, 26);

	public static ItemStack ieMoldPlate = new ItemStack(IEContent.itemMold, 1,
			0);
	public static ItemStack ieMoldRod = new ItemStack(IEContent.itemMold, 1, 2);

	public static ItemStack ieHammer = new ItemStack(IEContent.itemTool, 1, 0);
	public static ItemStack ieWireCutters = new ItemStack(IEContent.itemTool,
			1, 1);
	public static ItemStack ieVoltmeter = new ItemStack(IEContent.itemTool, 1,
			2);

	public static ItemStack ieSqueezer = new ItemStack(
			IEContent.blockMetalMultiblocks, 1, 2);
	public static ItemStack ieFermenter = new ItemStack(
			IEContent.blockMetalMultiblocks, 1, 3);

	public static ItemStack ieConveyor = new ItemStack(
			IEContent.blockMetalDevice, 1, 11);
	public static ItemStack ieConveyorDrop = new ItemStack(
			IEContent.blockMetalDevice, 1, 15);

	public static ItemStack bituminousCoal = new ItemStack(TFCItems.oreChunk,
			1, 14);

	public static ItemStack charcoal = new ItemStack(TFCItems.coal, 1, 1);
	public static ItemStack graphite = new ItemStack(TFCItems.powder, 1, 2);

	public static ItemStack ieLvWire = new ItemStack(IEContent.itemWireCoil, 1,
			0);
	public static ItemStack ieLvWireBlock = new ItemStack(
			IEContent.blockStorage, 1, 8);
	public static ItemStack ieLvConnector = new ItemStack(
			IEContent.blockMetalDevice);
	public static ItemStack ieMvConnector = new ItemStack(
			IEContent.blockMetalDevice, 1, 2);
	public static ItemStack ieHvConnector = new ItemStack(
			IEContent.blockMetalDevice, 1, 6);
	public static ItemStack ieMvWire = new ItemStack(IEContent.itemWireCoil, 1,
			1);
	public static ItemStack ieMvWireBlock = new ItemStack(
			IEContent.blockStorage, 1, 9);

	public static ItemStack ieHvWire = new ItemStack(IEContent.itemWireCoil, 1,
			2);
	public static ItemStack ieHvWireBlock = new ItemStack(
			IEContent.blockStorage, 1, 10);
	public static ItemStack ieHempWire = new ItemStack(IEContent.itemWireCoil,
			1, 3);
	public static ItemStack ieSteelWire = new ItemStack(IEContent.itemWireCoil,
			1, 4);
	public static ItemStack ieTreatedStick = new ItemStack(
			IEContent.itemMaterial);
	public static ItemStack ieHempFibre = new ItemStack(IEContent.itemMaterial,
			1, 3);

	public static ItemStack ieToughFabric = new ItemStack(
			IEContent.itemMaterial, 1, 4);
	public static ItemStack ieRevolverBarrel = new ItemStack(
			IEContent.itemMaterial, 1, 7);
	public static ItemStack ieRevolverDrum = new ItemStack(
			IEContent.itemMaterial, 1, 8);
	public static ItemStack ieComponentIron = new ItemStack(
			IEContent.itemMaterial, 1, 11);
	public static ItemStack ieComponentSteel = new ItemStack(
			IEContent.itemMaterial, 1, 12);

	public static ItemStack ieFocusedNozzle = new ItemStack(
			IEContent.itemToolUpgrades, 1, 7);
	public static ItemStack ieFluidPipe = new ItemStack(
			IEContent.blockMetalDevice2, 1, 5);
	public static ItemStack ieFluidPump = new ItemStack(
			IEContent.blockMetalDevice2, 1, 6);
	public static ItemStack ieSteelScaffolding = new ItemStack(
			IEContent.blockMetalDecoration, 1, 1);
	public static ItemStack ieHeavyEngineering = new ItemStack(
			IEContent.blockMetalDecoration, 1, 5);
	public static ItemStack ieLightEngineering = new ItemStack(
			IEContent.blockMetalDecoration, 1, 7);
	public static ItemStack ieRadiatorBlock = new ItemStack(
			IEContent.blockMetalDecoration, 1, 4);
	public static ItemStack ieSteelBlock = new ItemStack(
			IEContent.blockStorage, 1, 7);
	public static ItemStack ieSteelSlab = new ItemStack(
			IEContent.blockStorageSlabs, 1, 7);
	public static ItemStack ieSteelIngot = new ItemStack(IEContent.itemMetal,
			1, 7);
	public static ItemStack ieElectrumIngot = new ItemStack(
			IEContent.itemMetal, 1, 6);
	public static ItemStack ieSlag = new ItemStack(IEContent.itemMaterial, 1,
			13);
	public static ItemStack ieIronRod = new ItemStack(IEContent.itemMaterial,
			1, 14);
	public static ItemStack ieSteelRod = new ItemStack(IEContent.itemMaterial,
			1, 15);

	public static ItemStack ieBreakerSwitch = new ItemStack(
			IEContent.blockMetalDevice2);
	public static ItemStack ieCurrentTransformer = new ItemStack(
			IEContent.blockMetalDevice2, 1, 2);
	public static ItemStack iePoweredLantern = new ItemStack(
			IEContent.blockMetalDevice2, 1, 3);

	public static ItemStack ieAluminiumRod = new ItemStack(
			IEContent.itemMaterial, 1, 16);
	public static ItemStack railStandard = new ItemStack(
			RailcraftItem.rail.item(), 1, 0);
	public static ItemStack railAdvanced = new ItemStack(
			RailcraftItem.rail.item(), 1, 1);
	public static ItemStack railWooden = new ItemStack(
			RailcraftItem.rail.item(), 1, 2);
	public static ItemStack railElectric = new ItemStack(
			RailcraftItem.rail.item(), 1, 5);
	public static ItemStack detectorTank = new ItemStack(
			BlockDetector.getBlock(), 1, 8);
	public static ItemStack detectorPlayer = new ItemStack(
			BlockDetector.getBlock(), 1, 5);

	public static ItemStack detectorMob = new ItemStack(
			BlockDetector.getBlock(), 1, 3);
	public static ItemStack detectorExplosive = new ItemStack(
			BlockDetector.getBlock(), 1, 6);
	public static ItemStack detectorAnimal = new ItemStack(
			BlockDetector.getBlock(), 1, 7);
	public static ItemStack detectorAge = new ItemStack(
			BlockDetector.getBlock(), 1, 11);
	public static ItemStack detectorRouting = new ItemStack(
			BlockDetector.getBlock(), 1, 16);

	public static ItemStack detectorSheep = new ItemStack(
			BlockDetector.getBlock(), 1, 13);
	public static ItemStack detectorLocomotive = new ItemStack(
			BlockDetector.getBlock(), 1, 15);
	public static ItemStack detectorTrain = new ItemStack(
			BlockDetector.getBlock(), 1, 12);
	public static ItemStack itemLoader = new ItemStack(
			RailcraftBlocks.getBlockMachineGamma());
	public static ItemStack itemUnloader = new ItemStack(
			RailcraftBlocks.getBlockMachineGamma(), 1, 1);
	public static ItemStack advItemLoader = new ItemStack(
			RailcraftBlocks.getBlockMachineGamma(), 1, 2);
	public static ItemStack advItemUnloader = new ItemStack(
			RailcraftBlocks.getBlockMachineGamma(), 1, 3);
	public static ItemStack engravingBench = new ItemStack(
			RailcraftBlocks.getBlockMachineEpsilon(), 1, 5);
	public static ItemStack fireboxSolid = new ItemStack(
			RailcraftBlocks.getBlockMachineBeta(), 1, 5);
	public static ItemStack fireboxLiquid = new ItemStack(
			RailcraftBlocks.getBlockMachineBeta(), 1, 6);
	public static ItemStack tankGaugeIron = new ItemStack(
			RailcraftBlocks.getBlockMachineBeta(), 1, 1);
	public static ItemStack tankGaugeSteel = new ItemStack(
			RailcraftBlocks.getBlockMachineBeta(), 1, 14);
	public static ItemStack boilerTankLow = new ItemStack(
			RailcraftBlocks.getBlockMachineBeta(), 1, 3);
	public static ItemStack boilerTankHigh = new ItemStack(
			RailcraftBlocks.getBlockMachineBeta(), 1, 4);
	public static ItemStack circuitReceiver = new ItemStack(
			RailcraftItem.circuit.item(), 1, 1);
	public static ItemStack circuitController = new ItemStack(
			RailcraftItem.circuit.item(), 1, 0);
	public static ItemStack circuitSignal = new ItemStack(
			RailcraftItem.circuit.item(), 1, 2);

	public static ItemStack ieAirTank = new ItemStack(
			IEContent.itemToolUpgrades);
	public static ItemStack ieWoodenGrip = new ItemStack(
			IEContent.itemMaterial, 1, 9);

	public static ItemStack fluxTransformer = new ItemStack(
			RailcraftBlocks.getBlockMachineEpsilon(), 1, 4);
	//public static ItemStack trackWood = new ItemStack();
	public static ItemStack tinGearBushing = new ItemStack(
			RailcraftItem.gear.item(), 1, EnumGear.BUSHING.ordinal());
	public static ItemStack ironGear = new ItemStack(RailcraftItem.gear.item(),
			1, EnumGear.IRON.ordinal());

	public static ItemStack steelGear = new ItemStack(
			RailcraftItem.gear.item(), 1, EnumGear.STEEL.ordinal());
	public static ItemStack ieConcrete = new ItemStack(
			IEContent.blockStoneDecoration, 1, 4);
	public static ItemStack ieHempcrete = new ItemStack(
			IEContent.blockStoneDecoration, 1, 0);
	public static ItemStack arCircuitControl = new ItemStack(
			AdvancedRocketryItems.itemIC, 1, 3);
	public static ItemStack arCircuitIO = new ItemStack(
			AdvancedRocketryItems.itemIC, 1, 4);
	public static ItemStack arCircuitLiquidIO = new ItemStack(
			AdvancedRocketryItems.itemIC, 1, 5);
	public static ItemStack arCircuitBasic = new ItemStack(
			AdvancedRocketryItems.itemIC, 1, 0);
	public static ItemStack arCircuitAdv = new ItemStack(
			AdvancedRocketryItems.itemIC, 1, 2);
	public static ItemStack arCircuitTracking = new ItemStack(
			AdvancedRocketryItems.itemIC, 1, 1);

	public static ItemStack arWaferSilicon = new ItemStack(
			AdvancedRocketryItems.itemWafer);
	public static ItemStack arCircuitPlateBasic = new ItemStack(
			AdvancedRocketryItems.itemCircuitPlate, 1, 0);
	public static ItemStack arCircuitPlateAdv = new ItemStack(
			AdvancedRocketryItems.itemCircuitPlate, 1, 1);
	public static ItemStack arUi = new ItemStack(AdvancedRocketryItems.itemMisc);

	public static ItemStack arCarbonBrick = new ItemStack(
			AdvancedRocketryItems.itemMisc, 1, 1);

	//public static Item arNugget = MaterialRegistry.oreProducts[AllowedProducts
	//	.getProductByName("NUGGET").getFlagValue()];
	//public static ItemStack arTitaniumNugget = new ItemStack(arNugget, 1, MaterialRegistry.getItemStackFromMaterialAndType("titanium", product);
	public static ItemStack sulfur = new ItemStack(TFCItems.powder, 1, 3);
	public static ItemStack salt = new ItemStack(TFCItems.powder, 1, 9);
	public static ItemStack arOpticalSensor = new ItemStack(
			AdvancedRocketryItems.itemSatellitePrimaryFunction);
	public static ItemStack iceFresh = new ItemStack(TFCBlocks.ice, 1, 1);
	public static ItemStack iceSalt = new ItemStack(TFCBlocks.ice, 1, 0);

	public static ItemStack steamEngineHobbyist = new ItemStack(
			RailcraftBlocks.getBlockMachineBeta(), 1, 7);
}
