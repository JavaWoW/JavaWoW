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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.bouncycastle.util.BigIntegers;

/**
 * Provides utilities for accessing the filesystem.
 * 
 * @author Jon Huang
 *
 */
public final class FileUtil {
	private FileUtil() {
		// static utility class
	}

	/**
	 * Fetches the session key from a temporary file (for testing only).
	 * 
	 * @throws IOException If an error occurs while accessing the file
	 */
	public static final BigInteger getSessionKey() throws IOException {
		try (FileInputStream fis = new FileInputStream("session_key.tmp");
				BufferedInputStream bis = new BufferedInputStream(fis);) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = bis.read(buf, 0, buf.length)) > 0) {
				baos.write(buf, 0, bytesRead);
			}
			BigInteger K = new BigInteger(1, baos.toByteArray());
			return K;
		}
	}

	/**
	 * Saves the session key to a temporary file (for testing only).
	 * 
	 * @param K The session key to save
	 * @throws IOException If an error occurs while saving the file
	 */
	public static final void saveSessionKey(BigInteger K) throws IOException {
		try (FileOutputStream fos = new FileOutputStream("session_key.tmp");) {
			fos.write(BigIntegers.asUnsignedByteArray(K));
			fos.flush();
			fos.getFD().sync();
		}
	}

	/**
	 * Saves the verifier and salt to a temporary file (for testing only).
	 * 
	 * @param v The verifier to save
	 * @param s The salt to save
	 * @throws IOException If an error occurs while saving the files
	 */
	public static final void saveVS(BigInteger v, byte[] s) throws IOException {
		try (FileOutputStream fos = new FileOutputStream("v.tmp");) {
			fos.write(BigIntegers.asUnsignedByteArray(v));
			fos.flush();
			fos.getFD().sync();
		}
		try (FileOutputStream fos = new FileOutputStream("s.tmp");) {
			fos.write(s);
			fos.flush();
			fos.getFD().sync();
		}
	}
}