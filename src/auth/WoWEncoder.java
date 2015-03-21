package auth;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WoWEncoder implements ProtocolEncoder {
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
		} else {
			LOGGER.warn("Unrecognized Object: {}", msg.getClass().getName());
		}
	}
}