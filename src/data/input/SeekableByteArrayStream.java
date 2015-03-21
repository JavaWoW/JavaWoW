package data.input;

import data.HexTool;

/**
 * @author Jon
 *
 */
public final class SeekableByteArrayStream implements SeekableByteInputStream {
	private byte[] bytes;
	private int pos = 0;
	private int bytesRead = 0;

	public SeekableByteArrayStream(byte[] bytes) {
		this.bytes = bytes.clone(); // defensive copy
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.ByteInputStream#readByte()
	 */
	@Override
	public final int readByte() {
		bytesRead++;
		return bytes[pos++] & 0xFF;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.ByteInputStream#getBytesRead()
	 */
	@Override
	public final int getBytesRead() {
		return bytesRead;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.ByteInputStream#available()
	 */
	@Override
	public final int available() {
		return bytes.length - pos;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.SeekableInputStreamByteStream#seek(long)
	 */
	@Override
	public final void seek(int offset) {
		pos = offset;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.SeekableInputStreamByteStream#getPosition()
	 */
	@Override
	public final int getPosition() {
		return pos;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		String nows = "";
		if (bytes.length - pos > 0) {
			byte[] now = new byte[bytes.length - pos];
			System.arraycopy(bytes, pos, now, 0, bytes.length - pos);
			nows = HexTool.toString(now);
		}
		return "All: " + HexTool.toString(bytes) + "\nNow: " + nows;
	}
}