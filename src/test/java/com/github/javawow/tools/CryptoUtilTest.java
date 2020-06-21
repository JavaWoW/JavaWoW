package com.github.javawow.tools;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

import org.bouncycastle.util.Arrays;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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

	@Test
	public void testEncrypt() {
		BigInteger K = new BigInteger(
				"8f87ab94d7095173b37fca628e4ccdca3014dfea8713e37a9bf892b3d71bbf6e49a187e4c35b85b2", 16);
		Cipher ciph = CryptoUtil.createRC4Cipher(true, K);
		ByteBuf input = Unpooled.buffer(4, 5);
		ByteBuf output = Unpooled.buffer(4, 5);
		input.writeShort(3);
		input.writeShortLE(0x1EE);
		try {
			int updateLen = ciph.update(input.array(), input.arrayOffset(), input.readableBytes(), output.array(),
					output.arrayOffset());
			assertEquals(4, updateLen); // we expect 4 bytes to be processed
			output.writerIndex(updateLen);
			byte[] expectedOutput = { (byte) 0xD7, (byte) 0xBF, (byte) 0x93, 0x43 };
			byte[] outputArr = Arrays.copyOfRange(output.array(), output.arrayOffset(), output.arrayOffset() + output.readableBytes());
			assertArrayEquals(expectedOutput, outputArr);
		} catch (ShortBufferException e) {
			throw new AssertionError(e);
		}
	}
}