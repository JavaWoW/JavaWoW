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

package com.github.javawow.data.output;

import java.nio.charset.Charset;

/**
 * @author Jon
 *
 */
public class GenericLittleEndianWriter implements LittleEndianWriter {
	private static final Charset ASCII = Charset.forName("UTF-8");
	private ByteOutputStream bos;

	protected GenericLittleEndianWriter() {
	}

	public GenericLittleEndianWriter(ByteOutputStream bos) {
		this.bos = bos;
	}

	protected void setByteOutputStream(ByteOutputStream bos) {
		this.bos = bos;
	}

	@Override
	public final void writeZeroBytes(int i) {
		for (int x = 0; x < i; x++) {
			bos.writeByte((byte) 0);
		}
	}

	@Override
	public final void write(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			bos.writeByte(b[i]);
		}
	}

	@Override
	public final void write(byte b) {
		bos.writeByte(b);
	}

	@Override
	public final void write(int b) {
		bos.writeByte((byte) b);
	}

	@Override
	public final void writeShort(int i) {
		bos.writeByte((byte) (i & 0xFF));
		bos.writeByte((byte) ((i >>> 8) & 0xFF));
	}

	@Override
	public final void writeInt(int i) {
		bos.writeByte((byte) (i & 0xFF));
		bos.writeByte((byte) ((i >>> 8) & 0xFF));
		bos.writeByte((byte) ((i >>> 16) & 0xFF));
		bos.writeByte((byte) ((i >>> 24) & 0xFF));
	}

	@Override
	public final void writeInt(long i) {
		bos.writeByte((byte) (i & 0xFF));
		bos.writeByte((byte) ((i >>> 8) & 0xFF));
		bos.writeByte((byte) ((i >>> 16) & 0xFF));
		bos.writeByte((byte) ((i >>> 24) & 0xFF));
	}

	@Override
	public final void writeAsciiString(String s) {
		this.write(s.getBytes(ASCII));
	}

	@Override
	public final void writeAsciiString(String s, int max) {
		this.write(s.getBytes(ASCII));
		for (int i = s.length(); i < max; i++) {
			this.write(0);
		}
	}

	@Override
	public final void writeNullTerminatedAsciiString(String s) {
		this.writeAsciiString(s);
		this.write(0);
	}

	@Override
	public final void writeLong(long l) {
		bos.writeByte((byte) (l & 0xFF));
		bos.writeByte((byte) ((l >>> 8) & 0xFF));
		bos.writeByte((byte) ((l >>> 16) & 0xFF));
		bos.writeByte((byte) ((l >>> 24) & 0xFF));
		bos.writeByte((byte) ((l >>> 32) & 0xFF));
		bos.writeByte((byte) ((l >>> 40) & 0xFF));
		bos.writeByte((byte) ((l >>> 48) & 0xFF));
		bos.writeByte((byte) ((l >>> 56) & 0xFF));
	}

	@Override
	public final void writeFloat(float f) {
		int i = Float.floatToIntBits(f);
		this.writeInt(i);
	}

	@Override
	public final void writeBEFloat(float f) {
		int i = Float.floatToIntBits(f);
		bos.writeByte((byte) ((i >>> 24) & 0xFF));
		bos.writeByte((byte) ((i >>> 16) & 0xFF));
		bos.writeByte((byte) ((i >>> 8) & 0xFF));
		bos.writeByte((byte) (i & 0xFF));
	}

	@Override
	public final void writeDouble(double d) {
		long l = Double.doubleToLongBits(d);
		this.writeLong(l);
	}

	@Override
	public final void writeBEDouble(double d) {
		long l = Double.doubleToLongBits(d);
		bos.writeByte((byte) ((l >>> 56) & 0xFF));
		bos.writeByte((byte) ((l >>> 48) & 0xFF));
		bos.writeByte((byte) ((l >>> 40) & 0xFF));
		bos.writeByte((byte) ((l >>> 32) & 0xFF));
		bos.writeByte((byte) ((l >>> 24) & 0xFF));
		bos.writeByte((byte) ((l >>> 16) & 0xFF));
		bos.writeByte((byte) ((l >>> 8) & 0xFF));
		bos.writeByte((byte) (l & 0xFF));
	}
}