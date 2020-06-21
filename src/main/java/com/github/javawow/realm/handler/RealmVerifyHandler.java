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

package com.github.javawow.realm.handler;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.data.input.SeekableLittleEndianAccessor;
import com.github.javawow.data.output.LittleEndianWriterStream;
import com.github.javawow.realm.RealmDecoder;
import com.github.javawow.realm.RealmEncoder;
import com.github.javawow.tools.CryptoUtil;
import com.github.javawow.tools.FileUtil;

import io.netty.channel.Channel;

public final class RealmVerifyHandler implements BasicRealmHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealmVerifyHandler.class);
	private static final RealmVerifyHandler INSTANCE = new RealmVerifyHandler();

	private RealmVerifyHandler() {
	}

	public static final RealmVerifyHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final boolean hasValidState(Channel channel) {
		return true; // XXX Any other ways?
	}

	@Override
	public final void handlePacket(Channel channel, SeekableLittleEndianAccessor slea) {
		slea.skip(2); // ?
		int buildNumber = slea.readInt();
		int serverId = slea.readInt(); // server Id
		String username = slea.readNullTerminatedAsciiString();
		int loginServerType = slea.readInt(); // login server type
		int clientSeed = slea.readInt(); // client seed
		int region = slea.readInt(); // region
		int battleGroup = slea.readInt(); // battle group
		int realmIndex = slea.readInt(); // realm index
		long dosResponse = slea.readLong(); // unk4
		byte[] digest = slea.read(20);
		System.out.println("Build Number: " + buildNumber);
		System.out.println("Server ID: " + serverId);
		System.out.println("Username: " + username);
		System.out.println("Login Server Type: " + loginServerType);
		System.out.println("Client Seed: " + clientSeed);
		System.out.println("Region: " + region);
		System.out.println("Battle Group: " + battleGroup);
		System.out.println("Realm Index: " + realmIndex);
		System.out.println("DosResponse: " + dosResponse);
		System.out.println("Digest: " + Arrays.toString(digest));
		LOGGER.info(slea.toString());
		// Read session key from the DB
		BigInteger K;
		try {
			K = FileUtil.getSessionKey();
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			channel.close();
			return;
		}
		// TODO Initialize encryption (RC4)
		Cipher clientDecryptCipher = CryptoUtil.createRC4Cipher(true, K);
		Cipher serverEncryptCipher = CryptoUtil.createRC4Cipher(false, K);
		// Set the ciphers for WoW packet encryption/decryption
		channel.attr(RealmDecoder.DECRYPT_CIPHER_KEY).set(clientDecryptCipher);
		channel.attr(RealmEncoder.ENCRYPT_CIPHER_KEY).set(serverEncryptCipher);
		
		boolean error = true;
		if (error) {
			LittleEndianWriterStream lews = new LittleEndianWriterStream();
			lews.writeShort(0x01EE); // header
			lews.write(13); // code (success/error)
			channel.writeAndFlush(lews.toByteArray());
		} else {
			LittleEndianWriterStream lews = new LittleEndianWriterStream();
			lews.writeShort(0x01EE); // header
			lews.write(12); // code (success/error)
			lews.writeInt(0); // BillingTimeRemaining
			lews.write(0); // BillingPlanFlags
			lews.writeInt(0); // BillingTimeRested
			lews.write(2); // 0 - normal, 1 - TBC, 2 - WOTLK, must be set in database manually for each
							// account
			// queue information
			lews.writeInt(1); // Queue position
			lews.write(0); // Realm has a free character migration - bool
			// end queue information
			channel.writeAndFlush(lews.toByteArray());
		}
	}
}