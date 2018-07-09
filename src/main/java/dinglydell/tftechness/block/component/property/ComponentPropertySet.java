package dinglydell.tftechness.block.component.property;

import java.util.HashMap;
import java.util.Map;

/** A set of properties coming from one material */
public class ComponentPropertySet {

	private static Map<String, ComponentPropertySet> sets = new HashMap<String, ComponentPropertySet>();

	//TODO: consider max pressure change instead or in relation to gas speed rather than rotation
	public static final ComponentPropertySet TURBINE_SPEED = registerSet("wireTier",
			new ComponentProperty[] { ComponentProperty.MAX_TURBINE_SPEED });

	public static final ComponentPropertySet WIRE_TIER = registerSet("wireTier",
			new ComponentProperty[] { ComponentProperty.WIRE_TIER });

	public static final ComponentPropertySet THERMOMETER_TIER = registerSet("thermometerTier",
			new ComponentProperty[] { ComponentProperty.THERMOMETER_TIER });

	public static ComponentPropertySet registerSet(String name,
			ComponentProperty[] properties) {
		ComponentPropertySet set = new ComponentPropertySet(name, properties);
		sets.put(name, set);
		return set;
	}

	public String name;
	public ComponentProperty[] properties;

	public ComponentPropertySet(String name, ComponentProperty[] properties) {
		this.name = name;
		this.properties = properties;
	}

}
