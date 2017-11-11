package dinglydell.tftechness.util;

public class MathsUtils {
	public static int gcd(int a, int b) {
		if (b == 0) {
			return a;
		}

		int c = a % b;

		return gcd(b, c);
	}
}
