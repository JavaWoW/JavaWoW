package auth.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.mina.core.session.IoSession;
import org.bouncycastle.crypto.agreement.srp.SRP6Server;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.BitTools;
import util.RandomUtil;
import data.input.SeekableLittleEndianAccessor;

public final class ConnectHandler implements BasicHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectHandler.class);
	private static final BigInteger N = new BigInteger("894B645E89E1535BBDAD5B8B290650530801B18EBFBF5E8FAB3C82872A3E9BB7", 16);
	private static final BigInteger g = new BigInteger("7");
	private static final BigInteger k = new BigInteger("3");

	@Override
	public final void handlePacket(IoSession session, SeekableLittleEndianAccessor slea) {
		// Step 1 of SRP6: Client sends I (username) and A? (A = g^a)
		//LOGGER.info(slea.toString());
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
		String username = slea.readAsciiString(usernameLength);
		session.setAttribute("I", username);
		BigInteger s = RandomUtil.getRandomS();
		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError(e.getLocalizedMessage(), e);
		}
		String sha_hash = "5cd955b4d6d32a31ae4dfb0f03527125a77ac38f"; // lolwtf
		BigInteger p = new BigInteger(sha_hash, 16);
		sha1.update(BitTools.toLEByteArray(s, 32)); // feed the salt into the digest (s)
		sha1.update(BitTools.toLEByteArray(p, 20)); // feed the password hash into the digest (p)
		// SRP6 Parameters:
		// s = salt (random)
		// I = username
		// p = password hash (can we use BCrypt?)
		// v = g^x mod N
		// x = SHA1(s | p) [pipes denote concatenation]
		BigInteger x = BitTools.toBigInteger(sha1.digest(), true);
		BigInteger v = g.modPow(x, N); // x is the sha1 hash as a number
		SRP6Server srp = new SRP6Server();
		srp.init(N, g, v, new SHA1Digest(), RandomUtil.getSecureRandom());
		BigInteger B = srp.generateServerCredentials();
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
		baos.write(g.byteValue());
		baos.write(32);
		try {
			baos.write(BitTools.toLEByteArray(N, 32));
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		try {
			baos.write(BitTools.toLEByteArray(s, 32));
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		try {
			baos.write(new byte[16]);
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		baos.write(0);
		session.write(baos.toByteArray()); // accept the login
	}
}