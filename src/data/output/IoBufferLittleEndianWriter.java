package data.output;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Jon
 *
 */
public final class IoBufferLittleEndianWriter extends GenericLittleEndianWriter {
	private IoBuffer ib;

	public IoBufferLittleEndianWriter() {
		this(50, true);
	}

	public IoBufferLittleEndianWriter(int size) {
		this(size, false);
	}

	public IoBufferLittleEndianWriter(int initialSize, boolean autoExpand) {
		ib = IoBuffer.allocate(initialSize);
		ib.setAutoExpand(autoExpand);
		super.setByteOutputStream(new IoBufferOutputStream(ib));
	}

	public final IoBuffer getFlippedIB() {
		return ib.flip();
	}

	public final IoBuffer getByteBuffer() {
		return ib;
	}
}