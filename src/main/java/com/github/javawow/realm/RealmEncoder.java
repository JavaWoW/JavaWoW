package com.github.javawow.realm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
final class RealmEncoder extends MessageToByteEncoder<byte[]> {
	private static final RealmEncoder INSTANCE = new RealmEncoder();

	private RealmEncoder() {
		// singleton
	}

	public static final RealmEncoder getInstance() {
		return INSTANCE;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
		out.writeShortLE(msg.length);
		out.writeBytes(msg);
	}
}