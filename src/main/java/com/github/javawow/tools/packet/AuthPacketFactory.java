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

package com.github.javawow.tools.packet;

import java.math.BigInteger;

import com.github.javawow.data.output.LittleEndianWriterStream;
import com.github.javawow.tools.BitTools;

/**
 * Provides functions for building authentication packets.
 * 
 * @author Jon Huang
 *
 */
public final class AuthPacketFactory {
	private AuthPacketFactory() {
		// static utility class
	}

	public static byte[] getLoginChallenge(BigInteger B, BigInteger g, BigInteger N, byte[] s, byte[] versionChallenge,
			byte securityFlags) {
		LittleEndianWriterStream lew = new LittleEndianWriterStream();
		lew.write(0); // AUTH_LOGON_CHALLENGE
		lew.write(0); // ?
		lew.write(0); // WOW_SUCCESS
		lew.write(BitTools.toLEByteArray(B, 32));
		lew.write(1);
		lew.write(g.toByteArray());
		byte[] N_bytes = BitTools.toLEByteArray(N, 32);
		lew.write(N_bytes.length);
		lew.write(N_bytes);
		lew.write(s); // salt
		lew.write(versionChallenge);
		lew.write(securityFlags); // security flags
		if ((securityFlags & 0x1) == 0x1) {
			lew.writeInt(0);
			lew.writeLong(0);
			lew.writeLong(0);
		}
		if ((securityFlags & 0x2) == 0x2) {
			lew.write(0);
			lew.write(0);
			lew.write(0);
			lew.write(0);
			lew.writeLong(0);
		}
		if ((securityFlags & 0x4) == 0x4) {
			lew.write(1);
		}
		return lew.toByteArray();
	}

	public static byte[] getLoginProof(BigInteger m2) {
		LittleEndianWriterStream lew = new LittleEndianWriterStream();
		lew.write(1); // AUTH_LOGON_PROOF
		lew.write(0); // error
		lew.write(BitTools.toByteArray(m2, 20));
		int accountFlag = 0x00800000; // 0x01 = GM, 0x08 = Trial, 0x00800000 = Pro pass (arena tournament)
		lew.writeInt(accountFlag);
		lew.writeInt(0); // SurveyId
		lew.writeShort(0); // LoginFlags
		return lew.toByteArray();
	}

	public static byte[] getReconnectChallenge(byte[] random, byte[] versionChallenge) {
		if (random.length != 16) {
			throw new IllegalArgumentException("random bytes must be length 16");
		}
		LittleEndianWriterStream lew = new LittleEndianWriterStream();
		lew.write(2); // AUTH_RECONNECT_CHALLENGE
		lew.write(0); // WOW_SUCCESS
		lew.write(random);
		lew.write(versionChallenge);
		return lew.toByteArray();
	}
}