package dinglydell.tftechness.metal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

import com.bioxx.tfc.Core.Metal.MetalRegistry;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.Metal;

/** Nasty stuff happens here */
public class MetalSnatcher {
	
	// If your metals are stored in a map with a string key, why would you use an integer ID?
	// Why isn't metalID in BlockMetalSheet a string? Why is icons an array and not a Map?
	public static Metal getMetalByID(int metalID) {
		return getMetalsAsArray()[metalID];
	}
	
	public static int getIdFromMetal(Metal m) {
		ArrayList<Metal> metals = new ArrayList<Metal>(Arrays.asList(getMetalsAsArray()));
		return metals.indexOf(m);
	}
	
	public static HeatIndex getHeatIndexFromMetal(Metal m) {
		return HeatRegistry.getInstance().findMatchingIndex(new ItemStack(m.ingot));
	}
	
	public static Map<String, Metal> getMetals() {
		try {
			Field f = MetalRegistry.class.getDeclaredField("hash");
			f.setAccessible(true);
			return (Map<String, Metal>) f.get(MetalRegistry.instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Metal[] getMetalsAsArray() {
		Map<String, Metal> metalMap = getMetals();
		Metal[] metals = new Metal[metalMap.size()];
		int i = 0;
		for (Entry<String, Metal> metal : metalMap.entrySet()) {
			metals[i] = metal.getValue();
			i++;
		}
		return metals;
	}
	
}
