package dinglydell.tftechness.block.component.property;

import net.minecraft.util.EnumChatFormatting;
import dinglydell.tftechness.util.StringUtil;

public class ComponentPropertyFloat extends ComponentProperty<Float> {

	private String unit;
	private boolean prefixify;

	public ComponentPropertyFloat(String name, String unit, boolean prefixify) {
		super(name);
		this.unit = unit;
		this.prefixify = prefixify;
	}

	public ComponentPropertyFloat(String name, String unit) {
		this(name, unit, true);
	}

	@Override
	protected String getDisplayValue(Float value) {
		return (prefixify ? StringUtil.prefixify(value) : value)
				+ EnumChatFormatting.RED.toString() + unit;
	}

}
