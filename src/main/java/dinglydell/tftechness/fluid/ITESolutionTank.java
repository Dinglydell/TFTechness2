package dinglydell.tftechness.fluid;

public interface ITESolutionTank {
	/** Attempt to displace this much fluid from the tank into neighbour tanks */
	public int attemptOverflow(int overVol, boolean doOverflow);

	public SolutionTank getTank();
}
