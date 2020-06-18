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

import com.github.javawow.data.input.SeekableLittleEndianAccessor;

import io.netty.channel.Channel;

public interface BasicHandler {
	/**
	 * Verifies if the current state is valid for the handler to be executed.
	 * 
	 * @param session The session attempting to execute the handler.
	 * @return true for a valid state, false otherwise
	 */
	boolean hasValidState(Channel session);

	/**
	 * Implement this method to handle the operation to perform when the handler is
	 * called.
	 * 
	 * @param session The session executing this handler.
	 * @param slea    The seekable buffer containing the packet received.
	 */
	void handlePacket(Channel session, SeekableLittleEndianAccessor slea);
}