package data.output;

import java.io.ByteArrayOutputStream;

import data.HexTool;

/**
 * @author Jon
 *
 */
public final class MaplePacketLittleEndianWriter extends GenericLittleEndianWriter {
	private ByteArrayOutputStream baos;

	public MaplePacketLittleEndianWriter() {
		this(32);
	}

	public MaplePacketLittleEndianWriter(int size) {
		this.baos = new ByteArrayOutputStream(size);
		setByteOutputStream(new ByteArrayOutputByteStream(baos));
	}

	public final byte[] getPacket() {
		return baos.toByteArray();
	}

	@Override
	public final String toString() {
		return HexTool.toString(baos.toByteArray());
	}
}