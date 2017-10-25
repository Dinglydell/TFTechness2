package dinglydell.tftechness.item;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import blusunrize.immersiveengineering.common.IEContent;

import com.bioxx.tfc.api.TFCItems;

import dinglydell.tftechness.TFTechness2;

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

	public static ItemStack hopGraphite = new ItemStack(IEContent.itemMetal, 1,
			19);

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
	public static ItemStack ieMvWire = new ItemStack(IEContent.itemWireCoil, 1,
			1);
	public static ItemStack ieHvWire = new ItemStack(IEContent.itemWireCoil, 1,
			2);
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
	public static ItemStack ieSteelScaffolding = new ItemStack(
			IEContent.blockMetalDecoration, 1, 1);
	public static ItemStack ieHeavyEngineering = new ItemStack(
			IEContent.blockMetalDecoration, 1, 5);
	public static ItemStack ieLightEngineering = new ItemStack(
			IEContent.blockMetalDecoration, 1, 7);
	public static ItemStack ieSteelBlock = new ItemStack(
			IEContent.blockStorage, 1, 7);
	public static ItemStack ieSteelSlab = new ItemStack(
			IEContent.blockStorageSlabs, 1, 7);
}
