package dinglydell.tftechness.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dinglydell.tftechness.tileentities.TileTFTMachineController;

public class BlockTFTController extends BlockContainer {

	public BlockTFTController() {
		super(Material.iron);

	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileTFTMachineController te = (TileTFTMachineController) world
					.getTileEntity(x, y, z);
			te.openGui(world, player);
		}

		return true;

	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);

		((TileTFTMachineController) world.getTileEntity(x, y, z))
				.CheckForComponents();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {

		return new TileTFTMachineController();
	}
}
