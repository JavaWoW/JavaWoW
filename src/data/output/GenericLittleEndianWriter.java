package data.output;

import java.nio.charset.Charset;

/**
 * @author Jon
 *
 */
public class GenericLittleEndianWriter implements LittleEndianWriter {
	private static final Charset ASCII = Charset.forName("UTF-8");
	private ByteOutputStream bos;

	protected GenericLittleEndianWriter() {
	}

	public GenericLittleEndianWriter(ByteOutputStream bos) {
		this.bos = bos;
	}

	protected void setByteOutputStream(ByteOutputStream bos) {
		this.bos = bos;
	}

	@Override
	public final void writeZeroBytes(int i) {
		for (int x = 0; x < i; x++) {
			bos.writeByte((byte) 0);
		}
	}

	@Override
	public final void write(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			bos.writeByte(b[i]);
		}
	}

	@Override
	public final void write(byte b) {
		bos.writeByte(b);
	}

	@Override
	public final void write(int b) {
		bos.writeByte((byte) b);
	}

	@Override
	public final void writeShort(int i) {
		bos.writeByte((byte) (i & 0xFF));
		bos.writeByte((byte) ((i >>> 8) & 0xFF));
	}

	@Override
	public final void writeInt(int i) {
		bos.writeByte((byte) (i & 0xFF));
		bos.writeByte((byte) ((i >>> 8) & 0xFF));
		bos.writeByte((byte) ((i >>> 16) & 0xFF));
		bos.writeByte((byte) ((i >>> 24) & 0xFF));
	}

	@Override
	public final void writeInt(long i) {
		bos.writeByte((byte) (i & 0xFF));
		bos.writeByte((byte) ((i >>> 8) & 0xFF));
		bos.writeByte((byte) ((i >>> 16) & 0xFF));
		bos.writeByte((byte) ((i >>> 24) & 0xFF));
	}

	@Override
	public final void writeAsciiString(String s) {
		this.write(s.getBytes(ASCII));
	}

	@Override
	public final void writeAsciiString(String s, int max) {
		this.write(s.getBytes(ASCII));
		for (int i = s.length(); i < max; i++) {
			this.write(0);
		}
	}

	@Override
	public final void writeNullTerminatedAsciiString(String s) {
		this.writeAsciiString(s);
		this.write(0);
	}

	@Override
	public final void writeLong(long l) {
		bos.writeByte((byte) (l & 0xFF));
		bos.writeByte((byte) ((l >>> 8) & 0xFF));
		bos.writeByte((byte) ((l >>> 16) & 0xFF));
		bos.writeByte((byte) ((l >>> 24) & 0xFF));
		bos.writeByte((byte) ((l >>> 32) & 0xFF));
		bos.writeByte((byte) ((l >>> 40) & 0xFF));
		bos.writeByte((byte) ((l >>> 48) & 0xFF));
		bos.writeByte((byte) ((l >>> 56) & 0xFF));
	}

	@Override
	public final void writeFloat(float f) {
		int i = Float.floatToIntBits(f);
		this.writeInt(i);
	}

	@Override
	public final void writeBEFloat(float f) {
		int i = Float.floatToIntBits(f);
		bos.writeByte((byte) ((i >>> 24) & 0xFF));
		bos.writeByte((byte) ((i >>> 16) & 0xFF));
		bos.writeByte((byte) ((i >>> 8) & 0xFF));
		bos.writeByte((byte) (i & 0xFF));
	}

	@Override
	public final void writeDouble(double d) {
		long l = Double.doubleToLongBits(d);
		this.writeLong(l);
	}

	@Override
	public final void writeBEDouble(double d) {
		long l = Double.doubleToLongBits(d);
		bos.writeByte((byte) ((l >>> 56) & 0xFF));
		bos.writeByte((byte) ((l >>> 48) & 0xFF));
		bos.writeByte((byte) ((l >>> 40) & 0xFF));
		bos.writeByte((byte) ((l >>> 32) & 0xFF));
		bos.writeByte((byte) ((l >>> 24) & 0xFF));
		bos.writeByte((byte) ((l >>> 16) & 0xFF));
		bos.writeByte((byte) ((l >>> 8) & 0xFF));
		bos.writeByte((byte) (l & 0xFF));
	}
}