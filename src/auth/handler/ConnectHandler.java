package auth.handler;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

import org.apache.mina.core.session.IoSession;
import org.bouncycastle.crypto.agreement.srp.SRP6Server;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		
			sha1 = MessageDigest.getInstance("SHA-1");
		
		BigInteger bi = RandomUtil.getRandomS();
		byte[] s_ = bi.toByteArray();
		byte[] s_le = new byte[s_.length - (s_.length % 2)]; // little endian format of s_ (reversed)
		for (int i = (s_.length - 1), c = 0; i >= s_.length % 2; i--) {
			s_le[c++] = s_[i];
		}
		sha1.update(s_le); // feed the salt into the digest
		
		
		BigInteger v = g.modPow(x, N); // x is the sha1 hash as a number
		SRP6Server srp = new SRP6Server();
		srp.init(N, g, v, new SHA1Digest(), RandomUtil.getSecureRandom());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(0); // AUTH_LOGON_CHALLENGE
		baos.write(0); // ?
		baos.write(0); // WOW_SUCCESS
		// TODO Write more shit
		session.write(new byte[] {0, 0, 0}); // accept the login
	}
}