package realm.handler;

import java.util.Arrays;

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
		slea.skip(2); // ?
		int buildNumber = slea.readInt();
		int serverId = slea.readInt(); // server Id
		String username = slea.readNullTerminatedAsciiString();
		int loginServerType = slea.readInt(); // login server type
		int clientSeed = slea.readInt(); // client seed
		int region = slea.readInt(); // region
		int battleGroup = slea.readInt(); // battle group
		int realmIndex = slea.readInt(); // realm index
		long unk4 = slea.readLong(); // unk4
		byte[] digest = slea.read(20);
		System.out.println("Build Number: " + buildNumber);
		System.out.println("Server ID: " + serverId);
		System.out.println("Username: " + username);
		System.out.println("Login Server Type: " + loginServerType);
		System.out.println("Client Seed: " + clientSeed);
		System.out.println("Region: " + region);
		System.out.println("Battle Group: " + battleGroup);
		System.out.println("Realm Index: " + realmIndex);
		System.out.println("Unk4: " + unk4);
		System.out.println("Digest: " + Arrays.toString(digest));
		LOGGER.info(slea.toString());
	}
}