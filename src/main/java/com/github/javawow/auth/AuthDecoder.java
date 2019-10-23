package com.github.javawow.auth;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

final class AuthDecoder extends ByteToMessageDecoder {
//	private static final Logger LOG = LoggerFactory.getLogger(AuthDecoder.class);

	AuthDecoder() {
		// keep it package-private
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		/*
		 * TODO We can't always rely on this since TCP packets can be fragmented, thus
		 * we have to use the SRP-6a protocol to determine the length of the packet
		 * based on the state of where we are in the protocol. This will need to be
		 * refactored.
		 */
		int length = in.readableBytes();
		// TODO Use pooled buffers
		ByteBuf buf = Unpooled.buffer(length, length);
		in.readBytes(buf, length);
		out.add(buf);
	}
}