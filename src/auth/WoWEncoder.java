package auth;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.output.Packet;

public final class WoWEncoder implements ProtocolEncoder {
	private static final Logger LOGGER = LoggerFactory.getLogger(WoWEncoder.class);
	private static final WoWEncoder INSTANCE = new WoWEncoder();

	private WoWEncoder() {
	}

	public static final WoWEncoder getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void encode(IoSession message, Object msg, ProtocolEncoderOutput out) throws Exception {
		if (msg instanceof byte[]) {
			out.write(IoBuffer.wrap((byte[]) msg));
		} else if (msg instanceof Packet) {
			Packet p = (Packet) msg;
			byte[] raw = p.toByteArray();
			byte[] output = new byte[raw.length + 3];
			output[0] = p.getOpCode();
			output[1] = (byte) (raw.length & 0xFF00);
			output[2] = (byte) (raw.length & 0xFF);
			System.arraycopy(raw, 0, output, 3, raw.length);
		} else {
			LOGGER.warn("Unrecognized Object: {}", msg.getClass().getName());
		}
	}
}