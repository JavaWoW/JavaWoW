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

import java.util.Objects;

import org.bouncycastle.util.Arrays;

import com.google.errorprone.annotations.Immutable;

/**
 * WoW client reconnect proof message.
 * 
 * @author Jon Huang
 *
 */
@Immutable
public final class ReconnectProofMessage {
	@SuppressWarnings("Immutable")
	private final byte[] R1;
	@SuppressWarnings("Immutable")
	private final byte[] R2;
	@SuppressWarnings("Immutable")
	private final byte[] R3;
	private final byte numKeys; // number of keys

	public ReconnectProofMessage(byte[] r1, byte[] r2, byte[] r3, byte numKeys) {
		Objects.requireNonNull(r1, "r1 must not be null");
		Objects.requireNonNull(r2, "r2 must not be null");
		Objects.requireNonNull(r3, "r3 must not be null");
		if (r1.length != 16) {
			throw new IllegalArgumentException("r1 must be length 16");
		}
		if (r2.length != 20) {
			throw new IllegalArgumentException("r2 must be length 20");
		}
		if (r3.length != 20) {
			throw new IllegalArgumentException("r3 must be length 20");
		}
		this.R1 = Arrays.copyOf(r1, 16);
		this.R2 = Arrays.copyOf(r2, 16);
		this.R3 = Arrays.copyOf(r3, 16);
		this.numKeys = numKeys;
	}

	public final byte[] getR1() {
		return Arrays.copyOf(R1, R1.length);
	}

	public final byte[] getR2() {
		return Arrays.copyOf(R2, R2.length);
	}

	public final byte[] getR3() {
		return Arrays.copyOf(R3, R3.length);
	}

	public final byte getNumKeys() {
		return numKeys;
	}
}