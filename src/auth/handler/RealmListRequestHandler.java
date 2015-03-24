package auth.handler;

import org.apache.mina.core.session.IoSession;

import data.input.SeekableLittleEndianAccessor;
import data.output.LittleEndianWriterStream;

public final class RealmListRequestHandler implements BasicHandler {
//	private static final Logger LOGGER = LoggerFactory.getLogger(RealmListRequestHandler.class);

	@Override
	public final boolean hasValidState(IoSession session) {
		return session.containsAttribute("srp");
	}

	@Override
	public final void handlePacket(IoSession session, SeekableLittleEndianAccessor slea) {
		LittleEndianWriterStream lews = new LittleEndianWriterStream();
		lews.write(0x10); // header
		lews.writeShort(77); // size of entire packet
		lews.writeInt(0); // ?
		lews.writeShort(2); // number of realms (byte or short?)
		// realm #1
		lews.write(0); // icon
		lews.write(0); // locked
		lews.write(0x02); // flags
		lews.writeNullTerminatedAsciiString("Test Realm"); // realm name
		lews.writeNullTerminatedAsciiString("127.0.0.1:8087"); // ip:port
		lews.writeInt(0); // float I think....for population
		lews.write(0); // number of characters
		lews.write(1); // timezone
		lews.write(0x2C); // realm id?
		// realm #2
		lews.write(1); // icon
		lews.write(0); // locked
		lews.write(0); // flags
		lews.writeNullTerminatedAsciiString("JavaWoW");
		lews.writeNullTerminatedAsciiString("127.0.0.1:1119");
		lews.writeInt(0);
		lews.write(0);
		lews.write(1); // timezone
		lews.write(1);
		// end
		lews.write(0x10);
		lews.write(0);
		session.write(lews.toByteArray());
//		LOGGER.info("Packet Sent.");
	}
}