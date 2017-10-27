package dinglydell.tftechness.util;

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
}
