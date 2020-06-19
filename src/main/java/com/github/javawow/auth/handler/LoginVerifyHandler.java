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

import java.math.BigInteger;

import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.auth.AuthServer;
import com.github.javawow.auth.AuthState;
import com.github.javawow.auth.message.LoginProofMessage;
import com.github.javawow.tools.packet.AuthPacketFactory;
import com.github.javawow.tools.srp.WoWSRP6Server;

import io.netty.channel.Channel;

public final class LoginVerifyHandler implements BasicAuthHandler<LoginProofMessage> {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginVerifyHandler.class);
	private static final LoginVerifyHandler INSTANCE = new LoginVerifyHandler();

	private LoginVerifyHandler() {
		// singleton
	}

	public static final LoginVerifyHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final boolean hasValidState(Channel channel) {
		return channel.attr(AuthServer.SRP_ATTR).get() != null;
	}

	@Override
	public final void handleMessage(Channel channel, LoginProofMessage msg) {
		// Step 3 : Client sends us A and M1
		WoWSRP6Server srp = channel.attr(AuthServer.SRP_ATTR).get();
		if (srp == null) {
			LOGGER.error("srp not set!");
			channel.close();
			return;
		}
		BigInteger A = msg.getA();
		try {
			srp.calculateSecret(A);
		} catch (CryptoException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			channel.close();
			return;
		}
		BigInteger M1 = msg.getM1();
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
		// Set client attributes
		channel.attr(AuthState.ATTRIBUTE_KEY).set(AuthState.AUTHENTICATED);
		// Send M2 to the client
		channel.writeAndFlush(AuthPacketFactory.getLoginProof(M2));
	}
}