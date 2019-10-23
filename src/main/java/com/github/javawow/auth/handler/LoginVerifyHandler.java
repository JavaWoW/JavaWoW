package com.github.javawow.auth.handler;

import java.math.BigInteger;

import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.auth.AuthServer;
import com.github.javawow.data.input.SeekableLittleEndianAccessor;
import com.github.javawow.data.output.LittleEndianWriterStream;
import com.github.javawow.tools.BasicHandler;
import com.github.javawow.tools.BitTools;
import com.github.javawow.tools.srp.WoWSRP6Server;

import io.netty.channel.Channel;

public final class LoginVerifyHandler implements BasicHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginVerifyHandler.class);
	private static final LoginVerifyHandler INSTANCE = new LoginVerifyHandler();

	private LoginVerifyHandler() {
	}

	public static final LoginVerifyHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final boolean hasValidState(Channel channel) {
		return channel.attr(AuthServer.SRP_ATTR).get() != null;
	}

	@Override
	public final void handlePacket(Channel channel, SeekableLittleEndianAccessor slea) {
		// Step 3 : Client sends us A and M1
		byte[] A_bytes = slea.read(32); // Little-endian format
		byte[] M1_bytes = slea.read(20);
		slea.skip(20); // crc_hash
		slea.readByte();
		slea.readByte();
		WoWSRP6Server srp = channel.attr(AuthServer.SRP_ATTR).get();
		if (srp == null) {
			LOGGER.error("srp not set!");
			channel.close();
			return;
		}
		BigInteger A = BitTools.toBigInteger(A_bytes, true);
		//BigInteger S;
		try {
			srp.calculateSecret(A);
		} catch (CryptoException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			channel.close();
			return;
		}
		BigInteger M1 = BitTools.toBigInteger(M1_bytes, false);
		boolean M1_match;
		try {
			M1_match = srp.verifyClientEvidenceMessage(M1);
		} catch (CryptoException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			channel.close();
			return;
		}
		if (!M1_match) {
			// Authentication Failure
			LOGGER.warn("M1 did not match.");
			return;
		}
		// Step 4 : Server responds with M2 (only if A and M1 checks out)
		BigInteger M2;
		try {
			M2 = srp.calculateServerEvidenceMessage();
		} catch (CryptoException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			channel.close();
			return;
		}
		// Begin Packet Response:
		LittleEndianWriterStream lews = new LittleEndianWriterStream();
		lews.write(1); // cmd
		lews.write(0); // error
		lews.write(BitTools.toByteArray(M2, 20));
		int accountFlag = 0x00800000; // 0x01 = GM, 0x08 = Trial, 0x00800000 = Pro pass (arena tournament)
		lews.writeInt(accountFlag);
		lews.writeInt(0); // surveyId
		lews.writeShort(0); // ?
		channel.write(lews.toByteArray()); // send the packet
	}
}