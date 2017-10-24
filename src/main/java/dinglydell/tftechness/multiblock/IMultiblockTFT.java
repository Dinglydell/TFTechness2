package dinglydell.tftechness.multiblock;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IMultiblockTFT {
	void restore(World world, int x, int y, int z, EnumFacing facing);

	ItemStack getDropAt(int x, int y, int z, int masterX, int masterY,
			int masterZ, EnumFacing facing);
}
