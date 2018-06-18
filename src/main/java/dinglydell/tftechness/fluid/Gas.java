package dinglydell.tftechness.fluid;

public class Gas {
	public static Gas AIR = new Gas("air", -273);
	public static Gas STEAM = new Gas("steam", 100);

	public String gasName;
	private float boilingPoint;

	public Gas(String name, float boilingPoint) {
		this.gasName = name;
		this.boilingPoint = boilingPoint;
	}

	/** Returns the boiling point of the gas in degrees celsius */
	public float getBoilingPoint() {
		return boilingPoint;
	}

	//public FluidStack condense(float amt){
	//Fluid f = TFTPropertyRegistry.getLiquid(this);

	//}
}
