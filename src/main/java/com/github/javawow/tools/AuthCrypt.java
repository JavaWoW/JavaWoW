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

package com.github.javawow.tools;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Authentication cryptographic utility.
 * 
 * @author Jon Huang
 *
 */
public final class AuthCrypt {
	private static final String HMAC_ALGORITHM = "HmacSHA1";
	private static final byte[] ENCRYPTION_KEY = { (byte) 0xCC, (byte) 0x98, (byte) 0xAE, 0x04, (byte) 0xE8,
			(byte) 0x97, (byte) 0xEA, (byte) 0xCA, 0x12, (byte) 0xDD, (byte) 0xC0, (byte) 0x93, 0x42, (byte) 0x91, 0x53,
			0x57 };
	private static final byte[] DECRYPTION_KEY = { (byte) 0xC2, (byte) 0xB3, 0x72, 0x3C, (byte) 0xC6, (byte) 0xAE,
			(byte) 0xD9, (byte) 0xB5, 0x34, 0x3C, 0x53, (byte) 0xEE, 0x2F, 0x43, 0x67, (byte) 0xCE };

	private AuthCrypt() {
		// static utility class
	}

	public static final byte[] encryptHMacSHA1(byte[] input) {
		return doHMacSHA1(ENCRYPTION_KEY, input);
	}

	public static final byte[] decryptHMacSHA1(byte[] input) {
		return doHMacSHA1(DECRYPTION_KEY, input);
	}

	private static final byte[] doHMacSHA1(byte[] key, byte[] input) {
		try {
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			SecretKeySpec keySpec = new SecretKeySpec(key, HMAC_ALGORITHM);
			mac.init(keySpec);
			return mac.doFinal(input);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			// should not happen
			throw new AssertionError(e);
		}
	}
}