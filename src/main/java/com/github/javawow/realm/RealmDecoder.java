package com.github.javawow.realm;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

final class RealmDecoder extends ByteToMessageDecoder {
//	private static final Logger LOG = LoggerFactory.getLogger(RealmDecoder.class);

	RealmDecoder() {
		// keep it package-private
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 2) {
			// not enough data for a decode
			return;
		}
		in.markReaderIndex();
		int packetLength = in.readShort();
		if (in.readableBytes() < packetLength) {
			// still not enough data
			in.resetReaderIndex();
			return;
		}
		ByteBuf buf = Unpooled.buffer(packetLength, packetLength);
		buf.readBytes(buf, packetLength);
		out.add(buf);
	}
}