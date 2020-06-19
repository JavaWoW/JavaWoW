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

import java.math.BigInteger;

/**
 * Provides operations for manipulating bits, byte arrays, and
 * {@link BigInteger}s.
 * 
 * @author Jon Huang
 *
 */
public final class BitTools {
	private BitTools() {
		// static utility class
	}

	/**
	 * Reverses the given byte array buffer in place. Please be aware this method
	 * mutates the given byte array.
	 * 
	 * @param arr The byte array to reverse
	 */
	public static final void reverseBuffer(byte[] arr) {
		for (int i = 0, n = arr.length / 2, lastIndex = arr.length - 1; i < n; i++) {
			int otherIdx = lastIndex - i;
			byte temp = arr[i];
			arr[i] = arr[otherIdx];
			arr[otherIdx] = temp;
		}
	}

	/**
	 * Reverses the given byte array.
	 * 
	 * @param arr The byte array to reverse
	 * @return A newly allocated byte array in reverse order.
	 */
	public static final byte[] reverse(byte[] arr) {
		byte[] ret = new byte[arr.length];
		for (int i = (arr.length - 1), c = 0; i >= 0; i--) {
			ret[c++] = arr[i];
		}
		return ret;
	}

	/**
	 * Converts a {@link BigInteger} into a little-endian byte array of
	 * {@code minSize} or greater, dropping the extra sign byte if the number is
	 * positive.
	 * 
	 * @param bi      The {@link BigInteger} to convert.
	 * @param minSize The minimum length of the byte array to return.
	 * @return The byte array contents of the {@link BigInteger}
	 */
	public static final byte[] toLEByteArray(BigInteger bi, int minSize) {
		byte[] b = bi.toByteArray();
		int newLength;
		boolean ignoreMSB = false;
		if (b[0] == 0) { // most significant byte (sign byte) is 0 (positive), we ignore the sign byte
							// (if it exists)
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
	 * Converts a {@link BigInteger} into a big-endian byte array of {@code minSize}
	 * or greater, dropping the extra sign byte if the number is positive.
	 * 
	 * @param bi      The {@link BigInteger} to convert.
	 * @param minSize The minimum length of the byte array to return.
	 * @return The byte array contents of the {@link BigInteger}
	 */
	public static final byte[] toByteArray(BigInteger bi, int minSize) {
		byte[] b = bi.toByteArray();
		int newLength;
		boolean ignoreMSB = false;
		if (b[0] == 0) { // most significant byte (sign byte) is 0 (positive), we ignore the sign byte
							// (if it exists)
			newLength = b.length - 1;
			ignoreMSB = true;
		} else {
			newLength = b.length;
		}
		newLength = Math.max(newLength, minSize); // if minSize > length, set length = minSize
		byte[] ret = new byte[newLength];
		System.arraycopy(b, (ignoreMSB ? 1 : 0), ret, 0, newLength);
		return ret;
	}

	/**
	 * Reads {@code length} bytes from the specified byte buffer into a
	 * {@link BigInteger}, the byte array can be interpreted differently based on
	 * endianness.
	 * 
	 * @param buf          The byte array to read from to convert into a
	 *                     {@link BigInteger}.
	 * @param length       The number of bytes to read from the byte array
	 * @param littleEndian {@code true} if the byte array is little-endian,
	 *                     {@code false} otherwise.
	 * @return The {@link BigInteger} created from the byte array
	 */
	public static final BigInteger toBigInteger(byte[] buf, int length, boolean littleEndian) {
		byte[] arr = new byte[length];
		if (!littleEndian) {
			System.arraycopy(buf, 0, arr, 0, length);
			return new BigInteger(1, arr); // big-endian byte order
		}
		for (int i = (length - 1), c = 0; i >= 0; i--) {
			arr[c++] = buf[i];
		}
		return new BigInteger(1, arr); // little-endian byte order
	}

	/**
	 * Converts the specified byte array into a {@link BigInteger}, the byte array
	 * can be interpreted differently based on endianness.
	 * 
	 * @param arr          The byte array to convert into a {@link BigInteger}.
	 * @param littleEndian {@code true} if the byte array is little-endian,
	 *                     {@code false} otherwise.
	 * @return The {@link BigInteger} created from the byte array
	 */
	public static final BigInteger toBigInteger(byte[] arr, boolean littleEndian) {
		if (!littleEndian) {
			return new BigInteger(1, arr); // big-endian byte order
		}
		byte[] reverse = new byte[arr.length];
		for (int i = (arr.length - 1), c = 0; i >= 0; i--) {
			reverse[c++] = arr[i];
		}
		return new BigInteger(1, reverse); // little-endian byte order
	}
}