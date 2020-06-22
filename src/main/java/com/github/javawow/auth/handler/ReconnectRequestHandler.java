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
import com.github.javawow.auth.message.ReconnectRequestMessage;
import com.github.javawow.tools.RandomUtil;
import com.github.javawow.tools.packet.AuthPacketFactory;

import io.netty.channel.Channel;

public final class ReconnectRequestHandler implements BasicAuthHandler<ReconnectRequestMessage> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReconnectRequestHandler.class);
	private static final ReconnectRequestHandler INSTANCE = new ReconnectRequestHandler();

	private ReconnectRequestHandler() {
		// singleton
	}

	public static final ReconnectRequestHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final boolean hasValidState(Channel channel) {
		return channel.attr(AuthServer.SRP_ATTR).get() != null;
	}

	@Override
	public final void handleMessage(Channel channel, ReconnectRequestMessage msg) {
		String arch = msg.getArch();
		String os = msg.getOs();
		String locale = msg.getLocale();
		LOGGER.info("Arch: {} OS: {} Locale: {}", arch, os, locale);
//		int timezone = msg.getTimezone();
//		int ip = msg.getIp();
		byte[] random = new byte[16];
//		RandomUtil.getSecureRandom().nextBytes(random);
		RandomUtil.getRandom().nextBytes(random);
		channel.writeAndFlush(AuthPacketFactory.getReconnectChallenge(random, LoginRequestHandler.VERSION_CHALLENGE));
	}
}