package realm;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

final class RealmDecoder extends CumulativeProtocolDecoder {
	private static final RealmDecoder INSTANCE = new RealmDecoder();

	private RealmDecoder() {
	}

	public static final RealmDecoder getInstance() {
		return INSTANCE;
	}

	private static final class DecoderState {
		private int packetLength = -1;
	}

	@Override
	protected final boolean doDecode(IoSession session, IoBuffer buf, ProtocolDecoderOutput out) throws Exception {
		DecoderState decoderState = (DecoderState) session.getAttribute(DecoderState.class.getName());
		if (decoderState == null) {
			decoderState = new DecoderState();
			session.setAttribute(DecoderState.class.getName(), decoderState);
		}
		if (buf.remaining() >= 2 && decoderState.packetLength == -1) {
			// we received 
			int packetLength = (buf.getShort() & 0xFFFF);
			decoderState.packetLength = packetLength;
		} else if (buf.remaining() < 2 && decoderState.packetLength == -1) {
			// not enough data to decode
			return false;
		}
		if (buf.remaining() >= decoderState.packetLength) {
			byte[] in = new byte[decoderState.packetLength];
			buf.get(in, 0, decoderState.packetLength);
			decoderState.packetLength = -1;
			out.write(in);
			return true;
		}
		return false;
	}
}