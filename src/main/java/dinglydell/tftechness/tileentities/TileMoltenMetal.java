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
	private float temperature;
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
		nbt.setFloat("Temperature", temperature);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		stack = FluidStack.loadFluidStackFromNBT(nbt);
		temperature = nbt.getFloat("Temperature");
		setBlockAndMeta();
	}

	private void setBlockAndMeta() {
		ItemStack is = OreDictionary.getOres("block"
				+ TFTechness2.materialMap.get(((FluidMoltenMetal) stack
						.getFluid()).getMetalName()).oreName).get(0);
		solidBlock = ((ItemBlock) is.getItem()).field_150939_a;
		solidMeta = is.getItemDamage();

	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			return;
		}
		float externalTemp = TFC_Climate.getHeightAdjustedTemp(worldObj,
				xCoord,
				yCoord,
				zCoord);
		this.temperature -= stats.conductivity * (temperature - externalTemp)
				/ (stats.getSISpecificHeat());
		((FluidMoltenMetal) stack.getFluid())
				.setTemperature(temperature, stack);
		if (this.temperature < stats.heat.meltTemp && solidBlock != null
				&& blockMetadata == 0) {
			worldObj.setBlock(xCoord, yCoord, zCoord, solidBlock, solidMeta, 3);
		}
	}

	public void setTemperature(float temp) {
		this.temperature = temp;

	}

	public float getTemperature() {
		return temperature;
	}

	public void addThermalEnergy(float dE) {
		temperature += dE / (stats.getSISpecificHeat());

	}

	public float getConductivity() {

		return stats.conductivity;
	}

}
