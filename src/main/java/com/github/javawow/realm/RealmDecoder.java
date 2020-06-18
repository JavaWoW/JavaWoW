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

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

final class RealmDecoder extends ByteToMessageDecoder {
//	private static final Logger LOG = LoggerFactory.getLogger(RealmDecoder.class);

	RealmDecoder() {
		// keep it package-private
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 2) {
			// not enough data for a decode
			return;
		}
		in.markReaderIndex();
		int packetLength = in.readShort();
		if (in.readableBytes() < packetLength) {
			// still not enough data
			in.resetReaderIndex();
			return;
		}
		ByteBuf buf = Unpooled.buffer(packetLength, packetLength);
		in.readBytes(buf, packetLength);
		out.add(buf);
	}
}