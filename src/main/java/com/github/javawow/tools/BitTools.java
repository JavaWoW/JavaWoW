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

public final class BitTools {
	private BitTools() {
	}

	public static final byte[] reverse(byte[] arr) {
		byte[] ret = new byte[arr.length];
		for (int i = (arr.length - 1), c = 0; i >= 0; i--) {
			ret[c++] = arr[i];
		}
		return ret;
	}

	/**
	 * Converts a BigInteger into a little-endian byte array of minSize or greater,
	 * dropping the extra sign byte if the number is positive.
	 * 
	 * @param bi      The BigInteger to convert.
	 * @param minSize The minimum length of the byte array to return.
	 * @return
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
	 * Converts a BigInteger into a big-endian byte array of minSize or greater,
	 * dropping the extra sign byte if the number is positive.
	 * 
	 * @param bi      The BigInteger to convert.
	 * @param minSize The minimum length of the byte array to return.
	 * @return
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
		for (int i = (ignoreMSB ? 1 : 0), c = 0; i < b.length; i++) {
			ret[c++] = b[i];
		}
		return ret;
	}

	/**
	 * Converts the specified byte array into a BigInteger, the byte array can be
	 * interpreted differently based on endianness.
	 * 
	 * @param arr          The byte array to convert into a BigInteger.
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