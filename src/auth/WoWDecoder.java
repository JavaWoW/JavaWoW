package auth;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public final class WoWDecoder extends CumulativeProtocolDecoder {
	private static final WoWDecoder INSTANCE = new WoWDecoder();

	private WoWDecoder() {
	}

	public static final WoWDecoder getInstance() {
		return INSTANCE;
	}

	@Override
	protected final boolean doDecode(IoSession session, IoBuffer buf, ProtocolDecoderOutput out) throws Exception {
		/*byte cmd = buf.get();
		byte error = buf.get();
		byte b1 = buf.get();
		byte b2 = buf.get();
		short packetLength = (short) (b2 & 0xFF << 8 | b1 & 0xFF);
		int length = buf.remaining();
		if (packetLength >= length) {
			byte[] in = new byte[packetLength + 4];
			in[0] = cmd;
			in[1] = error;
			in[2] = b1;
			in[3] = b2;
			buf.get(in, 4, packetLength);
			out.write(in);
			return true;
		}
		return false;*/
		int length = buf.remaining();
		byte[] input = new byte[length];
		buf.get(input, 0, length);
		out.write(input);
		return true;
	}
}