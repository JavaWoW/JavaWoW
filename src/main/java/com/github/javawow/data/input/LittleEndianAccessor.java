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

import java.math.BigInteger;

/**
 * @author Jon
 *
 */
public interface LittleEndianAccessor {
	byte readByte();
	short readUnsignedByte();
	char readChar();
	short readShort();
	int readUnsignedShort();
	int readInt();
	long readUnsignedInt();
	long readLong();
	BigInteger readUnsignedLong();
	void skip(int num);
	byte[] read(int num);
	float readFloat();
	double readDouble();
	String readAsciiString(int n);
	String readLEAsciiString(int n);
	String readNullTerminatedAsciiString();
	String readLENullTerminatedAsciiString();
	int getBytesRead();
	int available();
}