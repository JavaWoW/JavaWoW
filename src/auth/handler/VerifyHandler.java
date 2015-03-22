package auth.handler;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.input.SeekableLittleEndianAccessor;

public final class VerifyHandler implements BasicHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyHandler.class);

	// Handshaking portion
	@Override
	public final void handlePacket(IoSession session, SeekableLittleEndianAccessor slea) {
		// Step 2 of SRP6: We send s and B (B = kv + g^b)
		LOGGER.info(slea.toString());
		// 01 09 40 8D 8D F7 A1 D2 29 A1 BC 1F 60 30 2E 63 86 B3 07 A5 BF 0F 02 B5 5A CB CF C8 CF EF 17 0D 36 F1 45 F8 44 1B 72 D0 B7 0A E8 B2 04 78 B3 9E 63 77 C5 7F CE D7 1A FB 94 F3 0A C9 A3 CC FD 94 AF 05 1F A3 91 7A 2D A5 AB 00 00
	}
}