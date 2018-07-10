package dinglydell.tftechness.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.bioxx.tfc.api.TFC_ItemHeat;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.component.property.ComponentPropertyThermometerTier.ThermometerTier;

public class StringUtil {
	public static String[] toStringArray(List<String> arr) {
		return arr.toArray(new String[arr.size()]);
	}

	/** Capitalises the first letter of the string. */
	public static String capitaliseFirst(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * Converts the first letter of the string to lower case, leaves the rest
	 * unchanged.
	 */
	public static String lowerCaseFirst(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	private static final String[] siPrefixes = new String[] { "k",
			"M",
			"G",
			"T",
			"P",
			"E" };
	private static final String[] smallSiPrefixes = new String[] { "m",
			"\u03bc",
			"n",
			"p",
			"f",
			"a" };

	/**
	 * Returns a value with an SI prefix eg. 1000 -> 1k
	 * */
	public static String prefixify(double value) {

		if (value < 1000) {
			if (value >= 1) {
				return (new DecimalFormat("###.##").format(value));
			}
			for (String prefix : smallSiPrefixes) {
				//value = Math.round(value);
				value *= 1000f;
				if (value >= 1) {
					return (new DecimalFormat("###.##").format(value))
							+ EnumChatFormatting.BOLD.toString() + prefix
							+ EnumChatFormatting.RESET.toString();
				}
			}
			return "0";//value + smallSiPrefixes[smallSiPrefixes.length - 1];

		}

		for (String prefix : siPrefixes) {
			//value = Math.round(value);
			value /= 1000f;
			if (value < 1000) {

				return (new DecimalFormat("###.##").format(value))
						+ EnumChatFormatting.BOLD.toString() + prefix
						+ EnumChatFormatting.RESET.toString();
			}
		}
		return value + siPrefixes[siPrefixes.length - 1];
	}

	public static void addTemperatureTooltip(List<String> tooltip, float temp,
			float max, ThermometerTier tier) {
		String roundedTemp = null;
		if (tier == ThermometerTier.rounded) {
			roundedTemp = "" + Math.round(temp / 10) * 10 + "±5";
		} else if (temp >= 100 || temp <= -100) {
			roundedTemp = "" + Math.round(temp);
		} else {

			roundedTemp = (new BigDecimal(temp)).round(new MathContext(3))
					.toString();
		}
		String colour;
		boolean danger = false;
		if (temp > 0.95 * max) {
			colour = EnumChatFormatting.RED.toString();
			danger = true;
		} else if (temp > 0.9 * max) {
			colour = EnumChatFormatting.GOLD.toString();
		} else if (temp > 0.8 * max) {
			colour = EnumChatFormatting.YELLOW.toString();
		} else {
			colour = EnumChatFormatting.WHITE.toString();
		}
		if (tier != ThermometerTier.fuzzy) {
			tooltip.add(colour + (roundedTemp) + TFTechness2.degrees + "C");
		}
		tooltip.add(TFC_ItemHeat.getHeatColor(temp, max));
		if (danger) {
			if (temp > max) {
				tooltip.add(colour
						+ StatCollector.translateToLocal("gui.tooltip.failure"));
			} else {
				tooltip.add(colour
						+ StatCollector.translateToLocal("gui.tooltip.danger"));
			}
		}

	}

	public static void addPressureTooltip(List<String> tooltip, double p,
			double max, double atmosphericPressure) {
		String chatColour;
		boolean danger = false;
		double diff = Math.abs(p - atmosphericPressure);
		if (max * 0.8 < diff) {
			chatColour = EnumChatFormatting.RED.toString();
			danger = true;
		} else if (max * 0.7 < diff) {
			chatColour = EnumChatFormatting.GOLD.toString();
		} else if (max * 0.6 < diff) {
			chatColour = EnumChatFormatting.YELLOW.toString();
		} else {
			chatColour = EnumChatFormatting.RESET.toString();
		}
		tooltip.add("Gases (" + chatColour + StringUtil.prefixify(p) + "Pa"
				+ EnumChatFormatting.RESET.toString() + ")");
		if (danger) {
			tooltip.add(chatColour
					+ StatCollector.translateToLocal("gui.tooltip.danger"));
		}

	}
}
