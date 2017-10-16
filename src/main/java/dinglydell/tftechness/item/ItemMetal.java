package dinglydell.tftechness.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.bioxx.tfc.Core.Metal.MetalRegistry;
import com.bioxx.tfc.Items.ItemIngot;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.Metal;

public class ItemMetal extends ItemIngot {

	protected Metal metal;
	private String name;

	public ItemMetal(String metal, int amt, String name) {
		super();
		this.setMetal(metal, amt);
		this.name = name;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ) {
		return false;

	}

	@Override
	public ItemTerra setMetal(String m, int amt) {
		metal = MetalRegistry.instance.getMetalFromString(m);
		return super.setMetal(m, amt);
	}

	//@Override
	//public void registerIcons(IIconRegister reg) {
	//	//IconRegistry.addIcon("metalRod" + metal.name, TFTechness.MODID + ":metal/" + metal.name.replaceAll(" ", "")
	//	//		+ "Rod", reg);
	//}

	//@Override
	//public IIcon getIcon(ItemStack stack, int pass) {
	//	return IconRegistry.getIcon("metalRod" + metal.name);
	//}

	@Override
	public String getItemStackDisplayName(ItemStack is) {

		return StatCollector.translateToLocal("metal." + metal.name + ".name")
				+ " "
				+ StatCollector.translateToLocal("item." + name + ".name");
	}

}
