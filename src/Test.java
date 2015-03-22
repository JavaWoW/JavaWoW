import java.math.BigInteger;
import java.util.Arrays;

import util.BitTools;

public final class Test {
	public static void main(String[] args) {
		/*BigInteger bi = RandomUtil.getRandomS();
		byte[] s_ = bi.toByteArray();
		byte[] s_le = new byte[s_.length - (s_.length % 2)];
		System.out.println(Arrays.toString(s_));
		System.out.println("Length: " + s_.length);
		for (int i = (s_.length - 1), c = 0; i >= s_.length % 2; i--) {
			s_le[c++] = s_[i];
		}
		System.out.println(Arrays.toString(s_le));*/
		String sha_hash = "5cd955b4d6d32a31ae4dfb0f03527125a77ac38f"; // lolwtf
		BigInteger p = new BigInteger(sha_hash, 16);
		byte[] p_ = p.toByteArray();
		byte[] p_le = new byte[p_.length - (p_.length % 2)];
		System.out.println("Length: " + p_.length);
		System.out.println(Arrays.toString(p_));
		for (int i = (p_.length - 1), c = 0; i >= p_.length % 2; i--) {
			p_le[c++] = p_[i];
		}
		System.out.println(Arrays.toString(p_le));
		System.out.println(Arrays.toString(BitTools.toLEByteArray(p, 20)));
	}
}