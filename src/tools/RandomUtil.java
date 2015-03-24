package tools;

import java.security.SecureRandom;

public final class RandomUtil {
	private static final SecureRandom sr = new SecureRandom();

	private RandomUtil() {
	}

	public static final SecureRandom getSecureRandom() {
		return sr;
	}
}