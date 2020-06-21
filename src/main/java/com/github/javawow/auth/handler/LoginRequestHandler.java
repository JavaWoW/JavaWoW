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
import java.nio.charset.StandardCharsets;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.SRP6GroupParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.auth.AuthServer;
import com.github.javawow.auth.message.LoginRequestMessage;
import com.github.javawow.tools.RandomUtil;
import com.github.javawow.tools.packet.AuthPacketFactory;
import com.github.javawow.tools.srp.WoWSRP6Server;
import com.github.javawow.tools.srp.WoWSRP6VerifierGenerator;

import io.netty.channel.Channel;

public final class LoginRequestHandler implements BasicAuthHandler<LoginRequestMessage> {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginRequestHandler.class);
	private static final LoginRequestHandler INSTANCE = new LoginRequestHandler();
	private static final BigInteger N = new BigInteger(
			"894B645E89E1535BBDAD5B8B290650530801B18EBFBF5E8FAB3C82872A3E9BB7", 16);
	private static final BigInteger g = BigInteger.valueOf(7);
	public static final byte[] VERSION_CHALLENGE = { (byte) 0xBA, (byte) 0xA3, 0x1E, (byte) 0x99, (byte) 0xA0, 0x0B,
			0x21, 0x57, (byte) 0xFC, 0x37, 0x3F, (byte) 0xB3, 0x69, (byte) 0xCD, (byte) 0xD2, (byte) 0xF1 };
	private static final SRP6GroupParameters params = new SRP6GroupParameters(N, g);

	private LoginRequestHandler() {
		// singleton
	}

	public static final LoginRequestHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final boolean hasValidState(Channel channel) {
		return channel.attr(AuthServer.SRP_ATTR).get() == null; // only if srp is not set
	}

	@Override
	public final void handleMessage(Channel channel, LoginRequestMessage msg) {
		String arch = new String(msg.getArch(), StandardCharsets.US_ASCII);
		String os = new String(msg.getOs(), StandardCharsets.US_ASCII);
		String locale = new String(msg.getLocale(), StandardCharsets.US_ASCII);
		LOGGER.info("Arch: {} OS: {} Locale: {}", arch, os, locale);
//		int timezone = msg.getTimezone();
//		int ip = msg.getIp();
		// Step 1 : Client sends us I
		byte[] I = msg.getI(); // username
		// Step 2 : Server responds with B, g, N, s
		byte[] p = "lolwtf".toUpperCase().getBytes(); // p (kept secret throughout)
		byte[] s = new byte[32]; // salt (s)
		RandomUtil.getSecureRandom().nextBytes(s);
		WoWSRP6VerifierGenerator gen = new WoWSRP6VerifierGenerator();
		gen.init(params, new SHA1Digest());
		BigInteger v = gen.generateVerifier(s, I, p); // generate v
		// Store v (verifier) and s (salt) in the database
//		try {
//			FileUtil.saveVS(v, s);
//		} catch (IOException e) {
//			LOGGER.error(e.getLocalizedMessage(), e);
//		}
		WoWSRP6Server srp = WoWSRP6Server.init(params, v, I, s, new SHA1Digest(), RandomUtil.getSecureRandom());
		BigInteger B = srp.generateServerCredentials(); // generate B
		// Set client values
		channel.attr(AuthServer.SRP_ATTR).set(srp);
		// Now we have B, g, N, s (send it here)
		channel.writeAndFlush(AuthPacketFactory.getLoginChallenge(B, g, N, s, VERSION_CHALLENGE, (byte) 0));
	}
}