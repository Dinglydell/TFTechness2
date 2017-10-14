package dinglydell.tftechness.metal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.bioxx.tfc.Core.Metal.Alloy;
import com.bioxx.tfc.Items.ItemIngot;
import com.bioxx.tfc.Items.ItemMeltedMetal;
import com.bioxx.tfc.api.HeatRaw;
import com.bioxx.tfc.api.Metal;
import com.bioxx.tfc.api.Enums.EnumSize;

import cpw.mods.fml.common.registry.GameRegistry;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.item.ItemRod;
import dinglydell.tftechness.item.ItemTFTMetalSheet;
import dinglydell.tftechness.item.TFTItems;

public class Material {
	
	public Item ingot;
	public Item ingot2x;
	public Item sheet;
	public Item sheet2x;
	public Item unshaped;
	public Item rod;
	public ItemStack nugget;
	
	public HeatRaw heatRaw;
	public int tier;
	public String name;
	public Metal metal;
	public Alloy.EnumTier alloyTier;
	public String oreName;
	
	private Material(String name, int tier, ItemStack nugget) {
		this.heatRaw = TFTechness2.statMap.get(name).heat;
		this.name = name;
		this.oreName = name;
		this.tier = tier;
		this.nugget = nugget;
	}
	
	public Material(String name, int tier, Alloy.EnumTier alloyTier, ItemStack nugget) {
		this(name, tier, nugget);
		this.alloyTier = alloyTier;
	}
	
	public void initialise(){
		addUnshaped();
		addIngots();
		addSheets();
		addRod();
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
		.setUnlocalizedName(name + "Sheet"))
		.setMetal(name, 200);

		TFTItems.sheets.put(name, sheet);
		GameRegistry.registerItem(sheet, name + "Sheet");
		OreDictionary.registerOre("plate" + name, sheet);
		
		sheet2x = ((ItemTFTMetalSheet) new ItemTFTMetalSheet(name)
		.setUnlocalizedName(name + "Sheet2x")).setMetal(name,
		400);

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
		unshaped = new ItemMeltedMetal().setUnlocalizedName(name
				+ "Unshaped");

		TFTItems.unshaped.put(name, unshaped);
		GameRegistry.registerItem(unshaped, "Unshaped" + name);
		
	}

}
