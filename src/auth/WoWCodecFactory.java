package auth;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public final class WoWCodecFactory implements ProtocolCodecFactory {
	private static final WoWCodecFactory INSTANCE = new WoWCodecFactory();

	private WoWCodecFactory() {
	}

	public static final WoWCodecFactory getInstance() {
		return INSTANCE;
	}

	@Override
	public final ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return WoWDecoder.getInstance();
	}

	@Override
	public final ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return WoWEncoder.getInstance();
	}
}