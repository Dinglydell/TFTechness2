package dinglydell.tftechness.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.bioxx.tfc.WorldGen.Generators.OreSpawnData;
import com.bioxx.tfc.WorldGen.Generators.WorldGenOre;
import com.bioxx.tfc.api.Metal;
import com.bioxx.tfc.api.TFCOptions;
import com.bioxx.tfc.api.Interfaces.ISmeltable.EnumTier;
import com.google.common.primitives.Shorts;

import cpw.mods.fml.common.registry.GameRegistry;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.item.ItemTFTOre;
import dinglydell.tftechness.item.TFTItems;

public class TFTOreRegistry {

	private static List<TFTOre> ores = new ArrayList<TFTOre>();

	public static void registerOre(TFTOre ore) {
		ores.add(ore);
	}

	public static void registerAllOreBlocks() {
		List<String> itemOreNames = new ArrayList<String>();
		List<Metal> itemOreMetals = new ArrayList<Metal>();
		List<Short> itemOreAmounts = new ArrayList<Short>();
		List<EnumTier> itemOreTiers = new ArrayList<EnumTier>();
		for (int i = 0; i < ores.size(); i++) {
			if (i % 16 == 0) {
				GameRegistry.registerBlock(new BlockTFTOre(i / 16,
						getBlockNames(i / 16)), "ore" + i / 16);
			}
			if (ores.get(i).drop == null) {
				Metal metal = ores.get(i).metal;
				EnumTier tier = ores.get(i).tier;
				ores.get(i).meta = itemOreNames.size();

				itemOreNames.add("Normal " + ores.get(i).name);
				itemOreAmounts.add((short) TFCOptions.normalOreUnits);
				itemOreMetals.add(metal);
				itemOreTiers.add(tier);

				itemOreNames.add("Rich " + ores.get(i).name);
				itemOreAmounts.add((short) TFCOptions.richOreUnits);
				itemOreMetals.add(metal);
				itemOreTiers.add(tier);

				itemOreNames.add("Poor " + ores.get(i).name);
				itemOreAmounts.add((short) TFCOptions.poorOreUnits);
				itemOreMetals.add(metal);
				itemOreTiers.add(tier);

			}

			OreSpawnData data = ores.get(i).getSpawnData(TFTechness2.MODID
					+ ":ore" + i / 16,
					i % 16);
			WorldGenOre.oreList.put(ores.get(i).name, data);
		}

		int size = itemOreNames.size();
		TFTItems.ore = new ItemTFTOre(itemOreNames.toArray(new String[size]),
				itemOreMetals.toArray(new Metal[size]),
				Shorts.toArray(itemOreAmounts),
				itemOreTiers.toArray(new EnumTier[size]))
				.setUnlocalizedName("ore");
		GameRegistry.registerItem(TFTItems.ore, "ore");
		((ItemTFTOre) TFTItems.ore).registerOreDict();

	}

	public static int getDamageDropped(int num, int dmg) {
		TFTOre ore = ores.get(num * 16 + dmg);
		if (ore.drop != null) {
			return ore.drop.getItemDamage();
		}
		return ore.meta;
	}

	private static String[] getBlockNames(int num) {
		List<String> names = new ArrayList<String>();
		for (TFTOre ore : ores.subList(num * 16,
				Math.min((num + 1) * 16, ores.size()))) {
			names.add(ore.name);
		}
		return names.toArray(new String[names.size()]);
	}

	public static ItemStack getDrop(int num, int metadata, int grade) {
		TFTOre ore = ores.get(num * 16 + metadata);
		if (ore.drop != null) {
			return ore.drop;
		}
		return new ItemStack(TFTItems.ore, 1, ore.meta + grade);
	}

}
