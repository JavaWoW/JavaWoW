package com.github.javawow.tools;

import java.io.ByteArrayOutputStream;

import io.netty.buffer.ByteBuf;

/**
 * 
 * @author Jon Huang
 *
 */
public final class HexTool {
	private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

	private HexTool() {
		throw new AssertionError();
	}

	public static final String toString(byte[] val) {
		return toString(val, 0);
	}

	/**
	 * Turns an array of bytes into a hexadecimal string.
	 * 
	 * @param val The bytes to convert.
	 * @return The hexadecimal representation of the array of bytes.
	 */
	public static final String toString(byte[] val, int offset) {
		return toString(val, offset, ' ');
	}

	public static final String toString(byte[] val, int offset, char spacer) {
		if (val.length == 0) {
			return "";
		}
		StringBuilder hexStr = new StringBuilder(val.length * 3); // 2 chars for each byte, 1 for the space
		for (int i = offset; i < val.length; i++) {
			int v = val[i] & 0xFF;
			hexStr.append(spacer);
			hexStr.append(HEX_CHARS[v >>> 4]).append(HEX_CHARS[v & 0xF]);
		}
		return hexStr.substring(1);
	}

	/**
	 * Turns an array of bytes into a hexadecimal string without spaces.
	 * 
	 * @param val The bytes to convert.
	 * @return The hexadecimal representation of the array of bytes.
	 */
	public static String toUnspacedString(byte[] val) {
		StringBuilder hexStr = new StringBuilder(val.length * 2); // 2 chars for each byte
		for (int i = 0; i < val.length; i++) {
			int v = val[i] & 0xFF;
			hexStr.append(HEX_CHARS[v >>> 4]).append(HEX_CHARS[v & 0xF]);
		}
		return hexStr.toString();
	}

	/**
	 * Turns an array of bytes into a ASCII string. Any non-printable characters
	 * are replaced by a period (<code>.</code>)
	 * 
	 * @param bytes The bytes to convert.
	 * @return The ASCII hexadecimal representation of <code>bytes</code>
	 */
	public static String toStringFromAscii(byte[] bytes) {
		char[] ret = new char[bytes.length];
		for (int x = 0; x < bytes.length; x++) {
			if (bytes[x] < 32 && bytes[x] >= 0) {
				ret[x] = '.';
			} else {
				int chr = bytes[x] & 0xFF;
				ret[x] = (char) chr;
			}
		}
		return String.valueOf(ret);
	}
	
	public static String toPaddedStringFromAscii(byte[] bytes) {
		String str = toStringFromAscii(bytes);
		StringBuilder ret = new StringBuilder(str.length() * 3);
		for (int i = 0; i < str.length(); i++) {
			ret.append(str.charAt(i));
			ret.append("  ");
		}
		return ret.toString();
	}

	/**
	 * Turns an hexadecimal string into a byte array.
	 * 
	 * @param hex The string to convert.
	 * @return The byte array representation of <code>hex</code>
	 */
	public static byte[] getByteArrayFromHexString(String hex) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int nexti = 0;
		int nextb = 0;
		boolean highoc = true;
		outer: for (;;) {
			int number = -1;
			while (number == -1) {
				if (nexti == hex.length()) {
					break outer;
				}
				char chr = hex.charAt(nexti);
				if (chr >= '0' && chr <= '9') {
					number = chr - '0';
				} else if (chr >= 'a' && chr <= 'f') {
					number = chr - 'a' + 10;
				} else if (chr >= 'A' && chr <= 'F') {
					number = chr - 'A' + 10;
				} else {
					number = -1;
				}
				nexti++;
			}
			if (highoc) {
				nextb = number << 4;
				highoc = false;
			} else {
				nextb |= number;
				highoc = true;
				baos.write(nextb);
			}
		}
		return baos.toByteArray();
	}

	public static final String toString(ByteBuf buf) {
		StringBuilder hexStr = new StringBuilder(buf.readableBytes() * 3); // 2 chars for each byte, 1 for the space
		buf.markReaderIndex();
		for (int i = buf.readerIndex(), n = buf.writerIndex(); i < n; i++) {
			int v = buf.readByte() & 0xFF;
			hexStr.append(' ' );
			hexStr.append(HEX_CHARS[v >>> 4]).append(HEX_CHARS[v & 0xF]);
		}
		buf.resetReaderIndex();
		return hexStr.substring(1);
	}

	public static final String toString(ByteBuf buf, int writerIndex, int readerIndex) {
		int readableBytes = writerIndex - readerIndex;
		StringBuilder hexStr = new StringBuilder(readableBytes * 3); // 2 chars for each byte, 1 for the space
		for (int i = readerIndex, n = writerIndex; i < n; i++) {
			int v = buf.getByte(i) & 0xFF;
			hexStr.append(' ' );
			hexStr.append(HEX_CHARS[v >>> 4]).append(HEX_CHARS[v & 0xF]);
		}
		return hexStr.substring(1);
	}
}