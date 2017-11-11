package dinglydell.tftechness.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.bioxx.tfc.Items.ItemOre;
import com.bioxx.tfc.api.Metal;

public class ItemTFTOre extends ItemOre {
	Metal[] metals;
	short[] amounts;
	EnumTier[] tiers;

	public ItemTFTOre(String[] names, Metal[] metals, short[] amounts,
			EnumTier[] tiers) {
		super();
		this.setHasSubtypes(true);
		metaNames = names;
		this.metals = metals;
		this.amounts = amounts;
		this.tiers = tiers;

	}

	public void registerOreDict() {
		for (int i = 0; i < metaNames.length; i++) {
			String name = metaNames[i];
			OreDictionary.registerOre("ore" + name.replaceAll("\\s+", ""),
					new ItemStack(this, 1, i));
		}

	}

	@Override
	public Metal getMetalType(ItemStack is) {
		return metals[is.getItemDamage()];
	}

	@Override
	public short getMetalReturnAmount(ItemStack is) {
		return this.amounts[is.getItemDamage()];
	}

	@Override
	public boolean isSmeltable(ItemStack is) {

		return this.metals[is.getItemDamage()] != null;
	}

	@Override
	public EnumTier getSmeltTier(ItemStack is) {
		return tiers[is.getItemDamage()];
	}

}
