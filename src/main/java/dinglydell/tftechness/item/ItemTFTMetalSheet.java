package dinglydell.tftechness.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.bioxx.tfc.Items.ItemMetalSheet;

import dinglydell.tftechness.block.TFTBlocks;
import dinglydell.tftechness.tileentities.TETFTMetalSheet;

public class ItemTFTMetalSheet extends ItemMetalSheet {
	public String metal;

	public ItemTFTMetalSheet(String metal) {
		super(0);
		this.metal = metal;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ) {
		//boolean success = super.onItemUse(itemstack, entityplayer, world, x, y, z, side, hitX, hitY, hitZ);
		boolean success = false;
		int[] sides = sidesMap[side];
		if (!world.isRemote && world.getBlock(x, y, z) != TFTBlocks.metalSheet
				&& valid(world, sides[0] + x, sides[1] + y, sides[2] + z)) {
			itemstack.stackSize--;
			world.setBlock(sides[0] + x,
					sides[1] + y,
					sides[2] + z,
					TFTBlocks.metalSheet);
			TETFTMetalSheet te = (TETFTMetalSheet) world.getTileEntity(sides[0]
					+ x, sides[1] + y, sides[2] + z);
			te.metal = this.metal;
			te.sheetStack = itemstack.copy();
			te.sheetStack.stackSize = 1;
			te.toggleBySide(flipSide(side), true);
			success = true;
		}
		return success;
	}

	@Override
	public boolean isValid(World world, int i, int j, int k) {
		Block block = world.getBlock(i, j, k);
		if (block == TFTBlocks.metalSheet
				&& world.getTileEntity(i, j, k) instanceof TETFTMetalSheet) {
			TETFTMetalSheet te = (TETFTMetalSheet) world.getTileEntity(i, j, k);
			if (this.metal.equals(te.metal))
				return true;
		}
		return false;
	}

	public boolean valid(World world, int i, int j, int k) {
		Block block = world.getBlock(i, j, k);
		if (block.isAir(world, i, j, k))
			return true;

		return isValid(world, i, j, k);
	}
}
