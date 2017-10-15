package dinglydell.tftechness.block;

import net.minecraft.item.ItemStack;

import com.bioxx.tfc.WorldGen.Generators.OreSpawnData;
import com.bioxx.tfc.api.Metal;
import com.bioxx.tfc.api.Interfaces.ISmeltable.EnumTier;

public class TFTOre {
	public static EnumTier tier;
	public Metal metal;
	public String name;
	public ItemStack drop;
	public OreSpawnData data;
	private String type;
	private String size;
	private int rarity;
	private String[] baseRocks;
	private int minY;
	private int maxY;
	private int hDensity;
	private int vDensity;

	public int meta;

	/** TFT will automatically create an ore item that is dropped from the block */
	public TFTOre(String name, String type, String size, int rarity,
			String[] baseRocks, int minY, int maxY, int vDensity, int hDensity) {
		this.name = name;

		this.type = type;
		this.size = size;
		this.rarity = rarity;
		this.baseRocks = baseRocks;
		this.minY = minY;
		this.maxY = maxY;
		this.hDensity = hDensity;
		this.vDensity = vDensity;
	}

	/** TFT will automatically create an ore item which produces the given metal */
	public TFTOre(String name, Metal metal, EnumTier tier, String type,
			String size, int rarity, String[] baseRocks, int minY, int maxY,
			int vDensity, int hDensity) {
		this(name, type, size, rarity, baseRocks, minY, maxY, vDensity,
				hDensity);
		this.tier = tier;
		this.metal = metal;
	}

	/**
	 * This overload manually sets the ore's drop. TFT will not automatically
	 * create an item.
	 */
	public TFTOre(String name, ItemStack drop, String type, String size,
			int rarity, String[] baseRocks, int minY, int maxY, int vDensity,
			int hDensity) {
		this(name, type, size, rarity, baseRocks, minY, maxY, vDensity,
				hDensity);
		this.drop = drop;

	}

	public OreSpawnData getSpawnData(String block, int meta) {
		return new OreSpawnData(type, size, block, meta, rarity, baseRocks,
				minY, maxY, vDensity, hDensity);
	}
}
