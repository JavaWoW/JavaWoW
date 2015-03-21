package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public final class SHA1Hasher {

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		String digested = byteToHex(sha1.digest("lolwtf".getBytes()));
		System.out.println(digested);
	}

	private static String byteToHex(final byte[] hash) {
		try (Formatter formatter = new Formatter();) {
			for (byte b : hash) {
				formatter.format("%02x", b);
			}
			String result = formatter.toString();
			return result;
		}
	}
}