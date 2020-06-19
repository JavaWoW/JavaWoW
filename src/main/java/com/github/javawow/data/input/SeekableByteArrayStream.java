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

import java.util.Arrays;

import com.github.javawow.tools.HexTool;

/**
 * @author Jon
 *
 */
public final class SeekableByteArrayStream implements SeekableByteInputStream {
	private byte[] bytes;
	private int pos = 0;
	private int bytesRead = 0;

	public SeekableByteArrayStream(byte[] bytes) {
		this.bytes = Arrays.copyOf(bytes, bytes.length); // defensive copy
	}

	/**
	 * @see com.github.javawow.data.input.ByteInputStream#readByte()
	 */
	@Override
	public final int readByte() {
		bytesRead++;
		return bytes[pos++] & 0xFF;
	}

	/**
	 * @see com.github.javawow.data.input.ByteInputStream#getBytesRead()
	 */
	@Override
	public final int getBytesRead() {
		return bytesRead;
	}

	/**
	 * @see com.github.javawow.data.input.ByteInputStream#available()
	 */
	@Override
	public final int available() {
		return bytes.length - pos;
	}

	/**
	 * @see com.github.javawow.data.input.SeekableByteInputStream#seek(int)
	 */
	@Override
	public final void seek(int offset) {
		pos = offset;
	}

	/**
	 * @see com.github.javawow.data.input.SeekableByteInputStream#getPosition()
	 */
	@Override
	public final int getPosition() {
		return pos;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		String nows = "";
		if (bytes.length - pos > 0) {
			byte[] now = new byte[bytes.length - pos];
			System.arraycopy(bytes, pos, now, 0, bytes.length - pos);
			nows = HexTool.toString(now);
		}
		return "All: " + HexTool.toString(bytes) + "\nNow: " + nows;
	}
}