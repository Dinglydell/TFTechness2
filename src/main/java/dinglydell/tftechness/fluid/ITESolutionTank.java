package dinglydell.tftechness.fluid;

import net.minecraftforge.common.util.ForgeDirection;

public interface ITESolutionTank {
	/** Attempt to displace this much fluid from the tank into neighbour tanks */
	public int attemptOverflow(int overVol, ForgeDirection from,
			boolean doOverflow);

	public SolutionTank getTank();

	public float getTemperature();
}
