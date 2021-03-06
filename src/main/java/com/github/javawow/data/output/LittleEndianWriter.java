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


/**
 * @author Jon
 *
 */
public interface LittleEndianWriter {
	void writeZeroBytes(int i);
	void write(byte b[]);
	void write(byte b);
	void write(int b);
	void writeShort(int s);
	void writeInt(int i);
	void writeInt(long i);
	void writeLong(long l);
	void writeFloat(float f);
	void writeBEFloat(float f);
	void writeDouble(double d);
	void writeBEDouble(double d);
	void writeAsciiString(String s);
	void writeAsciiString(String s, int max);
	void writeNullTerminatedAsciiString(String s);
}