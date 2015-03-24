package realm;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public final class RealmCodecFactory implements ProtocolCodecFactory {
	private static final RealmCodecFactory INSTANCE = new RealmCodecFactory();

	private RealmCodecFactory() {
	}

	public static final RealmCodecFactory getInstance() {
		return INSTANCE;
	}

	@Override
	public final ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return RealmDecoder.getInstance();
	}

	@Override
	public final ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return RealmEncoder.getInstance();
	}
}