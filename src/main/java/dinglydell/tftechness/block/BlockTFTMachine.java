package dinglydell.tftechness.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import dinglydell.tftechness.tileentities.TileTFTElectrolyser;
import dinglydell.tftechness.tileentities.TileTFTMachineBase;

public class BlockTFTMachine extends BlockContainer {
	public enum TFTMachines {
		electrolyser
	}

	public BlockTFTMachine() {
		super(Material.iron);

	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < TFTMachines.values().length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		switch (TFTMachines.values()[meta]) {
		case electrolyser:
			return new TileTFTElectrolyser();
		default:
			return null;

		}

	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z,
			int meta) {
		super.onBlockDestroyedByPlayer(world, x, y, z, meta);
		//restoreMultiblock(world, x, y, z);

	}

	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z,
			Explosion explosion) {

		//restoreMultiblock(world, x, y, z);
	}

	private void restoreMultiblock(World world, int x, int y, int z) {
		TileTFTMachineBase tile = (TileTFTMachineBase) world.getTileEntity(x,
				y,
				z);
		if (!tile.isRestoring) {
			//drop contents
			ItemStack block = tile.getMultiblock().getDropAt(x,
					y,
					z,
					tile.getMasterX(),
					tile.getMasterY(),
					tile.getMasterZ(),
					tile.getMaster().getFacing());
			if (block != null) {
				dropBlockAsItem(world, x, y, z, block);
			}

			for (int i = 0; i < tile.getMaster().getSizeInventory(); i++) {
				ItemStack stack = tile.getMaster().getStackInSlot(i);
				if (stack != null) {
					dropBlockAsItem(world, x, y, z, stack);
				}
			}
			//restore
			tile.getMultiblock().restore(world,
					tile.getMasterX(),
					tile.getMasterY(),
					tile.getMasterZ(),
					tile.getMaster().getFacing());

		}

	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		restoreMultiblock(world, x, y, z);

	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
			int metadata, int fortune) {

		return new ArrayList<ItemStack>();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileTFTMachineBase te = (TileTFTMachineBase) world.getTileEntity(x,
					y,
					z);
			te.openGui(world, player);
		}

		return true;

	}

}
