package auth.handler;

import org.apache.mina.core.session.IoSession;

import data.input.SeekableLittleEndianAccessor;

public interface BasicHandler {
	void handlePacket(IoSession session, SeekableLittleEndianAccessor slea);
}