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

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.google.errorprone.annotations.Immutable;

/**
 * WoW client initial login message.
 * 
 * @author Jon Huang
 *
 */
@Immutable
public final class LoginRequestMessage {
	private final byte error;
	private final short size;
	private final String gamename;
	private final byte majorVersion;
	private final byte minorVersion;
	private final byte patchVersion;
	private final short build;
	private final String arch;
	private final String os;
	private final String locale;
	private final int timezone;
	private final int ip;
	private final byte iLength;
	private final byte[] i;

	public LoginRequestMessage(byte error, short size, byte[] gamename, byte majorVersion, byte minorVersion,
			byte patchVersion, short build, byte[] arch, byte[] os, byte[] locale, int timezone, int ip, byte iLength,
			byte[] i) {
		Objects.requireNonNull(gamename, "gamename cannot be null");
		Objects.requireNonNull(arch, "arch cannot be null");
		Objects.requireNonNull(os, "os cannot be null");
		Objects.requireNonNull(locale, "locale cannot be null");
		Objects.requireNonNull(i, "I cannot be null");
		if (gamename.length != 4) {
			throw new IllegalArgumentException("gamename must be length 4");
		}
		if (arch.length != 4) {
			throw new IllegalArgumentException("arch must be length 4");
		}
		if (os.length != 4) {
			throw new IllegalArgumentException("os must be length 4");
		}
		if (locale.length != 4) {
			throw new IllegalArgumentException("locale must be length 4");
		}
		if (i.length != iLength) {
			throw new IllegalArgumentException("i length must match iLength");
		}
		this.error = error;
		this.size = size;
		this.gamename = new String(gamename, 0, gamename.length - 1, StandardCharsets.US_ASCII);
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.patchVersion = patchVersion;
		this.build = build;
		this.arch = new String(arch, 0, arch.length - 1, StandardCharsets.US_ASCII);
		this.os = new String(os, 0, os.length - 1, StandardCharsets.US_ASCII);
		this.locale = new String(locale, StandardCharsets.US_ASCII);
		this.timezone = timezone;
		this.ip = ip;
		this.iLength = iLength;
		this.i = i;
	}

	public final byte getError() {
		return error;
	}

	public final short getSize() {
		return size;
	}

	public final String getGamename() {
		return gamename;
	}

	public final byte getMajorVersion() {
		return majorVersion;
	}

	public final byte getMinorVersion() {
		return minorVersion;
	}

	public final byte getPatchVersion() {
		return patchVersion;
	}

	public final short getBuild() {
		return build;
	}

	public final String getArch() {
		return arch;
	}

	public final String getOs() {
		return os;
	}

	public final String getLocale() {
		return locale;
	}

	public final int getTimezone() {
		return timezone;
	}

	public final int getIp() {
		return ip;
	}

	public final byte getILength() {
		return iLength;
	}

	public final byte[] getI() {
		return i;
	}
}