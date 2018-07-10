package dinglydell.tftechness.fluid;

import java.util.Set;

public interface ITESolutionTank {
	/** Attempt to displace this much fluid from the tank into neighbour tanks */
	public float attemptOverflow(float overVol, Set<ITESolutionTank> from,
			boolean doOverflow);

	public SolutionTank getTank();

	public float getTemperature();

	public double getAtmosphericPressure();

	public double getMaxPressure();
}
