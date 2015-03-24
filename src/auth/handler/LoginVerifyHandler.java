package auth.handler;

import java.math.BigInteger;

import org.apache.mina.core.session.IoSession;
import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.BitTools;
import data.input.SeekableLittleEndianAccessor;

public class LoginVerifyHandler implements BasicHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginVerifyHandler.class);

	@Override
	public void handlePacket(IoSession session, SeekableLittleEndianAccessor slea) {
		byte[] A_bytes = slea.read(32); // Little-endian format
		byte[] M1_bytes = slea.read(20);
		byte[] crc_hash_bytes = slea.read(20);
		slea.readByte();
		slea.readByte();
		WoWSRP6Server srp = (WoWSRP6Server) session.getAttribute("srp");
		if (srp == null) {
			LOGGER.warn("srp not set!");
		}
		BigInteger A = BitTools.toBigInteger(A_bytes, true);
		BigInteger S;
		try {
			S = srp.calculateSecret(A);
		} catch (CryptoException e) {
			throw new InternalError(e.getLocalizedMessage(), e);
		}
		BigInteger M1 = BitTools.toBigInteger(M1_bytes, false);
		boolean M1_match;
		try {
			M1_match = srp.verifyClientEvidenceMessage(M1);
		} catch (CryptoException e) {
			throw new InternalError(e.getLocalizedMessage(), e);
		}
		LOGGER.info("BC M match: {}", M1_match);
	}
}