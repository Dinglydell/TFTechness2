package dinglydell.tftechness.util;

import java.text.DecimalFormat;
import java.util.List;

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
			"P" };

	/**
	 * Returns a value with an SI prefix eg. 1000 -> 1k
	 * */
	public static String prefixify(double value) {
		if (value < 1000) {
			return "" + value;
		}

		for (String prefix : siPrefixes) {
			//value = Math.round(value);
			value /= 1000f;
			if (value < 1000) {

				return (new DecimalFormat("###.##").format(value)) + prefix;
			}
		}
		return value + siPrefixes[siPrefixes.length - 1];
	}
}
