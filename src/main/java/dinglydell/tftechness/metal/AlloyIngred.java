package dinglydell.tftechness.metal;

import com.bioxx.tfc.Core.Metal.MetalRegistry;
import com.bioxx.tfc.api.Metal;

public class AlloyIngred {
	public Metal metal;
	public float min;
	public float max;
	
	public AlloyIngred(Metal metal, float min, float max) {
		this.metal = metal;
		this.min = min;
		this.max = max;
	}
	public AlloyIngred(String name, float min, float max){
		this(MetalRegistry.instance
		.getMetalFromString(name), min, max);
	}
}
