package auth.handler;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.input.SeekableLittleEndianAccessor;

public final class VerifyHandler implements BasicHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyHandler.class);

	@Override
	public final void handlePacket(IoSession session, SeekableLittleEndianAccessor slea) {
		// Step 2 of SRP6: We send s and B (B = kv + g^b)
		LOGGER.info(slea.toString());
		
	}
}