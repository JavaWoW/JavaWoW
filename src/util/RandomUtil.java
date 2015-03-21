package util;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class RandomUtil {
	private static final SecureRandom sr = new SecureRandom();

	private RandomUtil() {
	}

	public static final SecureRandom getSecureRandom() {
		return sr;
	}

	public static final BigInteger getRandomS() {
		return new BigInteger(1, sr.generateSeed(32));
	}

	public static final void main(String[] args) {
		BigInteger b = getRandomS();
		System.out.println(b);
	}
}