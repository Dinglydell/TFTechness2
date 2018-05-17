package dinglydell.tftechness.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.BlockTFTComponent;
import dinglydell.tftechness.block.BlockTFTComponent.TFTComponents;
import dinglydell.tftechness.tileentities.TileMachineComponent;

public class ItemBlockMachineComponent extends ItemBlock {

	protected BlockTFTComponent block;

	public ItemBlockMachineComponent(Block block) {
		super(block);
		this.block = (BlockTFTComponent) block;
		setHasSubtypes(true);
		setMaxDamage(0);

	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	//
	//@Override
	//@SideOnly(Side.CLIENT)
	//public void getSubItems(Item item, CreativeTabs tab, List list) {
	//
	//	for (int i = 0; i < TFTComponents.values().length; i++) {
	//		list.add(new ItemStack(item, 1, i));
	//	}
	//}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile."
				+ BlockTFTComponent.TFTComponents.values()[stack
						.getItemDamage()].name();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list,
			boolean flag) {
		list.add(EnumChatFormatting.LIGHT_PURPLE.toString()
				+ EnumChatFormatting.ITALIC.toString()
				+ StatCollector.translateToLocal("info.machine.tooltip.0"));
		list.add(EnumChatFormatting.LIGHT_PURPLE.toString()
				+ EnumChatFormatting.ITALIC.toString()
				+ StatCollector.translateToLocal("info.machine.tooltip.1"));
		list.add(EnumChatFormatting.LIGHT_PURPLE.toString()
				+ EnumChatFormatting.ITALIC.toString()
				+ StatCollector.translateToLocal("info.machine.tooltip.2"));
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			list.add("Broken data!");
		} else {
			list.add(EnumChatFormatting.DARK_AQUA.toString() + "Conductivity: "
					+ EnumChatFormatting.RED.toString()
					+ Math.round(100 * tag.getFloat("conductivity")) + "%");
		}
		TFTComponents component = TFTComponents.values()[stack.getItemDamage()];
		if (component.hasTooltip()) {
			list.add(EnumChatFormatting.RED.toString()
					+ StatCollector.translateToLocal(component.getTooltip()));
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ, int metadata) {
		if (super.placeBlockAt(stack,
				player,
				world,
				x,
				y,
				z,
				side,
				hitX,
				hitY,
				hitZ,
				metadata)) {
			TileMachineComponent tile = (TileMachineComponent) world
					.getTileEntity(x, y, z);
			if (stack.hasTagCompound()) {
				tile.setConductivity(stack.getTagCompound()
						.getFloat("conductivity"));
			} else {
				TFTechness2.logger
						.warn("Placed a TFT component with no conductivity value!");
			}
			return true;
		}
		return false;
	}
}
