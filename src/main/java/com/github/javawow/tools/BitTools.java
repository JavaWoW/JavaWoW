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
import java.util.Objects;

import io.netty.buffer.ByteBuf;

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
	 * Reads a little-endian C-string (null terminated string) from the given
	 * {@code ByteBuf}.
	 * 
	 * @param buf The {@code ByteBuf} to read from
	 * @return The little-endian C-string as a byte array
	 */
	public static final byte[] readLECString(ByteBuf buf) {
		Objects.requireNonNull(buf, "buffer cannot be null");
		buf.markReaderIndex();
		int bytesBeforeNul = buf.bytesBefore((byte) 0);
		byte[] dst = new byte[bytesBeforeNul];
		buf.readBytes(dst);
		reverseBuffer(dst, bytesBeforeNul);
		return dst;
	}

	/**
	 * Reads {@code length} bytes as characters from the given {@code ByteBuf} and
	 * returns a string with the given result.
	 * 
	 * @param buf    The {@code ByteBuf} to read from
	 * @param length The number of bytes to read (including the null terminator
	 *               byte)
	 * @return The C-string as a byte array
	 */
	public static final byte[] readLECString(ByteBuf buf, int length) {
		Objects.requireNonNull(buf, "buffer cannot be null");
		buf.markReaderIndex();
		byte[] dst = new byte[length];
		buf.readBytes(dst);
		reverseBuffer(dst, length - 1);
		if (dst[length - 1] != 0) {
			buf.resetReaderIndex();
			throw new IllegalStateException("last byte read is not null byte, it is: " + dst[length - 1]);
		}
		return dst;
	}

	/**
	 * Performs a little-endian read from the given {@code ByteBuf} into the
	 * destination byte array buffer.
	 */
	public static final void readLE(ByteBuf buf, byte[] destBuf) {
		buf.readBytes(destBuf);
		reverseBuffer(destBuf);
	}

	/**
	 * Reverses the given byte array buffer in place. Please be aware this method
	 * mutates the given byte array.
	 * 
	 * @param arr The byte array to reverse
	 */
	public static final void reverseBuffer(byte[] arr) {
		for (int i = 0, j = arr.length - 1; j > i; i++, j--) {
			byte tmp = arr[j];
			arr[j] = arr[i];
			arr[i] = tmp;
		}
	}

	/**
	 * Reverses the given byte array buffer in place using the length specifier.
	 * Please be aware this method mutates the given byte array.
	 * 
	 * @param arr The byte array to reverse
	 */
	public static final void reverseBuffer(byte[] arr, int length) {
		for (int i = 0, j = length - 1; j > i; i++, j--) {
			byte tmp = arr[j];
			arr[j] = arr[i];
			arr[i] = tmp;
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
		for (int i = (arr.length - 1), j = 0; i >= 0; i--, j++) {
			ret[j] = arr[i];
		}
		return ret;
	}

	/**
	 * Converts a {@link BigInteger} into a little-endian byte array, dropping the
	 * extra sign byte if the number is positive.
	 * 
	 * @param bi The {@link BigInteger} to convert.
	 * @return The byte array contents of the {@link BigInteger}
	 */
	public static final byte[] toLEByteArray(BigInteger bi) {
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
		byte[] ret = new byte[newLength];
		for (int i = (b.length - 1), j = 0, end = ignoreMSB ? 1 : 0; i >= end; i--, j++) {
			ret[j] = b[i];
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
		for (int i = (b.length - 1), j = 0, end = ignoreMSB ? 1 : 0; i >= end; i--, j++) {
			ret[j] = b[i];
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
		for (int i = (length - 1), j = 0; i >= 0; i--, j++) {
			arr[j] = buf[i];
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
		for (int i = (arr.length - 1), j = 0; i >= 0; i--, j++) {
			reverse[j] = arr[i];
		}
		return new BigInteger(1, reverse); // little-endian byte order
	}
}