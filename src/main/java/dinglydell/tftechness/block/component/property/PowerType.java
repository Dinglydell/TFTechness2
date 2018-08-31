package dinglydell.tftechness.block.component.property;

public class PowerType {
	public static final PowerType STEAM = new PowerType("steam", 1);
	public static final PowerType RF = new PowerType("rf", 2);
	protected int tierEffect;
	protected String name;

	public PowerType(String name, int tierEffect) {
		this.tierEffect = tierEffect;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/** This is added to the anvil tier by power assistance */
	public int getTierEffect() {
		return tierEffect;
	}
}