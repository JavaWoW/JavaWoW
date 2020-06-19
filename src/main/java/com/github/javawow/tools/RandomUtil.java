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

import java.security.SecureRandom;

/**
 * Provides cryptographically secure RNGs and PRNGs.
 * 
 * @author Jon Huang
 *
 */
public final class RandomUtil {
	private static final SecureRandom sr = new SecureRandom();

	private RandomUtil() {
		// static utility class
	}

	public static final SecureRandom getSecureRandom() {
		return sr;
	}
}