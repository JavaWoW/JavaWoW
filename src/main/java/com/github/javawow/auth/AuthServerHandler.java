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

package com.github.javawow.auth;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.auth.handler.BasicAuthHandler;
import com.github.javawow.auth.handler.LoginRequestHandler;
import com.github.javawow.auth.handler.LoginVerifyHandler;
import com.github.javawow.auth.handler.RealmListRequestHandler;
import com.github.javawow.auth.message.LoginProofMessage;
import com.github.javawow.auth.message.LoginRequestMessage;
import com.github.javawow.auth.message.RealmlistRequestMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
final class AuthServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerHandler.class);
	private static final AuthServerHandler INSTANCE = new AuthServerHandler();
	private static final Map<Class<?>, BasicAuthHandler<?>> handlers = new HashMap<>();

	static {
		handlers.put(LoginRequestMessage.class, LoginRequestHandler.getInstance());
		handlers.put(LoginProofMessage.class, LoginVerifyHandler.getInstance());
		handlers.put(RealmlistRequestMessage.class, RealmListRequestHandler.getInstance());
	}

	private AuthServerHandler() {
		// singleton
	}

	public static final AuthServerHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final void channelActive(ChannelHandlerContext ctx) throws Exception {
		// By default, when a new client is opened it is unauthenticated
		ctx.channel().attr(AuthState.ATTRIBUTE_KEY).set(AuthState.UNAUTHENTICATED);
		super.channelActive(ctx);
	}

	@Override
	public final void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		@SuppressWarnings("rawtypes")
		BasicAuthHandler handler = handlers.get(msg.getClass());
		if (handler != null) {
			Channel ch = ctx.channel();
			if (handler.hasValidState(ch)) {
				handler.handleMessage(ch, msg);
			} else {
				LOGGER.warn("Invalid state detected for handler: {}", handler.getClass().getName());
			}
		} else {
			LOGGER.warn("Unhandled Message: {}", msg);
		}
	}

	@Override
	public final void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error(cause.getLocalizedMessage(), cause);
	}
}