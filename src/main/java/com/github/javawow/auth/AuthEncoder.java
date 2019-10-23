package com.github.javawow.auth;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
final class AuthEncoder extends MessageToByteEncoder<byte[]> {
//	private static final Logger LOGGER = LoggerFactory.getLogger(AuthEncoder.class);
	private static final AuthEncoder INSTANCE = new AuthEncoder();

	private AuthEncoder() {
		// singleton
	}

	public static final AuthEncoder getInstance() {
		return INSTANCE;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
		out.writeBytes(msg);
	}
}