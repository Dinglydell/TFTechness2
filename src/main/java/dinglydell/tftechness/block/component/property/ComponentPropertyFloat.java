package dinglydell.tftechness.block.component.property;

import dinglydell.tftechness.util.StringUtil;

public class ComponentPropertyFloat extends ComponentProperty<Float> {

	private String unit;

	public ComponentPropertyFloat(String name, String unit) {
		super(name);
		this.unit = unit;
	}

	@Override
	protected String getDisplayValue(Float value) {
		return StringUtil.prefixify(value) + unit;
	}

}
