package dinglydell.tftechness.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidStack;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.metal.MetalStat;
import dinglydell.tftechness.tileentities.TileMoltenMetal;

public class BlockMoltenMetal extends BlockFluidClassic implements
		ITileEntityProvider {
	private MetalStat stat;

	public BlockMoltenMetal(FluidMoltenMetal fluid) {
		super(fluid, Material.lava);
		this.stat = TFTechness2.statMap.get((fluid).getMetalName());
		setStackTemperature(stack, stat.heat.meltTemp);
		setBlockName(fluid.getUnlocalizedName());

		//this.solidBlock = solidBlock;
		//this.solidMeta = solidMeta;

	}

	private void setStackTemperature(FluidStack stack, float temperature) {
		if (stack.tag == null) {
			stack.tag = new NBTTagCompound();
		}
		stack.tag.setFloat("temperature", temperature);
	}

	@Override
	public BlockFluidClassic setFluidStack(FluidStack stack) {
		if (!stack.tag.hasKey("Temperature")) {
			setStackTemperature(stack, stat.heat.meltTemp);
		}
		return super.setFluidStack(stack);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMoltenMetal(stack);

	}

	@Override
	public String getLocalizedName() {

		return stack.getLocalizedName();
	}

}
