package dinglydell.tftechness.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import dinglydell.tftechness.block.BlockTFTOre;

public class ItemBlockTFTOre extends ItemBlock {
	protected BlockTFTOre block;

	public ItemBlockTFTOre(Block block) {
		super(block);
		this.block = (BlockTFTOre) block;

	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile." + block.blockNames[stack.getItemDamage()];
	}
}
