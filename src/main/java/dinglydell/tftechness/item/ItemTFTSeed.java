package dinglydell.tftechness.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.bioxx.tfc.Items.ItemCustomSeeds;
import com.bioxx.tfc.TileEntities.TECrop;

import dinglydell.tftechness.block.TFTBlocks;

public class ItemTFTSeed extends ItemCustomSeeds {

	private int cropId;

	public ItemTFTSeed(int cropId) {
		super(cropId);
		this.cropId = cropId;
	}

	//TODO: maybe change this? it's a bit of a hack
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		boolean success = super.onItemUse(stack,
				player,
				world,
				x,
				y,
				z,
				side,
				hitX,
				hitY,
				hitZ);

		if (success) {
			world.setBlock(x, y, z, TFTBlocks.crops);
			TECrop te = (TECrop) world.getTileEntity(x, y + 1, z);
			te.cropId = cropId;
			world.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);

		}

		return success;
	}
}
