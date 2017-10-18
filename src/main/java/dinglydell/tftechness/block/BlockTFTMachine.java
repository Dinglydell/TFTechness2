package dinglydell.tftechness.block;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dinglydell.tftechness.tileentities.TileTFTElectrolyser;
import dinglydell.tftechness.tileentities.TileTFTMachineBase;

public class BlockTFTMachine extends BlockContainer {
	enum TFTMachines {
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
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		TileTFTMachineBase te = (TileTFTMachineBase) world.getTileEntity(x,
				y,
				z);
		te.openGui(world, player);
		return true;

	}

}
