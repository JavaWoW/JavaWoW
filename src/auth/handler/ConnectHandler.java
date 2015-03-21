package auth.handler;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.input.SeekableLittleEndianAccessor;

public final class ConnectHandler implements BasicHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectHandler.class);

	@Override
	public final void handlePacket(IoSession session, SeekableLittleEndianAccessor slea) {
		LOGGER.info(slea.toString());
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
		session.write(new byte[] {0, 0, 3});
	}
}