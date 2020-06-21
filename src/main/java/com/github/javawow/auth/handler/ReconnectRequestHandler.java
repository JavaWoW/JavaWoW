package com.github.javawow.auth.handler;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.auth.AuthServer;
import com.github.javawow.auth.message.ReconnectRequestMessage;
import com.github.javawow.tools.RandomUtil;
import com.github.javawow.tools.packet.AuthPacketFactory;

import io.netty.channel.Channel;

public final class ReconnectRequestHandler implements BasicAuthHandler<ReconnectRequestMessage> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReconnectRequestHandler.class);
	private static final ReconnectRequestHandler INSTANCE = new ReconnectRequestHandler();

	private ReconnectRequestHandler() {
		// singleton
	}

	public static final ReconnectRequestHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final boolean hasValidState(Channel channel) {
		return channel.attr(AuthServer.SRP_ATTR).get() != null;
	}

	@Override
	public final void handleMessage(Channel channel, ReconnectRequestMessage msg) {
		String arch = new String(msg.getArch(), StandardCharsets.US_ASCII);
		String os = new String(msg.getOs(), StandardCharsets.US_ASCII);
		String locale = new String(msg.getLocale(), StandardCharsets.US_ASCII);
		LOGGER.info("Arch: {} OS: {} Locale: {}", arch, os, locale);
//		int timezone = msg.getTimezone();
//		int ip = msg.getIp();
		byte[] random = new byte[16];
//		RandomUtil.getSecureRandom().nextBytes(random);
		RandomUtil.getRandom().nextBytes(random);
		channel.writeAndFlush(AuthPacketFactory.getReconnectChallenge(random, LoginRequestHandler.VERSION_CHALLENGE));
	}
}