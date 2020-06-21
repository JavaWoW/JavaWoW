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

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.auth.message.LoginProofMessage;
import com.github.javawow.auth.message.LoginRequestMessage;
import com.github.javawow.auth.message.RealmlistRequestMessage;
import com.github.javawow.auth.message.ReconnectProofMessage;
import com.github.javawow.auth.message.ReconnectRequestMessage;
import com.github.javawow.tools.BitTools;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

final class AuthDecoder extends ByteToMessageDecoder {
	private static final Logger LOG = LoggerFactory.getLogger(AuthDecoder.class);

	AuthDecoder() {
		// keep it package-private
	}

	@Override
	protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// Mark reader index in case we need to restore
		in.markReaderIndex();
		// Read the command byte
		byte cmd = in.readByte();
		if (cmd == 0x0) { // ClientLink::CMD_AUTH_LOGON_CHALLENGE (008CC3E0)
			// initial length is 33
			if (in.readableBytes() < 33) {
				// not enough data to decode
				// reset reader index
				in.resetReaderIndex();
				return;
			}
			byte error = in.readByte();
			short size = in.readShortLE();
			byte[] gamename = new byte[4];
			in.readBytes(gamename);
			BitTools.reverseBuffer(gamename);
			byte majorVersion = in.readByte();
			byte minorVersion = in.readByte();
			byte patchVersion = in.readByte();
			short build = in.readShortLE();
			byte[] arch = new byte[4];
			in.readBytes(arch);
			BitTools.reverseBuffer(arch);
			byte[] os = new byte[4];
			in.readBytes(os);
			BitTools.reverseBuffer(os);
			byte[] locale = new byte[4];
			in.readBytes(locale);
			locale = BitTools.reverse(locale);
			int timezone = in.readIntLE();
			int ip = in.readIntLE();
			byte iLength = in.readByte();
			if (in.readableBytes() < iLength) {
				// not enough data to decode
				// reset reader index
				in.resetReaderIndex();
				return;
			}
			byte[] i = new byte[iLength];
			in.readBytes(i);
			out.add(new LoginRequestMessage(error, size, gamename, majorVersion, minorVersion, patchVersion, build,
					arch, os, locale, timezone, ip, iLength, i));
		} else if (cmd == 0x1) { // login verification
			if (in.readableBytes() < 74) {
				// not enough data to decode
				// reset reader index
				in.resetReaderIndex();
				return;
			}
			byte[] A_bytes = new byte[32];
			byte[] M1_bytes = new byte[20];
			byte[] crcHash = new byte[20];
			in.readBytes(A_bytes, 0, A_bytes.length); // little-endian order
			in.readBytes(M1_bytes, 0, M1_bytes.length);
			in.readBytes(crcHash, 0, crcHash.length);
			byte numKeys = in.readByte();
			byte securityFlags = in.readByte();
			BigInteger a = BitTools.toBigInteger(A_bytes, true);
			BigInteger m1 = BitTools.toBigInteger(M1_bytes, false);
			out.add(new LoginProofMessage(a, m1, crcHash, numKeys, securityFlags));
		} else if (cmd == 0x2) { // reconnect request
			// initial length is 33
			if (in.readableBytes() < 33) {
				// not enough data to decode
				// reset reader index
				in.resetReaderIndex();
				return;
			}
			byte error = in.readByte();
			short size = in.readShortLE();
			byte[] gamename = new byte[4];
			in.readBytes(gamename);
			BitTools.reverseBuffer(gamename);
			byte majorVersion = in.readByte();
			byte minorVersion = in.readByte();
			byte patchVersion = in.readByte();
			short build = in.readShortLE();
			byte[] arch = new byte[4];
			in.readBytes(arch);
			BitTools.reverseBuffer(arch);
			byte[] os = new byte[4];
			in.readBytes(os);
			BitTools.reverseBuffer(os);
			byte[] locale = new byte[4];
			in.readBytes(locale);
			locale = BitTools.reverse(locale);
			int timezone = in.readIntLE();
			int ip = in.readIntLE();
			byte iLength = in.readByte();
			if (in.readableBytes() < iLength) {
				// not enough data to decode
				// reset reader index
				in.resetReaderIndex();
				return;
			}
			byte[] i = new byte[iLength];
			in.readBytes(i);
			out.add(new ReconnectRequestMessage(error, size, gamename, majorVersion, minorVersion, patchVersion, build,
					arch, os, locale, timezone, ip, iLength, i));
		} else if (cmd == 0x3) { // reconnect proof
			if (in.readableBytes() < 57) {
				// not enough data to decode
				// reset reader index
				in.resetReaderIndex();
				return;
			}
			byte[] R1 = new byte[16];
			byte[] R2 = new byte[20];
			byte[] R3 = new byte[20];
			in.readBytes(R1, 0, R1.length); // little-endian order
			in.readBytes(R2, 0, R2.length);
			in.readBytes(R3, 0, R3.length);
			byte numKeys = in.readByte();
			out.add(new ReconnectProofMessage(R1, R2, R3, numKeys));
		} else if (cmd == 0x10) { // realm list request
			int unk = in.readInt();
			out.add(new RealmlistRequestMessage(unk));
		} else {
			LOG.info("Unhandled Command: " + cmd);
			// reset reader index
			in.resetReaderIndex();
			return;
		}
	}
}