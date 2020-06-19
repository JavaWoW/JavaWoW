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

package com.github.javawow.data.input;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

/**
 * @author Jon
 *
 */
public class GenericLittleEndianAccessor implements LittleEndianAccessor {
	private final ByteInputStream bis;

	public GenericLittleEndianAccessor(ByteInputStream bis) {
		this.bis = bis;
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readByte()
	 */
	@Override
	public final byte readByte() {
		return (byte) bis.readByte();
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readUnsignedByte()
	 */
	@Override
	public short readUnsignedByte() {
		return (short) (readByte() & 0xFF);
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readChar()
	 */
	@Override
	public final char readChar() {
		return (char) readShort();
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readShort()
	 */
	@Override
	public final short readShort() {
		int byte1 = bis.readByte();
		int byte2 = bis.readByte();
		return (short) ((byte2 << 8) + byte1);
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readUnsignedShort()
	 */
	@Override
	public int readUnsignedShort() {
		return readShort() & 0xFFFF;
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readInt()
	 */
	@Override
	public final int readInt() {
		int byte1 = bis.readByte();
		int byte2 = bis.readByte();
		int byte3 = bis.readByte();
		int byte4 = bis.readByte();
		return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readUnsignedInt()
	 */
	@Override
	public final long readUnsignedInt() {
		return readInt() & 0xFFFFFFFFL;
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readLong()
	 */
	@Override
	public final long readLong() {
		long byte1 = bis.readByte();
		long byte2 = bis.readByte();
		long byte3 = bis.readByte();
		long byte4 = bis.readByte();
		long byte5 = bis.readByte();
		long byte6 = bis.readByte();
		long byte7 = bis.readByte();
		long byte8 = bis.readByte();
		return (byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readUnsignedLong()
	 */
	@Override
	public BigInteger readUnsignedLong() {
		BigInteger mask = new BigInteger("18446744073709551615"); // 0xFFFFFFFFFFFFFFFFL
		return mask.and(BigInteger.valueOf(readLong()));
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#skip(int)
	 */
	@Override
	public void skip(int num) {
		for (int x = 0; x < num; x++) {
			readByte();
		}
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#read(int)
	 */
	@Override
	public final byte[] read(int num) {
		byte[] ret = new byte[num];
		for (int x = 0; x < num; x++) {
			ret[x] = readByte();
		}
		return ret;
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readFloat()
	 */
	@Override
	public final float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readDouble()
	 */
	@Override
	public final double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readAsciiString(int)
	 */
	@Override
	public final String readAsciiString(int n) {
		char ret[] = new char[n];
		for (int x = 0; x < n; x++) {
			ret[x] = (char) readByte();
		}
		return new String(ret);
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readAsciiString(int)
	 */
	@Override
	public final String readLEAsciiString(int length) {
		char ret[] = new char[length];
		for (int x = 0, n = length - 1; x < length; x++) {
			ret[n - x] = (char) readByte();
		}
		return new String(ret);
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readNullTerminatedAsciiString()
	 */
	@Override
	public final String readNullTerminatedAsciiString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte b;
		while ((b = readByte()) != 0) {
			baos.write(b);
		}
		byte[] buf = baos.toByteArray();
		char[] chrBuf = new char[buf.length];
		for (int x = 0; x < buf.length; x++) {
			chrBuf[x] = (char) buf[x];
		}
		return new String(chrBuf);
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#readNullTerminatedAsciiString()
	 */
	@Override
	public final String readLENullTerminatedAsciiString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte b;
		while ((b = readByte()) != 0) {
			baos.write(b);
		}
		byte[] buf = baos.toByteArray();
		char[] chrBuf = new char[buf.length];
		for (int x = 0, n = buf.length - 1; x < buf.length; x++) {
			chrBuf[n - x] = (char) buf[x];
		}
		return new String(chrBuf);
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#getBytesRead()
	 */
	@Override
	public final int getBytesRead() {
		return bis.getBytesRead();
	}

	/**
	 * @see com.github.javawow.data.input.LittleEndianAccessor#available()
	 */
	@Override
	public final int available() {
		return bis.available();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return bis.toString();
	}
}