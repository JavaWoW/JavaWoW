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

package com.github.javawow.tools.packet;

/**
 * Represents a World of Warcraft packet. Contains an opcode (operation code)
 * and the buffer containing the payload of the packet.
 * 
 * @author Jon Huang
 *
 * @param <T> The payload type
 */
public interface WoWPacket<T> {
	/**
	 * Determines if the given packet is a large packet (size > 32767).
	 * 
	 * @return {@code true} if the given packet is a large packet, {@code false}
	 *         otherwise
	 */
//	boolean isLargePacket();

	/**
	 * Fetches the Operation Code of this packet.
	 * 
	 * @return the operation code
	 */
	int getOpCode();

	/**
	 * Fetches the payload of the packet.
	 * 
	 * @return the payload of the packet
	 */
	T getPayload();
}