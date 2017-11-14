package dinglydell.tftechness.block;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.bioxx.tfc.Blocks.Devices.BlockBarrel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dinglydell.tftechness.tileentities.TileTreatedBarrel;

public class BlockTreatedBarrel extends BlockBarrel {
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileTreatedBarrel();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		//TODO: proper render colours instead of using chestnut as a hacky workaround
		par3List.add(new ItemStack(par1, 1, 3));
	}
}
