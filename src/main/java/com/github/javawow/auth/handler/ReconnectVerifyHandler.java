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

package com.github.javawow.auth.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.auth.AuthServer;
import com.github.javawow.auth.message.ReconnectProofMessage;

import io.netty.channel.Channel;

public final class ReconnectVerifyHandler implements BasicAuthHandler<ReconnectProofMessage> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReconnectVerifyHandler.class);
	private static final ReconnectVerifyHandler INSTANCE = new ReconnectVerifyHandler();

	private ReconnectVerifyHandler() {
		// singleton
	}

	public static final ReconnectVerifyHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final boolean hasValidState(Channel channel) {
		return channel.attr(AuthServer.SRP_ATTR).get() != null;
	}

	@Override
	public final void handleMessage(Channel channel, ReconnectProofMessage msg) {
		msg.getR1();
		msg.getR2();
		msg.getR3();
	}
}