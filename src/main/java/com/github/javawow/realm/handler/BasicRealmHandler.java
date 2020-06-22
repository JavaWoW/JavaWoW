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

package com.github.javawow.realm.handler;

import com.github.javawow.data.input.SeekableLittleEndianAccessor;

import io.netty.channel.Channel;

public interface BasicRealmHandler {
	/**
	 * Verifies if the current state is valid for the handler to be executed.
	 * 
	 * @param channel The channel attempting to execute the handler.
	 * @return {@code true} for a valid state, {@code false} otherwise
	 */
	boolean hasValidState(Channel channel);

	/**
	 * Implement this method to handle the operation to perform when the handler is
	 * called.
	 * 
	 * @param channel The channel executing this handler.
	 * @param slea    The message from the client
	 */
	void handlePacket(Channel channel, SeekableLittleEndianAccessor slea);
}