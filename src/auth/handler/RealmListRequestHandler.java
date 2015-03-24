package auth.handler;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.input.SeekableLittleEndianAccessor;
import data.output.Packet;

public final class RealmListRequestHandler implements BasicHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealmListRequestHandler.class);

	@Override
	public final boolean hasValidState(IoSession session) {
		return session.containsAttribute("srp");
	}

	@Override
	public final void handlePacket(IoSession session, SeekableLittleEndianAccessor slea) {
		// FIXME Realm packet is not working
		Packet p = new Packet((byte) 0x10);
		//lews.write(0x10); // header
		//lews.writeShort(); // size of packet
		p.writeInt(0); // ?
		p.writeShort(1); // number of realms
		// for each realm
		p.write(0x2A); // icon
		p.write(0); // locked
		p.write(0); // color
		p.writeNullTerminatedAsciiString("RealmNameTest"); // realm name
		p.writeNullTerminatedAsciiString("localhost:1337"); // ip:port
		p.writeInt(0); // float I think....for population
		p.writeFloat(0); // number of characters
		p.write(0); // timezone
		p.write(0); // ?
		// end
		session.write(p);
		LOGGER.info("Packet Sent.");
	}
}