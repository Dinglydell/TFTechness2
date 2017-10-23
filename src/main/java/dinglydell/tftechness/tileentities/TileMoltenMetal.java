package dinglydell.tftechness.tileentities;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.bioxx.tfc.Core.TFC_Climate;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.metal.MetalStat;

public class TileMoltenMetal extends TileEntity {
	protected static final float coolingCoefficient = 0.01f;

	private FluidStack stack;
	private int temperature;
	private Block solidBlock;
	private int solidMeta;
	private MetalStat stats;

	public TileMoltenMetal(FluidStack stack) {
		this.stack = stack;
		//this.solidBlock = solidBlock;
		//this.solidMeta = solidMeta;

		this.temperature = stack.getFluid().getTemperature(stack);
		String metal = ((FluidMoltenMetal) stack.getFluid()).getMetalName();
		this.stats = TFTechness2.statMap.get(metal);
		setBlockAndMeta();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		super.writeToNBT(nbt);

		stack.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		stack = FluidStack.loadFluidStackFromNBT(nbt);

		setBlockAndMeta();
	}

	private void setBlockAndMeta() {
		ItemStack is = OreDictionary.getOres("block"
				+ ((FluidMoltenMetal) stack.getFluid()).getMetalName()).get(0);
		solidBlock = ((ItemBlock) is.getItem()).field_150939_a;
		solidMeta = is.getItemDamage();

	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		float externalTemp = TFC_Climate.getHeightAdjustedTemp(worldObj,
				xCoord,
				yCoord,
				zCoord);
		this.temperature -= coolingCoefficient * (temperature - externalTemp);
		((FluidMoltenMetal) stack.getFluid())
				.setTemperature(temperature, stack);
		if (this.temperature < stats.heat.meltTemp && solidBlock != null) {
			worldObj.setBlock(xCoord, yCoord, zCoord, solidBlock, solidMeta, 2);
		}
	}

}
