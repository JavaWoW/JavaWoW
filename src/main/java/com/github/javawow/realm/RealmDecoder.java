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

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

public final class RealmDecoder extends ByteToMessageDecoder {
	private static final Logger LOG = LoggerFactory.getLogger(RealmDecoder.class);
	public static final AttributeKey<Cipher> DECRYPT_CIPHER_KEY = AttributeKey.newInstance("RC4_CIPHER_DECRYPT");

	RealmDecoder() {
		// keep it package-private
	}

	@Override
	protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 6) {
			// not enough data for a decode
			return;
		}
		LOG.info("Received:\n{}", ByteBufUtil.prettyHexDump(in));
		in.markReaderIndex();
		Cipher decryptCipher = ctx.channel().attr(DECRYPT_CIPHER_KEY).get();
		if (decryptCipher == null) {
			// Encryption has not started yet, read the packets plain
			int packetLength = in.readShort();
			System.out.println("Unencrypted Packet Length: " + packetLength);
			if (in.readableBytes() < packetLength) {
				// still not enough data
				in.resetReaderIndex();
				return;
			}
//			ByteBuf buf = ctx.alloc().buffer(packetLength, packetLength);
			ByteBuf buf = Unpooled.buffer(packetLength, packetLength);
			in.readBytes(buf, packetLength);
			out.add(buf);
		} else {
			// Encryption is active, therefore the header is encrypted
			// header length: packet size (2 bytes) + opcode (4 bytes)
			byte[] encHeaderBuf = new byte[6]; // buffer to hold the encrypted header
			byte[] headerBuf = new byte[6]; // buffer to hold the decrypted header
			try {
				in.readBytes(encHeaderBuf);
				int decryptLen = decryptCipher.doFinal(encHeaderBuf, 0, 6, headerBuf, 0);
				ByteBuf clientHeader = Unpooled.wrappedBuffer(headerBuf);
				System.out.println("Decrypted Header:\n" + ByteBufUtil.prettyHexDump(clientHeader));
				int packetLength = clientHeader.readShort();
				System.out.println("Determined packet length: " + packetLength);
				if (in.readableBytes() < packetLength) {
					// still not enough data
					in.resetReaderIndex();
					return;
				}
				int opcode = clientHeader.readIntLE();
//				ByteBuf buf = ctx.alloc().buffer(packetLength, packetLength);
				ByteBuf buf = Unpooled.buffer(packetLength, packetLength);
				buf.writeIntLE(opcode);
				in.readBytes(buf, packetLength - 4);
				out.add(buf);
			} finally {
				// release the buffers we just created
//				encHeaderBuf.release();
//				headerBuf.release();
			}
		}
	}
}