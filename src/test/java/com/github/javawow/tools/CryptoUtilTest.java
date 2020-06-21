package com.github.javawow.tools;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class CryptoUtilTest {
	@Test
	public void testEncryptHMacSHA1() {
		byte[] key = new byte[40];
		for (int i = 0; i < key.length; i++) {
			key[i] = (byte) i;
		}
		byte[] output = CryptoUtil.encryptHMacSHA1(key);
		byte[] expected = HexTool.parseHexString("316ECB675E326F11C2F47035F756A79263817B4A");
		assertArrayEquals(expected, output);
	}

	@Test
	public void testDecryptHMacSHA1() {
		byte[] key = new byte[40];
		for (int i = 0; i < key.length; i++) {
			key[i] = (byte) i;
		}
		byte[] output = CryptoUtil.decryptHMacSHA1(key);
		byte[] expected = HexTool.parseHexString("719A8BF129DBC2924479B66C84173A29EF9BD343");
		assertArrayEquals(expected, output);
	}
}