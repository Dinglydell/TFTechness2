package dinglydell.tftechness.tileentities;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.bioxx.tfc.TileEntities.TEBarrel;

public class TileTreatedBarrel extends TEBarrel implements IFluidHandler {

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!getSealed() && fluid == null
				|| resource.getFluidID() == fluid.getFluidID()) {
			int afterFill = Math.min(getMaxLiquid(), (fluid == null ? 0
					: fluid.amount) + resource.amount);
			if (doFill) {
				fluid = new FluidStack(resource.getFluid(), afterFill);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return afterFill;
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if (getSealed() || fluid == null
				|| resource.getFluidID() != fluid.getFluidID()) {
			return null;
		}
		int beforeDrain = fluid.amount;
		int afterDrain = Math.max(0, fluid.amount - resource.amount);
		if (doDrain) {
			fluid.amount = afterDrain;
			if (afterDrain == 0) {
				actionEmpty();
			} else {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}

		return new FluidStack(resource.getFluid(), beforeDrain - afterDrain);

	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (fluid == null) {
			return null;
		}
		return drain(from, new FluidStack(fluid.getFluid(), maxDrain), doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fill) {

		return fluid == null || fluid.equals(fill);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid drain) {

		return fluid != null && fluid.equals(drain);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { new FluidTankInfo(fluid, getMaxLiquid()) };
	}

}
