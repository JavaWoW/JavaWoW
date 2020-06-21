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

package com.github.javawow.tools.srp;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.srp.SRP6Util;
import org.bouncycastle.util.BigIntegers;

import com.github.javawow.tools.BitTools;

/**
 * Provides functions unique to the WoW implementation of SRP-6.
 * 
 * @author Jon Huang
 *
 */
public final class WoWSRP6Util extends SRP6Util {
	private WoWSRP6Util() {
		// static utility class
	}

	public static BigInteger calculateK(Digest digest, BigInteger N, BigInteger g) {
		return hashPaddedPair(digest, N, N, g);
	}

	public static BigInteger calculateU(Digest digest, BigInteger N, BigInteger A, BigInteger B) {
		return hashPaddedPair(digest, N, A, B);
	}

	public static BigInteger calculateX(Digest digest, BigInteger N, byte[] salt, byte[] identity, byte[] password) {
		byte[] output = new byte[digest.getDigestSize()];
		digest.update(identity, 0, identity.length);
		digest.update((byte) ':');
		digest.update(password, 0, password.length);
		digest.doFinal(output, 0);
		digest.update(salt, 0, salt.length);
		digest.update(output, 0, output.length);
		digest.doFinal(output, 0);
		output = BitTools.reverse(output);
		return new BigInteger(1, output);
	}

	public static BigInteger generatePrivateValue(Digest digest, BigInteger N, BigInteger g, SecureRandom random) {
		int minBits = Math.min(256, N.bitLength() / 2);
		BigInteger min = BigInteger.ONE.shiftLeft(minBits - 1);
		BigInteger max = N.subtract(BigInteger.ONE);
		return BigIntegers.createRandomInRange(min, max, random);
	}

	public static BigInteger validatePublicValue(BigInteger N, BigInteger val) throws CryptoException {
		val = val.mod(N);
		// Check that val % N != 0
		if (val.equals(BigInteger.ZERO)) {
			throw new CryptoException("Invalid public value: 0");
		}
		return val;
	}

	/**
	 * Computes the client evidence message (M1) according to the standard routine:
	 * M1 = H( H( N ) ^ H( g ) | H( I ) | s | A | B | H( S ) )
	 * 
	 * @param digest The Digest used as the hashing function H
	 * @param N      Modulus used to get the pad length
	 * @param A      The public client value
	 * @param B      The public server value
	 * @param S      The secret calculated by both sides
	 * @return M1 The calculated client evidence message
	 */
	public static BigInteger calculateM1(Digest digest, BigInteger N, BigInteger g, byte[] I, byte[] s, BigInteger A,
			BigInteger B, BigInteger S) {
		BigInteger K = calculateKey(digest, N, S);
		int padLength = (N.bitLength() + 7) / 8;
		byte[] N_bytes = BitTools.reverse(getPadded(N, padLength));
		digest.update(N_bytes, 0, N_bytes.length);
		byte[] N_digested = new byte[digest.getDigestSize()]; // H( N )
		digest.doFinal(N_digested, 0);
		byte[] g_bytes = BitTools.reverse(BigIntegers.asUnsignedByteArray(g));
		digest.update(g_bytes, 0, g_bytes.length);
		byte[] g_digested = new byte[digest.getDigestSize()]; // H( g )
		digest.doFinal(g_digested, 0);
		byte[] product = new byte[digest.getDigestSize()]; // H( N ) ^ H( g )
		for (int i = 0; i < product.length; i++) {
			product[i] = (byte) (N_digested[i] ^ g_digested[i]);
		}
		digest.update(I, 0, I.length);
		byte[] I_digested = new byte[digest.getDigestSize()]; // H( I )
		digest.doFinal(I_digested, 0);
		byte[] A_bytes = BitTools.reverse(getPadded(A, padLength));
		byte[] B_bytes = BitTools.reverse(getPadded(B, padLength));
		byte[] K_bytes = BigIntegers.asUnsignedByteArray(K);
		// Add in the bytes now
		digest.update(product, 0, product.length); // H( N ) ^ H( g )
		digest.update(I_digested, 0, I_digested.length); // H( I )
		digest.update(s, 0, s.length); // s
		digest.update(A_bytes, 0, A_bytes.length); // A
		digest.update(B_bytes, 0, B_bytes.length); // B
		digest.update(K_bytes, 0, K_bytes.length); // H( S )
		byte[] output = new byte[digest.getDigestSize()];
		digest.doFinal(output, 0);
		BigInteger M1 = new BigInteger(1, output);
		return M1;
	}

	/**
	 * Computes the server evidence message (M2) according to the standard routine:
	 * M2 = H( A | M1 | H( S ) )
	 * 
	 * @param digest The Digest used as the hashing function H
	 * @param N      Modulus used to get the pad length
	 * @param A      The public client value
	 * @param M1     The client evidence message
	 * @param S      The secret calculated by both sides
	 * @return M2 The calculated server evidence message
	 */
	public static BigInteger calculateM2(Digest digest, BigInteger N, BigInteger A, BigInteger M1, BigInteger S) {
		BigInteger K = calculateKey(digest, N, S);
		int padLength = (N.bitLength() + 7) / 8;
		byte[] A_bytes = BitTools.reverse(getPadded(A, padLength));
		byte[] M1_bytes = BigIntegers.asUnsignedByteArray(M1);
		byte[] K_bytes = BigIntegers.asUnsignedByteArray(K);
		digest.update(A_bytes, 0, A_bytes.length); // A
		digest.update(M1_bytes, 0, M1_bytes.length); // M1
		digest.update(K_bytes, 0, K_bytes.length); // K
		byte[] output = new byte[digest.getDigestSize()];
		digest.doFinal(output, 0);
		BigInteger M2 = new BigInteger(1, output);
		return M2;
	}

	/**
	 * Computes the final Key according to the standard routine: Key = H(S)
	 * 
	 * @param digest The Digest used as the hashing function H
	 * @param N      Modulus used to get the pad length
	 * @param S      The secret calculated by both sides
	 * @return
	 */
	public static BigInteger calculateKey(Digest digest, BigInteger N, BigInteger S) {
		// Take S and convert it into a little-endian byte array
		byte[] S_le = BitTools.toLEByteArray(S, 32);
		byte[] S_even_bytes = new byte[16];
		byte[] S_odd_bytes = new byte[16];
		// Read in the even-indexed bytes
		for (int i = 0, j = 0; i < 32; i += 2, j++) {
			S_even_bytes[j] = S_le[i];
			S_odd_bytes[j] = S_le[i + 1];
		}
		digest.update(S_even_bytes, 0, S_even_bytes.length);
		byte[] S_even_digested_bytes = new byte[digest.getDigestSize()];
		digest.doFinal(S_even_digested_bytes, 0);
		digest.update(S_odd_bytes, 0, S_odd_bytes.length);
		byte[] S_odd_digested_bytes = new byte[digest.getDigestSize()];
		digest.doFinal(S_odd_digested_bytes, 0);
		byte[] K_bytes = new byte[digest.getDigestSize() * 2];
		// copy the bytes in
		for (int i = 0, j = 0; i < 40; i += 2, j++) {
			K_bytes[i] = S_even_digested_bytes[j];
			K_bytes[i + 1] = S_odd_digested_bytes[j];
		}
		return new BigInteger(1, K_bytes);
	}

	private static BigInteger hashPaddedPair(Digest digest, BigInteger N, BigInteger n1, BigInteger n2) {
		int padLength = (N.bitLength() + 7) / 8;
		byte[] n1_bytes = BitTools.reverse(getPadded(n1, padLength));
		byte[] n2_bytes = BitTools.reverse(getPadded(n2, padLength));
		digest.update(n1_bytes, 0, n1_bytes.length);
		digest.update(n2_bytes, 0, n2_bytes.length);
		byte[] output = new byte[digest.getDigestSize()];
		digest.doFinal(output, 0);
		output = BitTools.reverse(output); // reverse output
		return new BigInteger(1, output);
	}

	private static byte[] getPadded(BigInteger n, int length) {
		byte[] bs = BigIntegers.asUnsignedByteArray(n);
		if (bs.length < length) {
			byte[] tmp = new byte[length];
			System.arraycopy(bs, 0, tmp, length - bs.length, bs.length);
			bs = tmp;
		}
		return bs;
	}
}