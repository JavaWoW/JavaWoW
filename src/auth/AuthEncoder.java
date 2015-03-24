package auth;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class AuthEncoder implements ProtocolEncoder {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthEncoder.class);
	private static final AuthEncoder INSTANCE = new AuthEncoder();

	private AuthEncoder() {
	}

	public static final AuthEncoder getInstance() {
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