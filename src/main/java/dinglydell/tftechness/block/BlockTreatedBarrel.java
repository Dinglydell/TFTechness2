package dinglydell.tftechness.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.bioxx.tfc.Blocks.Devices.BlockBarrel;

import dinglydell.tftechness.tileentities.TileTreatedBarrel;

public class BlockTreatedBarrel extends BlockBarrel {
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileTreatedBarrel();
	}
}
