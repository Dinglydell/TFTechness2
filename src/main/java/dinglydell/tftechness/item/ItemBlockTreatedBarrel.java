package dinglydell.tftechness.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc.Items.ItemBlocks.ItemBarrels;

public class ItemBlockTreatedBarrel extends ItemBarrels {

	public ItemBlockTreatedBarrel(Block block) {
		super(block);
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List list) {

		list.add(new ItemStack(par1, 1, 3));
	}

}
