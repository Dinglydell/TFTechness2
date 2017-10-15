package dinglydell.tftechness.util;

import java.util.List;

public class StringUtil {
	public static String[] toStringArray(List<String> arr) {
		return arr.toArray(new String[arr.size()]);
	}
}
