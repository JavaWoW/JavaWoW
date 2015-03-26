package realm.handler;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.BasicHandler;
import data.input.SeekableLittleEndianAccessor;

public final class RealmVerifyHandler implements BasicHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealmVerifyHandler.class);
	private static final RealmVerifyHandler INSTANCE = new RealmVerifyHandler();

	private RealmVerifyHandler() {
	}

	public static final RealmVerifyHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final boolean hasValidState(IoSession session) {
		return true; // XXX Any other ways?
	}

	@Override
	public final void handlePacket(IoSession session, SeekableLittleEndianAccessor slea) {
		slea.readByte();
		slea.readByte();
		short buildNumber = slea.readShort(); // build number of the client
		slea.skip(6);
		String username = slea.readNullTerminatedAsciiString();
		System.out.println("Build Number: " + buildNumber);
		System.out.println("Username: " + username);
		LOGGER.info(slea.toString());
	}
}