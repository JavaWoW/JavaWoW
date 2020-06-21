/*
 * Java World of Warcraft Emulation Project
 * Copyright (C) 2015-2020 JavaWoW
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.javawow.auth.handler;

import com.github.javawow.auth.AuthServer;
import com.github.javawow.auth.message.RealmlistRequestMessage;
import com.github.javawow.data.output.LittleEndianWriterStream;

import io.netty.channel.Channel;

public final class RealmListRequestHandler implements BasicAuthHandler<RealmlistRequestMessage> {
//	private static final Logger LOGGER = LoggerFactory.getLogger(RealmListRequestHandler.class);
	private static final RealmListRequestHandler INSTANCE = new RealmListRequestHandler();

	private RealmListRequestHandler() {
		// singleton
	}

	public static final RealmListRequestHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final boolean hasValidState(Channel channel) {
		return channel.attr(AuthServer.SRP_ATTR).get() != null;
	}

	@Override
	public final void handleMessage(Channel channel, RealmlistRequestMessage msg) {
		LittleEndianWriterStream lews = new LittleEndianWriterStream(16);
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
		channel.writeAndFlush(lews.getPacket());
	}
}