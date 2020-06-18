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

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

final class AuthDecoder extends ByteToMessageDecoder {
//	private static final Logger LOG = LoggerFactory.getLogger(AuthDecoder.class);

	AuthDecoder() {
		// keep it package-private
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		/*
		 * TODO We can't always rely on this since TCP packets can be fragmented, thus
		 * we have to use the SRP-6a protocol to determine the length of the packet
		 * based on the state of where we are in the protocol. This will need to be
		 * refactored.
		 */
		int length = in.readableBytes();
		// TODO Use pooled buffers
		ByteBuf buf = Unpooled.buffer(length, length);
		in.readBytes(buf, length);
		out.add(buf);
	}
}