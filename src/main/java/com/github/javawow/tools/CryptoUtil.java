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

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.BigIntegers;

/**
 * WoW Cryptographic Utilities.
 * 
 * @author Jon Huang
 *
 */
public final class CryptoUtil {
	private static final String HMAC_ALGORITHM = "HmacSHA1";
	private static final byte[] ENCRYPTION_KEY = { (byte) 0xCC, (byte) 0x98, (byte) 0xAE, 0x04, (byte) 0xE8,
			(byte) 0x97, (byte) 0xEA, (byte) 0xCA, 0x12, (byte) 0xDD, (byte) 0xC0, (byte) 0x93, 0x42, (byte) 0x91, 0x53,
			0x57 };
	private static final byte[] DECRYPTION_KEY = { (byte) 0xC2, (byte) 0xB3, 0x72, 0x3C, (byte) 0xC6, (byte) 0xAE,
			(byte) 0xD9, (byte) 0xB5, 0x34, 0x3C, 0x53, (byte) 0xEE, 0x2F, 0x43, 0x67, (byte) 0xCE };
	private static final String RC4_ALGORITHM = "RC4";
	/**
	 * This is the source buffer for the RC4-drop1024 input. We expect to read in an
	 * array of bytes that contain {@code 0}s. Java by default will automatically
	 * initialize the array values to 0.
	 */
	private static final byte[] ZEROES = new byte[1024];
	/**
	 * This is the destination buffer for the RC4-drop1024 output. We do not care
	 * about the contents of this array and only define this here to avoid
	 * allocating a new buffer then relying on RC to deallocate the buffer each time
	 * we create a new RC4-drop1024 {@code Cipher} instance.
	 */
	private static final byte[] DROPPED = new byte[1024];

	private CryptoUtil() {
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

	/**
	 * Creates a WoW specific RC4-drop1024 {@code Cipher} from the given SRP-6
	 * session key (K). It is suggested to use the
	 * {@link Cipher#update(byte[], int, int, byte[], int)} method for
	 * encryption/decryption. Do <b>not</b> perform a call to
	 * {@link Cipher#doFinal()} or its variants as this will reset the internal
	 * state of the RC-4 cipher.
	 * 
	 * @param encrypt {@code true} if the cipher is to be used for encryption
	 *                (server-side), {@code false} if it is to be used for
	 *                decryption (client-side)
	 * @param K       The session key
	 * @return a initialized {@code Cipher} for encryption/decryption
	 */
	public static final Cipher createRC4Cipher(boolean encrypt, BigInteger K) {
		Objects.requireNonNull(K, "session key cannot be null");
		byte[] sessionKey = BigIntegers.asUnsignedByteArray(K);
		byte[] key;
		// Generate the RC4 cipher key using the SRP-6a session key
		if (encrypt) {
			key = encryptHMacSHA1(sessionKey);
		} else {
			key = decryptHMacSHA1(sessionKey);
		}
		// Create the RC4 cipher
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(RC4_ALGORITHM);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// should never happen
			throw new AssertionError(RC4_ALGORITHM + " is not provided by the cryptography context");
		}
		SecretKeySpec keySpec = new SecretKeySpec(key, RC4_ALGORITHM);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		} catch (InvalidKeyException e) {
			// should never happen
			throw new RuntimeException(e);
		}
		// Drop the first 1024 bytes, as WoW uses RC4-drop1024
		try {
			int droppedLen = cipher.update(ZEROES, 0, 1024, DROPPED, 0);
			if (droppedLen != 1024) {
				throw new RuntimeException("unable to drop 1024 bytes from RC4");
			}
		} catch (ShortBufferException e) {
			// should never happen
			throw new RuntimeException(e);
		}
		return cipher;
	}
}