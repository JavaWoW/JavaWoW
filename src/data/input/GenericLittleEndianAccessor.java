package data.input;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

/**
 * @author Jon
 *
 */
public class GenericLittleEndianAccessor implements LittleEndianAccessor {
	private final ByteInputStream bis;

	public GenericLittleEndianAccessor(ByteInputStream bis) {
		this.bis = bis;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readByte()
	 */
	@Override
	public final byte readByte() {
		return (byte) bis.readByte();
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readUnsignedByte()
	 */
	@Override
	public short readUnsignedByte() {
		return (short) (readByte() & 0xFF);
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readChar()
	 */
	@Override
	public final char readChar() {
		return (char) readShort();
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readShort()
	 */
	@Override
	public final short readShort() {
		int byte1 = bis.readByte();
		int byte2 = bis.readByte();
		return (short) ((byte2 << 8) + byte1);
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readUnsignedShort()
	 */
	@Override
	public int readUnsignedShort() {
		return readShort() & 0xFFFF;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readInt()
	 */
	@Override
	public final int readInt() {
		int byte1 = bis.readByte();
		int byte2 = bis.readByte();
		int byte3 = bis.readByte();
		int byte4 = bis.readByte();
		return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readUnsignedInt()
	 */
	@Override
	public final long readUnsignedInt() {
		return readInt() & 0xFFFFFFFFL;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readLong()
	 */
	@Override
	public final long readLong() {
		long byte1 = bis.readByte();
		long byte2 = bis.readByte();
		long byte3 = bis.readByte();
		long byte4 = bis.readByte();
		long byte5 = bis.readByte();
		long byte6 = bis.readByte();
		long byte7 = bis.readByte();
		long byte8 = bis.readByte();
		return (byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readUnsignedLong()
	 */
	@Override
	public BigInteger readUnsignedLong() {
		BigInteger mask = new BigInteger("18446744073709551615"); // 0xFFFFFFFFFFFFFFFFL
		return mask.and(BigInteger.valueOf(readLong()));
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#skip(int)
	 */
	@Override
	public void skip(int num) {
		for (int x = 0; x < num; x++) {
			readByte();
		}
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#read(int)
	 */
	@Override
	public final byte[] read(int num) {
		byte[] ret = new byte[num];
		for (int x = 0; x < num; x++) {
			ret[x] = readByte();
		}
		return ret;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readFloat()
	 */
	@Override
	public final float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readDouble()
	 */
	@Override
	public final double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readAsciiString(int)
	 */
	@Override
	public final String readAsciiString(int n) {
		char ret[] = new char[n];
		for (int x = 0; x < n; x++) {
			ret[x] = (char) readByte();
		}
		return new String(ret);
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readAsciiString(int)
	 */
	@Override
	public final String readLEAsciiString(int length) {
		char ret[] = new char[length];
		for (int x = 0, n = length - 1; x < length; x++) {
			ret[n - x] = (char) readByte();
		}
		return new String(ret);
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readNullTerminatedAsciiString()
	 */
	@Override
	public final String readNullTerminatedAsciiString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte b;
		while ((b = readByte()) != 0) {
			baos.write(b);
		}
		byte[] buf = baos.toByteArray();
		char[] chrBuf = new char[buf.length];
		for (int x = 0; x < buf.length; x++) {
			chrBuf[x] = (char) buf[x];
		}
		return new String(chrBuf);
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readNullTerminatedAsciiString()
	 */
	@Override
	public final String readLENullTerminatedAsciiString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte b;
		while ((b = readByte()) != 0) {
			baos.write(b);
		}
		byte[] buf = baos.toByteArray();
		char[] chrBuf = new char[buf.length];
		for (int x = 0, n = buf.length - 1; x < buf.length; x++) {
			chrBuf[n - x] = (char) buf[x];
		}
		return new String(chrBuf);
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readMapleAsciiString()
	 */
	@Override
	public final String readMapleAsciiString() {
		return readAsciiString(readShort());
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#readPos()
	 */
	@Override
	public final Point readPos() {
		int x = readShort();
		int y = readShort();
		return new Point(x, y);
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#getBytesRead()
	 */
	@Override
	public final int getBytesRead() {
		return bis.getBytesRead();
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.LittleEndianAccessor#available()
	 */
	@Override
	public final int available() {
		return bis.available();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return bis.toString();
	}
}