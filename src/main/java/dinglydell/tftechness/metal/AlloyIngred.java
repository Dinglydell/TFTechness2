package dinglydell.tftechness.metal;

import com.bioxx.tfc.api.Metal;

public class AlloyIngred {
	public String metal;
	public float min;
	public float max;

	public AlloyIngred(Metal metal, float min, float max) {
		this(metal.name, min, max);

	}

	public AlloyIngred(String name, float min, float max) {
		this.metal = name;
		this.min = min;
		this.max = max;
	}
}
