package dinglydell.tftechness.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import com.bioxx.tfc.Blocks.Terrain.BlockOre;
import com.bioxx.tfc.TileEntities.TEOre;
import com.bioxx.tfc.api.TFCItems;

public class BlockTFTOre extends BlockOre {
	private int num;

	public BlockTFTOre(int num, String[] nameList) {
		super(Material.rock);
		setHardness(10F);
		setResistance(10F);
		setBlockName("Ore");
		this.num = num;

		blockNames = nameList;
	}

	@Override
	public int damageDropped(int dmg) {
		return TFTOreRegistry.getDamageDropped(num, dmg);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 1;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
			int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		int count = quantityDropped(metadata, fortune, world.rand);
		TEOre te = (TEOre) world.getTileEntity(x, y, z);
		int grade = getGrade(te);
		ret.add(TFTOreRegistry.getDrop(num, metadata, grade));
		return ret;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x,
			int y, int z) {
		if (!world.isRemote) {
			boolean dropOres = false;
			if (player != null) {
				player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)],
						1);
				player.addExhaustion(0.075F);
				dropOres = player.canHarvestBlock(this);
			}

			if (player == null || dropOres) {
				int meta = world.getBlockMetadata(x, y, z);
				TEOre te = (TEOre) world.getTileEntity(x, y, z);
				int grade = getGrade(te);
				ItemStack itemstack = TFTOreRegistry.getDrop(num, meta, grade)
						.copy();

				//TFTechness2.logger.debug(grade);
				dropBlockAsItem(world, x, y, z, itemstack);
			}
		}
		return world.setBlockToAir(x, y, z);
	}

	private int getGrade(TEOre te) {
		return te.extraData & 7;
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion exp) {
		ItemStack itemstack;
		int meta = world.getBlockMetadata(x, y, z);
		itemstack = new ItemStack(TFCItems.oreChunk, 1, meta + 32);
		dropBlockAsItem(world, x, y, z, itemstack);
		onBlockDestroyedByExplosion(world, x, y, z, exp);
	}

}
