package dinglydell.tftechness.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TFTItems {
	public static ItemStack woodenBucketCreosote;
	public static Item ore;
	public static Item nuggetMold;
	public static Item crushedBauxite;
	public static Item alumina;
	public static Item basicRailbed;

	/** maps cropName -> seed item */
	public static Map<String, Item> seed = new HashMap<String, Item>();

	public static Map<String, Item> unshaped = new HashMap<String, Item>();
	public static Map<String, Item> ingots = new HashMap<String, Item>();
	public static Map<String, Item> ingots2x = new HashMap<String, Item>();
	public static Map<String, Item> sheets = new HashMap<String, Item>();
	public static Map<String, Item> sheets2x = new HashMap<String, Item>();
	public static Map<String, Item> rods = new HashMap<String, Item>();
	public static Map<String, Item> nuggets = new HashMap<String, Item>();
	public static Item hopGraphite;
	public static Item controlCircuitUnfinished;
	public static Item ioCircuitUnfinished;
	public static Item liquidIoCircuitUnfinished;
	public static Item lumberTreatedWood;

}
