package dinglydell.tftechness.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockAutoAnvil extends BlockContainer {

	enum Action {
		lightHit, mediumHit, heavyHit, draw, punch, bend, upset, shrink
	}

	protected BlockAutoAnvil(Material m) {
		super(m);
		//BlockCactus
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_,
			int p_149668_2_, int p_149668_3_, int p_149668_4_) {
		float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox((double) ((float) p_149668_2_ + f),
				(double) p_149668_3_,
				(double) ((float) p_149668_4_ + f),
				(double) ((float) (p_149668_2_ + 1) - f),
				(double) ((float) (p_149668_3_ + 1) - f),
				(double) ((float) (p_149668_4_ + 1) - f));
	}

}
