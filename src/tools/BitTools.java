package tools;

import java.math.BigInteger;

public final class BitTools {
	private BitTools() {
	}

	public static final byte[] toByteArray(BigInteger bi) {
		// Remove the sign bit if it exists
		byte[] bits = bi.toByteArray();
		int length = (int) Math.ceil(bi.bitLength() / 8);
		byte[] ret = new byte[length];
		// (bits.length - length) will either return 0 or 1
		for (int i = (bits.length - length), j = 0; i < bits.length; i++) {
			ret[j++] = bits[i];
		}
		return ret;
	}

	public static final byte[] reverse(byte[] arr) {
		byte[] ret = new byte[arr.length];
		for (int i = (arr.length - 1), c = 0; i >= 0; i--) {
			ret[c++] = arr[i];
		}
		return ret;
	}

	public static final BigInteger reverse(BigInteger bi) {
		byte[] b = bi.toByteArray();
		b = reverse(b);
		return new BigInteger(1, b);
	}

	/**
	 * Converts a BigInteger into a little-endian byte array of minSize or greater,
	 * dropping the extra sign byte if the number is positive.
	 * 
	 * @param bi The BigInteger to convert.
	 * @param minSize The minimum length of the byte array to return.
	 * @return
	 */
	public static final byte[] toLEByteArray(BigInteger bi, int minSize) {
		byte[] b = bi.toByteArray();
		int newLength;
		boolean ignoreMSB = false;
		if (b[0] == 0) { // most significant byte (sign byte) is 0 (positive), we ignore the sign byte (if it exists)
			newLength = b.length - 1;
			ignoreMSB = true;
		} else {
			newLength = b.length;
		}
		newLength = Math.max(newLength, minSize); // if minSize > length, set length = minSize
		byte[] ret = new byte[newLength];
		for (int i = (b.length - 1), c = 0, end = ignoreMSB ? 1 : 0; i >= end; i--) {
			ret[c++] = b[i];
		}
		return ret;
	}

	/**
	 * Converts a BigInteger into a big-endian byte array of minSize or greater,
	 * dropping the extra sign byte if the number is positive.
	 * 
	 * @param bi The BigInteger to convert.
	 * @param minSize The minimum length of the byte array to return.
	 * @return
	 */
	public static final byte[] toByteArray(BigInteger bi, int minSize) {
		byte[] b = bi.toByteArray();
		int newLength;
		boolean ignoreMSB = false;
		if (b[0] == 0) { // most significant byte (sign byte) is 0 (positive), we ignore the sign byte (if it exists)
			newLength = b.length - 1;
			ignoreMSB = true;
		} else {
			newLength = b.length;
		}
		newLength = Math.max(newLength, minSize); // if minSize > length, set length = minSize
		byte[] ret = new byte[newLength];
		for (int i = (ignoreMSB ? 1 : 0), c = 0; i < b.length; i++) {
			ret[c++] = b[i];
		}
		return ret;
	}

	/**
	 * Converts the specified byte array into a BigInteger, the byte array can be
	 * interpreted differently based on endianness.
	 * 
	 * @param arr The byte array to convert into a BigInteger.
	 * @param littleEndian True if the byte array is little-endian, false otherwise.
	 * @return
	 */
	public static final BigInteger toBigInteger(byte[] arr, boolean littleEndian) {
		if (littleEndian) {
			byte[] reverse = new byte[arr.length];
			for (int i = (arr.length - 1), c = 0; i >= 0; i--) {
				reverse[c++] = arr[i];
			}
			return new BigInteger(1, reverse); // little-endian byte order
		}
		return new BigInteger(1, arr); // big-endian byte order
	}
}