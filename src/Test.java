
public final class Test {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte b1 = 0x25;
		byte b2 = 0;
		int i = (b2 & 0xFF << 8 | b1 & 0xFF);
		System.out.println(i);
	}
}