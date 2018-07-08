package dinglydell.tftechness.block.component.property;

import dinglydell.tftechness.block.component.property.ComponentPropertyThermometerTier.ThermometerTier;
import dinglydell.tftechness.util.StringUtil;

public class ComponentPropertyThermometerTier extends
		ComponentProperty<ThermometerTier> {
	/** how much each level of TFC heat increments to add a new star */
	public static final int[] temperatureIncrements = new int[] { 16,
			26,
			54,
			20,
			30,
			40,
			34,
			40,
			20,
			20 };

	public static final int[] temperatureIntervals;
	static {
		temperatureIntervals = new int[temperatureIncrements.length * 5];
		int temp = 0;
		for (int i = 0; i < temperatureIncrements.length; i++) {
			for (int j = 0; j < 5; j++) {
				temp += temperatureIncrements[i];
				temperatureIntervals[i * 5 + j] = temp;
			}
		}
	}

	public enum ThermometerTier {
		fuzzy(20), rounded(10), precise(1);

		private int increment;

		private ThermometerTier(int increment) {
			this.increment = increment;
		}

		public int increment(float targetTemperature, int wheelMovement) {
			if (wheelMovement == 0) {
				return (int) targetTemperature;
			}
			if (this == fuzzy && targetTemperature > 0) { // special case
				int i;

				for (i = 0; i < temperatureIntervals.length
						&& temperatureIntervals[i] < targetTemperature; i++)
					;

				int newIndex = i + wheelMovement;
				if (i >= temperatureIntervals.length) {
					newIndex += (targetTemperature - temperatureIntervals[temperatureIntervals.length - 1])
							/ increment;
				} else if (i == 0) {
					newIndex -= (temperatureIntervals[0] - targetTemperature)
							/ increment;
				}
				if (newIndex >= temperatureIntervals.length
						|| i >= temperatureIntervals.length) {
					if (newIndex == temperatureIntervals.length) {
						if (wheelMovement > 0) {
							newIndex++;
						} else {
							newIndex--;
						}
					}
					return temperatureIntervals[temperatureIntervals.length - 1]
							+ (increment * (newIndex - temperatureIntervals.length));
				}
				if (newIndex <= 0) {
					return temperatureIntervals[0] + increment * newIndex;
				}
				return temperatureIntervals[newIndex];
			}
			int target = Math.round(targetTemperature * increment) / increment;
			return target + wheelMovement * increment;
		}

		public int round(float targetTemperature) {
			if (this == ThermometerTier.fuzzy && targetTemperature > 0) {// special case
				int i;
				for (i = 0; i < temperatureIntervals.length
						&& temperatureIntervals[i] < targetTemperature; i++)
					;
				if (i < temperatureIntervals.length) {
					return temperatureIntervals[i];
				}
			}
			return Math.round(targetTemperature / increment) * increment;
		}
	}

	public ComponentPropertyThermometerTier(String name) {
		super(name);
	}

	@Override
	protected String getDisplayValue(ThermometerTier tier) {
		return StringUtil.capitaliseFirst(tier.name());
	}

}
