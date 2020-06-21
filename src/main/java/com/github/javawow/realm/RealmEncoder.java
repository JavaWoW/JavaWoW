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

import javax.crypto.Cipher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;

@Sharable
public final class RealmEncoder extends MessageToByteEncoder<byte[]> {
	private static final RealmEncoder INSTANCE = new RealmEncoder();
	public static final AttributeKey<Cipher> ENCRYPT_CIPHER_KEY = AttributeKey.newInstance("RC4_CIPHER_ENCRYPT");

	private RealmEncoder() {
		// singleton
	}

	public static final RealmEncoder getInstance() {
		return INSTANCE;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
		Cipher encryptCipher = ctx.channel().attr(ENCRYPT_CIPHER_KEY).get();
		if (encryptCipher == null) {
			// Encryption has not started yet, write the packets plain
			out.writeShort(msg.length);
			out.writeBytes(msg);
		} else {
			// Encryption is active, therefore the header must be encrypted
			ByteBuf encHeaderBuf = Unpooled.buffer(2, 3);
			encryptCipher.doFinal(msg, 0, 2, encHeaderBuf.array(), 0);
			out.writeBytes(encHeaderBuf);
			out.writeBytes(msg, 2, msg.length - 2);
		}
	}
}