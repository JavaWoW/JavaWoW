package auth;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

final class AuthCodecFactory implements ProtocolCodecFactory {
	private static final AuthCodecFactory INSTANCE = new AuthCodecFactory();

	private AuthCodecFactory() {
	}

	public static final AuthCodecFactory getInstance() {
		return INSTANCE;
	}

	@Override
	public final ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return AuthDecoder.getInstance();
	}

	@Override
	public final ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return AuthEncoder.getInstance();
	}
}