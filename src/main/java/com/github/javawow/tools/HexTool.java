/*
 * Java World of Warcraft Emulation Project
 * Copyright (C) 2015-2020 JavaWoW
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.javawow.tools;

import io.netty.buffer.ByteBuf;

/**
 * Provides functions for manipulating hexadecimal values (converting from hex
 * to strings and back)
 * 
 * @author Jon Huang
 *
 */
public final class HexTool {
	private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

	private HexTool() {
		// static utility class
	}

	/**
	 * Converts an array of bytes into a hexadecimal string starting from index 0.
	 * 
	 * @param arr The array of bytes to convert.
	 * @return The converted hexadecimal string.
	 */
	public static final String toString(byte[] arr) {
		return toString(arr, 0);
	}

	/**
	 * Converts an array of bytes into a hexadecimal string.
	 * 
	 * @param arr    The array of bytes to convert.
	 * @param offset The index offset to start from.
	 * @return The converted hexadecimal string.
	 */
	public static final String toString(byte[] arr, int offset) {
		return toString(arr, offset, ' ');
	}

	/**
	 * Converts an array of bytes into a hexadecimal string.
	 * 
	 * @param arr       The array of bytes to convert.
	 * @param offset    The index offset to start from.
	 * @param separator The separator character for each hexadecimal value.
	 * @return The converted hexadecimal string.
	 */
	public static final String toString(byte[] arr, int offset, char separator) {
		if (arr.length == 0) {
			return "";
		}
		char[] cbuf = new char[(arr.length - offset) * 3]; // 2 chars for each byte, 1 for the space
		for (int i = offset, cbufIdx = 0; i < arr.length; i++, cbufIdx += 3) {
			int ub = arr[i] & 0xFF;
			cbuf[cbufIdx] = HEX_CHARS[ub >>> 4];
			cbuf[cbufIdx + 1] = HEX_CHARS[ub & 0xF];
			cbuf[cbufIdx + 2] = separator;
		}
		return String.valueOf(cbuf, 0, cbuf.length - 1);
	}

	/**
	 * Converts an array of bytes into a hexadecimal string without separator
	 * characters.
	 * 
	 * @param arr The array of bytes to convert.
	 * @return The converted hexadecimal string.
	 */
	public static final String toUnspacedString(byte[] arr) {
		if (arr.length == 0) {
			return "";
		}
		char[] cbuf = new char[arr.length * 2]; // 2 chars for each byte
		for (int i = 0, cbufIdx = 0; i < arr.length; i++, cbufIdx += 2) {
			int ub = arr[i] & 0xFF;
			cbuf[cbufIdx] = HEX_CHARS[ub >>> 4];
			cbuf[cbufIdx + 1] = HEX_CHARS[ub & 0xF];
		}
		return String.valueOf(cbuf);
	}

	/**
	 * Converts an array of bytes into an ASCII string. Any non-printable characters
	 * are replaced by a period ({@code .})
	 * 
	 * @param arr The array of bytes to convert.
	 * @return The ASCII representation of {@code bytes}
	 */
	public static final String toStringFromAscii(byte[] arr) {
		if (arr.length == 0) {
			return "";
		}
		char[] cbuf = new char[arr.length];
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < 32 && arr[i] >= 0) {
				cbuf[i] = '.';
			} else {
				cbuf[i] = (char) (arr[i] & 0xFF);
			}
		}
		return String.valueOf(cbuf);
	}

	/**
	 * Converts an array of bytes into a padded ASCII string. The string is padded
	 * with space characters. Any non-printable characters are replaced by a period
	 * ({@code .})
	 * 
	 * @param arr The array of bytes to convert.
	 * @return The padded ASCII representation of {@code bytes}
	 */
	public static final String toPaddedStringFromAscii(byte[] arr) {
		if (arr.length == 0) {
			return "";
		}
		char[] cbuf = new char[arr.length * 2];
		for (int i = 0, cbufIdx = 0; i < arr.length; i++, cbufIdx += 2) {
			if (arr[i] >= 0 && arr[i] < 32) { // non-printable characters
				cbuf[cbufIdx] = '.';
			} else {
				cbuf[cbufIdx] = (char) (arr[i] & 0xFF);
			}
			cbuf[cbufIdx + 1] = ' ';
		}
		return String.valueOf(cbuf, 0, cbuf.length - 1);
	}

	/**
	 * Converts the character to a numerical value (base 16).
	 * 
	 * @param ch The character to convert.
	 * @return The numerical value (base 16) of the character.
	 */
	private static final int hexToBin(char ch) {
		if ('0' <= ch && ch <= '9') {
			return ch - '0';
		}
		if ('A' <= ch && ch <= 'F') {
			return ch - 'A' + 10;
		}
		if ('a' <= ch && ch <= 'f') {
			return ch - 'a' + 10;
		}
		return -1;
	}

	/**
	 * Parses a hexadecimal string into an array of bytes. Legacy method.
	 * 
	 * @param str The hexadecimal string to parse.
	 * @return The array of bytes parsed from {@code hex}
	 */
	public static final byte[] getByteArrayFromHexString(String str) {
		String hexStr = str.replace(" ", ""); // remove all space characters
		return parseHexString(hexStr);
	}

	/**
	 * Parses a hexadecimal string into an array of bytes.
	 * 
	 * @param str The hexadecimal string to parse.
	 * @return The array of bytes parsed from {@code hex}
	 */
	public static final byte[] parseHexString(String str) {
		int strLen = str.length();
		// if the string length is not even, not parsable
		if (strLen % 2 != 0) {
			throw new IllegalArgumentException("hex string needs to be even length: " + str);
		}
		byte[] buf = new byte[strLen / 2];
		for (int i = 0; i < strLen; i += 2) {
			int h = hexToBin(str.charAt(i));
			int l = hexToBin(str.charAt(i + 1));
			if (h == -1 || l == -1) {
				throw new IllegalArgumentException("hex string contains illegal character: " + str);
			}
			buf[i / 2] = (byte) (h * 16 + l);
		}
		return buf;
	}

	/**
	 * Converts the given {@code ByteBuf} into a hexadecimal string.
	 * 
	 * @param buf The {@code ByteBuf} to convert.
	 * @return The converted hexadecimal string.
	 */
	public static final String toString(ByteBuf buf) {
		char[] cbuf = new char[buf.readableBytes() * 3]; // 2 chars for each byte, 1 for the space
		for (int i = buf.readerIndex(), n = buf.writerIndex(), cbufIdx = 0; i < n; i++, cbufIdx += 3) {
			short ub = buf.getUnsignedByte(i);
			cbuf[cbufIdx] = HEX_CHARS[ub >>> 4];
			cbuf[cbufIdx + 1] = HEX_CHARS[ub & 0xF];
			cbuf[cbufIdx + 2] = ' ';
		}
		return String.valueOf(cbuf, 0, cbuf.length - 1);
	}

	/**
	 * Converts the given {@code ByteBuf} into a hexadecimal string.
	 * 
	 * @param buf         The {@code ByteBuf} to convert.
	 * @param writerIndex The writer index of the {@code ByteBuf}
	 * @param readerIndex The reader index of the {@code ByteBuf}
	 * @return The converted hexadecimal string.
	 */
	public static final String toString(ByteBuf buf, int writerIndex, int readerIndex) {
		int readableBytes = writerIndex - readerIndex;
		char[] cbuf = new char[readableBytes * 3]; // 2 chars for each byte, 1 for the space
		for (int i = readerIndex, n = writerIndex, cbufIdx = 0; i < n; i++, cbufIdx += 3) {
			short ub = buf.getUnsignedByte(i);
			cbuf[cbufIdx] = HEX_CHARS[ub >>> 4];
			cbuf[cbufIdx + 1] = HEX_CHARS[ub & 0xF];
			cbuf[cbufIdx + 2] = ' ';
		}
		return String.valueOf(cbuf, 0, cbuf.length - 1);
	}
}