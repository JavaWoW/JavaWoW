package com.github.javawow.auth;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.auth.handler.LoginRequestHandler;
import com.github.javawow.auth.handler.LoginVerifyHandler;
import com.github.javawow.auth.handler.RealmListRequestHandler;
import com.github.javawow.data.input.GenericSeekableLittleEndianAccessor;
import com.github.javawow.data.input.SeekableByteArrayStream;
import com.github.javawow.data.input.SeekableLittleEndianAccessor;
import com.github.javawow.tools.BasicHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
final class AuthServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerHandler.class);
	private static final AuthServerHandler INSTANCE = new AuthServerHandler();
	private static final Map<Byte, BasicHandler> handlers = new HashMap<Byte, BasicHandler>();

	static {
		handlers.put((byte) 0x00, LoginRequestHandler.getInstance());
		handlers.put((byte) 0x01, LoginVerifyHandler.getInstance());
		handlers.put((byte) 0x10, RealmListRequestHandler.getInstance());
	}

	private AuthServerHandler() {
		// singleton
	}

	public static final AuthServerHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@Override
	public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		byte[] buf = ((ByteBuf) msg).array();
		// TODO Pass ByteBuf instead of byte[]
		SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new SeekableByteArrayStream(buf));
		byte header = slea.readByte();
		BasicHandler handler = handlers.get(header);
		if (handler != null) {
			Channel ch = ctx.channel();
			if (handler.hasValidState(ch)) {
				handler.handlePacket(ch, slea);
			} else {
				LOGGER.warn("Invalid state detected for handler: {}", handler.getClass().getName());
			}
		} else {
			LOGGER.warn("Unhandled Packet. Header: 0x{}", Integer.toHexString(header));
		}
	}

	@Override
	public final void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error(cause.getLocalizedMessage(), cause);
	}
}