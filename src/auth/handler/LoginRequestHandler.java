package auth.handler;

import java.math.BigInteger;

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
import data.output.LittleEndianWriterStream;

public final class LoginRequestHandler implements BasicHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginRequestHandler.class);
	private static final BigInteger N = new BigInteger("894B645E89E1535BBDAD5B8B290650530801B18EBFBF5E8FAB3C82872A3E9BB7", 16);
	private static final BigInteger g = BigInteger.valueOf(7);
	private static final SRP6GroupParameters params = new SRP6GroupParameters(N, g);

	@Override
	public final boolean hasValidState(IoSession session) {
		return !session.containsAttribute("srp"); // only if srp is not set
	}

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
		// Step 1 : Client sends us I
		byte[] I = slea.read(usernameLength); // username
		// Step 2 : Server responds with B, g, N, s
		byte[] p = "lolwtf".toUpperCase().getBytes(); // p (kept secret throughout)
		byte[] s = new byte[32]; // salt (s)
		RandomUtil.getSecureRandom().nextBytes(s);
		WoWSRP6VerifierGenerator gen = new WoWSRP6VerifierGenerator();
		gen.init(params, new SHA1Digest());
		BigInteger v = gen.generateVerifier(s, I, p); // generate v
		WoWSRP6Server srp = WoWSRP6Server.init(params, v, I, s, new SHA1Digest(), RandomUtil.getSecureRandom());
		BigInteger B = srp.generateServerCredentials(); // generate B
		// Set client values
		session.setAttribute("srp", srp);
		// Now we have B, g, N, s (send it here)
		// Begin Packet Response:
		LittleEndianWriterStream lews = new LittleEndianWriterStream();
		lews.write(0); // AUTH_LOGON_CHALLENGE
		lews.write(0); // ?
		lews.write(0); // WOW_SUCCESS
		lews.write(BitTools.toLEByteArray(B, 32));
		lews.write(1);
		lews.write(g.toByteArray());
		byte[] N_bytes = BitTools.toLEByteArray(N, 32);
		lews.write(N_bytes.length);
		lews.write(N_bytes);
		lews.write(s);
		lews.write(new byte[16]);
		lews.write(0);
		session.write(lews.toByteArray()); // send the packet
		LOGGER.info("Packet Sent.");
	}
}