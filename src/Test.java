import java.math.BigInteger;
import java.util.Arrays;

import util.RandomUtil;

public final class Test {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigInteger bi = RandomUtil.getRandomS();
		byte[] s_ = bi.toByteArray();
		byte[] s_le = new byte[s_.length - (s_.length % 2)];
		System.out.println(Arrays.toString(s_));
		System.out.println("Length: " + s_.length);
		for (int i = (s_.length - 1), c = 0; i >= s_.length % 2; i--) {
			s_le[c++] = s_[i];
		}
		System.out.println(Arrays.toString(s_le));
	}
}