package data.output;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Jon
 *
 */
public final class IoBufferOutputStream implements ByteOutputStream {
	private IoBuffer ib;

	public IoBufferOutputStream(IoBuffer ib) {
		this.ib = ib;
	}

	@Override
	public final void writeByte(byte b) {
		ib.put(b);
	}
}