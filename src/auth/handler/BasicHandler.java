package auth.handler;

import org.apache.mina.core.session.IoSession;

import data.input.SeekableLittleEndianAccessor;

public interface BasicHandler {
	/**
	 * Verifies if the current state is valid for the handler to be executed.
	 * @param session The session attempting to execute the handler.
	 * @return true for a valid state, false otherwise
	 */
	boolean hasValidState(IoSession session);

	/**
	 * Implement this method to handle the operation to perform when the handler
	 * is called.
	 * @param session The session executing this handler.
	 * @param slea The seekable buffer containing the packet received.
	 */
	void handlePacket(IoSession session, SeekableLittleEndianAccessor slea);
}