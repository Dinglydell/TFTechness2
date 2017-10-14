package dinglydell.tftechness.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc.Reference;
import com.bioxx.tfc.Blocks.BlockMetalSheet;
import com.bioxx.tfc.api.Metal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dinglydell.tftechness.metal.MetalSnatcher;
import dinglydell.tftechness.tileentities.TETFTMetalSheet;

public class BlockTFTMetalSheet extends BlockMetalSheet {
	public Map<String, IIcon> icons = new HashMap();
	
	public BlockTFTMetalSheet() {
		super();
		
	}
	
	@Override
	public void registerBlockIcons(IIconRegister registerer) {
		for (Entry<String, Metal> ent : MetalSnatcher.getMetals().entrySet()) {
			Metal m = ent.getValue();
			icons.put(m.name, registerer.registerIcon(Reference.MOD_ID + ":" + "metal/" + m.name));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int i, int j, int k, int meta) {
		TETFTMetalSheet te = (TETFTMetalSheet) access.getTileEntity(i, j, k);
		if (te != null) {
			return icons.get(te.metal);
		} else {
			return icons.get("Tin");
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TETFTMetalSheet();
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return icons.get("Tin");
	}
}
