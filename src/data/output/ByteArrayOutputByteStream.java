package data.output;

import java.io.ByteArrayOutputStream;

/**
 * @author Jon
 *
 */
public final class ByteArrayOutputByteStream implements ByteOutputStream {
	private ByteArrayOutputStream baos;

	public ByteArrayOutputByteStream(ByteArrayOutputStream baos) {
		this.baos = baos;
	}

	@Override
	public final void writeByte(byte b) {
		baos.write(b);
	}
}