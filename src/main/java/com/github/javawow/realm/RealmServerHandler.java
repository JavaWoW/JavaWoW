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

package com.github.javawow.realm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.data.input.GenericSeekableLittleEndianAccessor;
import com.github.javawow.data.input.SeekableByteArrayStream;
import com.github.javawow.data.input.SeekableLittleEndianAccessor;
import com.github.javawow.data.output.LittleEndianWriterStream;
import com.github.javawow.realm.handler.BasicRealmHandler;
import com.github.javawow.realm.handler.RealmVerifyHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
final class RealmServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealmServerHandler.class);
	private static final RealmServerHandler INSTANCE = new RealmServerHandler();
	private static final Map<Short, BasicRealmHandler> handlers = new HashMap<>();

	static {
		handlers.put((short) 0x1ED, RealmVerifyHandler.getInstance()); // CMSG_AUTH_SESSION
	}

	private RealmServerHandler() {
		// singleton
	}

	public static final RealmServerHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("IoSession opened with {}.", ctx.channel().remoteAddress());
		Random r = new Random(1337);
		// Send Authentication Challenge Packet:
		LittleEndianWriterStream lews = new LittleEndianWriterStream(0x01EC);
		lews.writeInt(1); // ?
		byte[] authSeed = new byte[4];
		byte[] seed1 = new byte[16];
		byte[] seed2 = new byte[16];
		r.nextBytes(authSeed);
		r.nextBytes(seed1);
		r.nextBytes(seed2);
		System.out.println("auth seed: " + Arrays.toString(authSeed));
		lews.write(authSeed); // auth seed
		lews.write(seed1); // ?
		lews.write(seed2); // ?
		ctx.channel().writeAndFlush(lews.getPacket());
		LOGGER.debug("Sent Authentication Challenge");
	}

	@Override
	public final void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		LOGGER.debug("IoSession closed with {}.", ctx.channel().remoteAddress());
		super.channelInactive(ctx);
	}

	@Override
	public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new SeekableByteArrayStream(((ByteBuf) msg).array()));
		short header = slea.readShort();
		BasicRealmHandler handler = handlers.get(header);
		if (handler != null) {
			Channel ch = ctx.channel();
			if (handler.hasValidState(ch)) {
				handler.handlePacket(ch, slea);
			} else {
				LOGGER.warn("Invalid state detected for handler: {}", handler.getClass().getName());
			}
		} else {
			LOGGER.warn("Unhandled Packet. Header: 0x{}", Integer.toHexString(header));
		}
	}

	@Override
	public final void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error(cause.getLocalizedMessage(), cause);
	}
}