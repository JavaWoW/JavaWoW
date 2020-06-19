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

import com.google.errorprone.annotations.Immutable;

/**
 * WoW client reconnect proof message.
 * 
 * @author Jon Huang
 *
 */
@Immutable
public final class ReconnectProofMessage {
	private final byte[] R1;
	private final byte[] R2;
	private final byte[] R3;
	private final byte numKeys; // number of keys

	public ReconnectProofMessage(byte[] r1, byte[] r2, byte[] r3, byte numKeys) {
		this.R1 = r1;
		this.R2 = r2;
		this.R3 = r3;
		this.numKeys = numKeys;
	}

	public final byte[] getR1() {
		return R1;
	}

	public final byte[] getR2() {
		return R2;
	}

	public final byte[] getR3() {
		return R3;
	}

	public final byte getNumKeys() {
		return numKeys;
	}
}