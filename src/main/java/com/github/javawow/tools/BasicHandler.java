package com.github.javawow.tools;

import com.github.javawow.data.input.SeekableLittleEndianAccessor;

import io.netty.channel.Channel;

public interface BasicHandler {
	/**
	 * Verifies if the current state is valid for the handler to be executed.
	 * @param session The session attempting to execute the handler.
	 * @return true for a valid state, false otherwise
	 */
	boolean hasValidState(Channel session);

	/**
	 * Implement this method to handle the operation to perform when the handler
	 * is called.
	 * @param session The session executing this handler.
	 * @param slea The seekable buffer containing the packet received.
	 */
	void handlePacket(Channel session, SeekableLittleEndianAccessor slea);
}