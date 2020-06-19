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

package com.github.javawow.auth.message;

import java.math.BigInteger;
import java.util.Objects;

import org.bouncycastle.util.Arrays;

import com.google.errorprone.annotations.Immutable;

/**
 * WoW client login proof message.
 * 
 * @author Jon Huang
 *
 */
@Immutable
public final class LoginProofMessage {
	private final BigInteger a;
	private final BigInteger m1;
	private final byte[] crcHash; // length 20
	private final byte numKeys; // number of keys
	private final byte securityFlags;

	public LoginProofMessage(BigInteger a, BigInteger m1, byte[] crcHash, byte numKeys, byte securityFlags) {
		Objects.requireNonNull(a, "A cannot be null");
		Objects.requireNonNull(m1, "M1 cannot be null");
		Objects.requireNonNull(crcHash, "CRC hash cannot be null");
		if (crcHash.length != 20) {
			throw new IllegalArgumentException("CRC hash must be length 20");
		}
		this.a = a;
		this.m1 = m1;
		this.crcHash = Arrays.copyOf(crcHash, crcHash.length);
		this.numKeys = numKeys;
		this.securityFlags = securityFlags;
	}

	public final BigInteger getA() {
		return a;
	}

	public final BigInteger getM1() {
		return m1;
	}

	public final byte[] getCrcHash() {
		return Arrays.copyOf(crcHash, crcHash.length); // defensive copy
	}

	public final byte getNumKeys() {
		return numKeys;
	}

	public final byte getSecurityFlags() {
		return securityFlags;
	}
}