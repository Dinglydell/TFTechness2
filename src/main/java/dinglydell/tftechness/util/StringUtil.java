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
}
