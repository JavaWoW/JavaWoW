package com.github.javawow.data.input;

import com.github.javawow.tools.HexTool;

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
	 * @see com.github.javawow.data.input.ByteInputStream#readByte()
	 */
	@Override
	public final int readByte() {
		bytesRead++;
		return bytes[pos++] & 0xFF;
	}

	/**
	 * @see com.github.javawow.data.input.ByteInputStream#getBytesRead()
	 */
	@Override
	public final int getBytesRead() {
		return bytesRead;
	}

	/**
	 * @see com.github.javawow.data.input.ByteInputStream#available()
	 */
	@Override
	public final int available() {
		return bytes.length - pos;
	}

	/**
	 * @see com.github.javawow.data.input.SeekableByteInputStream#seek(int)
	 */
	@Override
	public final void seek(int offset) {
		pos = offset;
	}

	/**
	 * @see com.github.javawow.data.input.SeekableByteInputStream#getPosition()
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