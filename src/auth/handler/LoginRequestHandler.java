package auth.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import org.apache.mina.core.session.IoSession;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.SRP6GroupParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.BitTools;
import tools.RandomUtil;
import tools.srp.WoWSRP6Server;
import tools.srp.WoWSRP6VerifierGenerator;
import data.input.SeekableLittleEndianAccessor;

public class LoginRequestHandler implements BasicHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginRequestHandler.class);
	private static final BigInteger N = new BigInteger("894B645E89E1535BBDAD5B8B290650530801B18EBFBF5E8FAB3C82872A3E9BB7", 16);
	private static final BigInteger g = BigInteger.valueOf(7);
	private static final SRP6GroupParameters params = new SRP6GroupParameters(N, g);

	@Override
	public final void handlePacket(IoSession session, SeekableLittleEndianAccessor slea) {
		slea.readByte();
		slea.readLENullTerminatedAsciiString(); // WoW
		slea.readByte(); // major version (3)
		slea.readByte(); // minor version (3)
		slea.readByte(); // patch version (5)
		slea.readShort(); // build number (12340)
		String processor = slea.readLENullTerminatedAsciiString();
		String os = slea.readLENullTerminatedAsciiString();
		String lang = slea.readLEAsciiString(4);
		LOGGER.info("Info. Proc: {} OS: {} Lang: {}", processor, os, lang);
		slea.readInt(); // ?
		slea.readInt(); // ip
		int usernameLength = slea.readByte();
		Random r = new Random(1337); // XXX Debug only
		// Step 1 : Client sends us I
		byte[] I = slea.read(usernameLength); // username
		// Step 2 : Server responds with B, g, N, s
		byte[] p = "lolwtf".toUpperCase().getBytes(); // p (kept secret throughout)
		byte[] s = new byte[32]; // salt (s)
		r.nextBytes(s);
		WoWSRP6VerifierGenerator gen = new WoWSRP6VerifierGenerator();
		gen.init(params, new SHA1Digest());
		BigInteger v = gen.generateVerifier(s, I, p); // generate v
		WoWSRP6Server srp = WoWSRP6Server.init(params, v, I, s, new SHA1Digest(), RandomUtil.getSecureRandom());
		BigInteger B = srp.generateServerCredentials(); // generate B
		// Set client values
		session.setAttribute("srp", srp);
		// Now we have B, g, N, s (send it here)
		// Begin Packet Response:
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(0); // AUTH_LOGON_CHALLENGE
		baos.write(0); // ?
		baos.write(0); // WOW_SUCCESS
		try {
			baos.write(BitTools.toLEByteArray(B, 32));
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		baos.write(1);
		try {
			baos.write(g.toByteArray());
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		byte[] N_bytes = BitTools.toLEByteArray(N, 32);
		baos.write(N_bytes.length);
		try {
			baos.write(N_bytes);
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
//		LOGGER.info("N Length: {}", N_bytes.length);
		try {
			baos.write(s);
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		try {
			baos.write(new byte[16]);
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		baos.write(0);
		session.write(baos.toByteArray()); // send the packet
		LOGGER.info("Packet Sent.");
	}
}