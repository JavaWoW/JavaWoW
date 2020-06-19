package com.github.javawow.auth.handler;

import com.github.javawow.auth.AuthServer;
import com.github.javawow.auth.message.ReconnectRequestMessage;

import io.netty.channel.Channel;

public final class ReconnectRequestHandler implements BasicAuthHandler<ReconnectRequestMessage> {
	@Override
	public boolean hasValidState(Channel channel) {
		return channel.attr(AuthServer.SRP_ATTR).get() != null;
	}

	@Override
	public void handleMessage(Channel channel, ReconnectRequestMessage msg) {
		// TODO Auto-generated method stub
		
	}
}