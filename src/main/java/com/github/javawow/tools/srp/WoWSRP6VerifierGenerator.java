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

import org.bouncycastle.crypto.agreement.srp.SRP6VerifierGenerator;

public final class WoWSRP6VerifierGenerator extends SRP6VerifierGenerator {
	/**
	 * Creates a new SRP verifier
	 * 
	 * @param salt     The salt to use, generally should be large and random
	 * @param identity The user's identifying information (eg. username)
	 * @param password The user's password
	 * @return A new verifier for use in future SRP authentication
	 */
	@Override
	public final BigInteger generateVerifier(byte[] salt, byte[] identity, byte[] password) {
		BigInteger x = WoWSRP6Util.calculateX(digest, N, salt, identity, password);
		return g.modPow(x, N);
	}
}