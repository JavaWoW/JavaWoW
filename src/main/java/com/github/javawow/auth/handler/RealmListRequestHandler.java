package com.github.javawow.auth.handler;

import com.github.javawow.auth.AuthServer;
import com.github.javawow.data.input.SeekableLittleEndianAccessor;
import com.github.javawow.data.output.LittleEndianWriterStream;
import com.github.javawow.tools.BasicHandler;

import io.netty.channel.Channel;

public final class RealmListRequestHandler implements BasicHandler {
//	private static final Logger LOGGER = LoggerFactory.getLogger(RealmListRequestHandler.class);
	private static final RealmListRequestHandler INSTANCE = new RealmListRequestHandler();

	private RealmListRequestHandler() {
	}

	public static final RealmListRequestHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final boolean hasValidState(Channel channel) {
		return channel.attr(AuthServer.SRP_ATTR).get() != null;
	}

	@Override
	public final void handlePacket(Channel channel, SeekableLittleEndianAccessor slea) {
		LittleEndianWriterStream lews = new LittleEndianWriterStream();
		lews.write(0x10); // header
		lews.writeShort(77); // size of entire packet
		lews.writeInt(0); // ?
		lews.writeShort(2); // number of realms
		// realm #1
		lews.write(0); // server_type: 0 = NORMAL; 1 = PVP; 6 = RP; 8 = RPPVP
		lews.write(0); // locked
		lews.write(0x02); // flags
		lews.writeNullTerminatedAsciiString("Test Realm"); // realm name
		lews.writeNullTerminatedAsciiString("127.0.0.1:1338"); // ip:port
		lews.writeFloat(0.1f); // float I think....for population
		lews.write(0); // number of characters
		lews.write(1); // timezone
		lews.write(0x2C); // realm id?
		// realm #2
		lews.write(1); // server_type: 0 = NORMAL; 1 = PVP; 6 = RP; 8 = RPPVP
		lews.write(0); // locked (1 = locked, 0 = not locked)
		lews.write(0); // flags (0 = online, 0x02 = offline)
		lews.writeNullTerminatedAsciiString("JavaWoW");
		lews.writeNullTerminatedAsciiString("127.0.0.1:1337");
		lews.writeFloat(0.1f);
		lews.write(0);
		lews.write(1); // timezone
		lews.write(1);
		// end
		lews.writeShort(0x10);
		channel.write(lews.toByteArray());
	}
}