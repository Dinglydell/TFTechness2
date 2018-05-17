package dinglydell.tftechness.block;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.tileentities.TileMachineComponent;
import dinglydell.tftechness.tileentities.TileMachineHeatingElement;

public class BlockTFTComponent extends BlockContainer {

	public enum TFTComponents {
		wall(null), heater("info.machine.heater.tooltip");

		private String tooltip;

		TFTComponents(String tooltip) {
			this.tooltip = tooltip;
		}

		public boolean hasTooltip() {
			return tooltip != null;
		}

		public String getTooltip() {
			return tooltip;
		}
	}

	private IIcon[] icons;

	public BlockTFTComponent() {
		super(Material.iron);
		setHardness(5f);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TileMachineComponent tile = (TileMachineComponent) world
				.getTileEntity(x, y, z);
		return tile.getLightLevel();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < TFTComponents.values().length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	//@Override
	//public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
	//	TileMachineComponent tile = (TileMachineComponent) world
	//			.getTileEntity(x, y, z);
	//	if (tile.getTemperature() >= 100) {
	//		entity.attackEntityFrom(DamageSource.onFire,
	//				tile.getTemperature() / 100f);
	//	}
	//
	//}
	@Override
	public int onBlockPlaced(World p_149660_1_, int p_149660_2_,
			int p_149660_3_, int p_149660_4_, int p_149660_5_,
			float p_149660_6_, float p_149660_7_, float p_149660_8_,
			int p_149660_9_) {
		// TODO Auto-generated method stub
		return super.onBlockPlaced(p_149660_1_,
				p_149660_2_,
				p_149660_3_,
				p_149660_4_,
				p_149660_5_,
				p_149660_6_,
				p_149660_7_,
				p_149660_8_,
				p_149660_9_);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		switch (TFTComponents.values()[meta]) {
		case wall:
			return new TileMachineComponent();//.setConductivity(0.01f);//new TileMachineWall();
			//return new TileTFTElectrolyser();
		case heater:
			return new TileMachineHeatingElement();//.setConductivity(0.5f);
		default:
			return null;

		}

	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);

		((TileMachineComponent) world.getTileEntity(x, y, z)).CheckForMaster();
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z,
			int meta) {
		super.onBlockDestroyedByPlayer(world, x, y, z, meta);
		//restoreMultiblock(world, x, y, z);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		icons = new IIcon[TFTComponents.values().length];
		for (TFTComponents m : TFTComponents.values()) {
			icons[m.ordinal()] = register.registerIcon(TFTechness2.MODID
					+ ":machine/" + m.name().toLowerCase());
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return icons[meta];
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z,
			Explosion explosion) {

		//restoreMultiblock(world, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileMachineComponent te = (TileMachineComponent) world
					.getTileEntity(x, y, z);
			te.openGui(world, player);
		}

		return true;

	}

	public static ItemStack getBlockWithNBT(TFTComponents component,
			float conductivity) {
		ItemStack is = new ItemStack(TFTBlocks.machineComponent, 1,
				component.ordinal());
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("conductivity", conductivity);
		is.setTagCompound(nbt);
		return is;
	}

}
