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

import java.io.ByteArrayOutputStream;

import com.github.javawow.tools.HexTool;
import com.github.javawow.tools.packet.ByteBufWoWPacket;

import io.netty.buffer.Unpooled;

/**
 * @author Jon
 *
 */
public class LittleEndianWriterStream extends GenericLittleEndianWriter {
	private int opcode;
	private ByteArrayOutputStream baos;

	public LittleEndianWriterStream(int opcode) {
		this(opcode, 32);
	}

	public LittleEndianWriterStream(int opcode, int size) {
		this.opcode = opcode;
		this.baos = new ByteArrayOutputStream(size);
		setByteOutputStream(new ByteArrayOutputByteStream(baos));
	}

//	public final byte[] toByteArray() {
//		return baos.toByteArray();
//	}

//	public final int size() {
//		return baos.size();
//	}

	public final ByteBufWoWPacket getPacket() {
		return new ByteBufWoWPacket(opcode, Unpooled.wrappedBuffer(baos.toByteArray()));
	}

	@Override
	public final String toString() {
		return HexTool.toString(baos.toByteArray());
	}
}