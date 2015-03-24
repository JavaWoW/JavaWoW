package realm;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class RealmEncoder implements ProtocolEncoder {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealmEncoder.class);
	private static final RealmEncoder INSTANCE = new RealmEncoder();

	private RealmEncoder() {
	}

	public static final RealmEncoder getInstance() {
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