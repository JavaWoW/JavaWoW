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

import com.github.javawow.tools.packet.ByteBufWoWPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;

@Sharable
public final class RealmEncoder extends MessageToByteEncoder<ByteBufWoWPacket> {
	private static final RealmEncoder INSTANCE = new RealmEncoder();
	public static final AttributeKey<Cipher> ENCRYPT_CIPHER_KEY = AttributeKey.newInstance("RC4_CIPHER_ENCRYPT");

	private RealmEncoder() {
		// singleton
	}

	public static final RealmEncoder getInstance() {
		return INSTANCE;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBufWoWPacket msg, ByteBuf out) throws Exception {
		// Header Format:
		// Size (Big Endian) (Variable: 2-3 bytes)
		// Opcode (Little Endian) (2 bytes)
		// Length can be 4 (short packet) or 5 (long packet)
		ByteBuf headerBuf = ctx.alloc().heapBuffer(4, 5); // Must be kept a heap buffer since we call array()
		try {
			ByteBuf payload = msg.getPayload();
			int packetLength = payload.readableBytes() + 2; // payload size + size of the opcode
			if (packetLength > 0x7FFF) { // outside the range of a signed short, we write 24-bits instead
				int lengthMask = packetLength | 0x00800000; // The sign bit on the medium is set to indicate this is a
															// 24-bits value to the client
				headerBuf.writeMedium(lengthMask);
			} else {
				headerBuf.writeShort(packetLength);
			}
			headerBuf.writeShortLE(msg.getOpCode());
			// Determine if the cipher is set and we need to encrypt the header or not
			Cipher encryptCipher = ctx.channel().attr(ENCRYPT_CIPHER_KEY).get();
			if (encryptCipher == null) {
				// Encryption has not started yet, write the header plain
				out.writeBytes(headerBuf);
			} else {
				// Encryption is active, therefore the header must be encrypted
				int headerSize = headerBuf.readableBytes();
				ByteBuf encHeaderBuf = ctx.alloc().heapBuffer(headerSize, headerSize);
				try {
					headerBuf.readBytes(encHeaderBuf, 0, headerSize);
					int updateLen = encryptCipher.update(headerBuf.array(), headerBuf.arrayOffset(),
							headerSize, encHeaderBuf.array(), encHeaderBuf.arrayOffset());
					encHeaderBuf.writerIndex(updateLen); // increment writer index by number of bytes we written
					out.writeBytes(encHeaderBuf);
				} finally {
					encHeaderBuf.release();
				}
			}
			// Write the rest of the packet payload
			out.writeBytes(payload);
		} finally {
			headerBuf.release();
		}
	}
}