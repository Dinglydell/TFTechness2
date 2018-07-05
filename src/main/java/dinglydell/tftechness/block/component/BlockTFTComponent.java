package dinglydell.tftechness.block.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dinglydell.tftechness.block.TFTBlocks;
import dinglydell.tftechness.block.component.property.ComponentProperty;
import dinglydell.tftechness.tileentities.TileMachineComponent;

public class BlockTFTComponent extends BlockContainer {

	//public enum TFTComponents {
	//	wall(null, null), heater("info.machine.heater.tooltip",
	//			new String[] { "tier" }), shelf("info.machine.shelf.tooltip",
	//			null), cooler("info.machine.cooler.tooltip",
	//			new String[] { "tier" });
	//
	//	private String tooltip;
	//	private String[] properties;
	//
	//	TFTComponents(String tooltip, String[] properties) {
	//		this.tooltip = tooltip;
	//		this.properties = properties;
	//	}
	//
	//	public boolean hasTooltip() {
	//		return tooltip != null || properties != null;
	//	}
	//
	//	public String getTooltip() {
	//		return tooltip;
	//	}
	//
	//	public void addTooltip(List list, NBTTagCompound nbt) {
	//		list.add(EnumChatFormatting.RED.toString()
	//				+ StatCollector.translateToLocal(tooltip));
	//		if (properties != null && nbt != null) { //TODO: better system than just enums - make it dynamic
	//			for (String prop : properties) {
	//				list.add(EnumChatFormatting.DARK_AQUA.toString()
	//						+ StatCollector
	//								.translateToLocal("info.machine.property."
	//										+ prop) + " "
	//						+ EnumChatFormatting.RED.toString()
	//						+ nbt.getTag(prop).toString());
	//			}
	//		}
	//	}
	//
	//	public boolean hasProperties() {
	//		return properties != null;
	//	}
	//
	//	public String[] getProperties() {
	//		return properties;
	//	}
	//
	//	public IIcon[] registerIcon(IIconRegister register) {
	//		List<IIcon> icons = new ArrayList<IIcon>();
	//		icons.add(register.registerIcon(TFTechness2.MODID + ":machine/"
	//				+ name().toLowerCase()));
	//		if (this == TFTComponents.cooler) {
	//			icons.add(register.registerIcon(TFTechness2.MODID + ":machine/"
	//					+ name().toLowerCase() + "_cool"));
	//		}
	//		return icons.toArray(new IIcon[icons.size()]);
	//	}
	//}

	private IIcon[][] icons;
	public final Component component;

	public BlockTFTComponent(Component component) {
		super(Material.iron);
		setHardness(5f);
		this.component = component;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TileMachineComponent tile = (TileMachineComponent) world
				.getTileEntity(x, y, z);
		return tile == null ? 0 : tile.getLightLevel();
	}

	//@Override
	//public void getSubBlocks(Item item, CreativeTabs tab, List list) {
	//	for (int i = 0; i < TFTComponents.values().length; i++) {
	//		list.add(new ItemStack(item, 1, i));
	//	}
	//}

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
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
			int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		return drops;

	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return component.createNewTileEntity(worldIn, meta);
		//TileMachineComponent tile;
		//switch (TFTComponents.values()[meta]) {
		//
		//case wall:
		//	tile = new TileMachineComponent();//.setConductivity(0.01f);//new TileMachineWall();
		//	break;
		////return new TileTFTElectrolyser();
		//case heater:
		//	tile = new TileMachineHeatingElement();//.setConductivity(0.5f);
		//	break;
		//case shelf:
		//	tile = new TileMachineComponentItemShelf();
		//	break;
		//case cooler:
		//	tile = new TileMachineCoolingElement();
		//	break;
		//default:
		//	return null;
		//
		//}
		////tile.setTemperature(TFC_Climate.getHeightAdjustedTemp(worldIn, x, y, z))
		//return tile;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		TileMachineComponent tile = (TileMachineComponent) world
				.getTileEntity(x, y, z);
		tile.onDestroy();
		if (tile.getTemperature() > tile.getMaxTemperature()) {
			return;
		}
		if (tile instanceof IInventory) {
			IInventory inv = (IInventory) tile;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack item = inv.getStackInSlotOnClosing(i);
				if (item != null) {
					dropBlockAsItem(world, x, y, z, item);
				}
			}
		}
		ItemStack dropStack = new ItemStack(this, 1);
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeComponentPropertiesToNBT(nbt);
		dropStack.setTagCompound(nbt);

		dropBlockAsItem(world, x, y, z, dropStack);

	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side,
			float p_149660_6_, float p_149660_7_, float p_149660_8_, int meta) {
		//TileMachineComponent tile = (TileMachineComponent) world
		//	.getTileEntity(x, y, z);
		//tile.setPlacedSide(side);
		return side;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		TileMachineComponent t = (TileMachineComponent) world.getTileEntity(x,
				y,
				z);
		t.initialiseComponent();
		//super.onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
	}

	//@Override
	//public void onBlockDestroyedByPlayer(World world, int x, int y, int z,
	//		int meta) {
	//	super.onBlockDestroyedByPlayer(world, x, y, z, meta);
	//	//restoreMultiblock(world, x, y, z);
	//
	//}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileMachineComponent tile = (TileMachineComponent) world
				.getTileEntity(x, y, z);
		return tile.getIcon(side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return component.getIcon(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		component.registerIcons(register);
		//icons = new IIcon[TFTComponents.values().length][];
		//for (TFTComponents m : TFTComponents.values()) {
		//	icons[m.ordinal()] = m.registerIcon(register);
		//}

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

	public static ItemStack getBlockWithNBT(Component component,
			List<ComponentMaterial> materials) {
		ItemStack is = new ItemStack(TFTBlocks.components.get(component));//component.ordinal());
		NBTTagCompound nbt = new NBTTagCompound();

		//		Map<ComponentProperty, Object> propValues = new HashMap<ComponentProperty, Object>();
		for (int i = 0; i < component.propertySets.size(); i++) {
			ComponentProperty[] propSet = component.propertySets.get(i).properties;
			ComponentMaterial material = materials.get(i);
			for (ComponentProperty prop : propSet) {
				nbt.setString(prop.name, material.name);
				//prop.setNBT(nbt, material.validFor.get(prop));
			}

		}

		//nbt.setFloat("Conductivity", conductivity);
		//nbt.setInteger("tier", wireTier.ordinal());
		is.setTagCompound(nbt);
		return is;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		((TileMachineComponent) world.getTileEntity(x, y, z))
				.randomDisplayTick(rand);
	}

}
