package com.github.javawow.realm;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.data.input.GenericSeekableLittleEndianAccessor;
import com.github.javawow.data.input.SeekableByteArrayStream;
import com.github.javawow.data.input.SeekableLittleEndianAccessor;
import com.github.javawow.data.output.LittleEndianWriterStream;
import com.github.javawow.realm.handler.RealmVerifyHandler;
import com.github.javawow.tools.BasicHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
final class RealmServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealmServerHandler.class);
	private static final RealmServerHandler INSTANCE = new RealmServerHandler();
	private static final Map<Short, BasicHandler> handlers = new HashMap<Short, BasicHandler>();

	static {
		handlers.put((short) 0x1ED, RealmVerifyHandler.getInstance());
	}

	private RealmServerHandler() {
		// singleton
	}

	public static final RealmServerHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("IoSession opened with {}.", ctx.channel().remoteAddress());
		// Send Authentication Challenge Packet:
		LittleEndianWriterStream lews = new LittleEndianWriterStream();
		lews.writeShort(0x01EC); // header
		lews.writeInt(1); // ?
		Random r = new Random(1337);
		byte[] seed1 = new byte[16];
		byte[] seed2 = new byte[16];
		r.nextBytes(seed1);
		r.nextBytes(seed2);
		lews.write(seed1); // ?
		lews.write(seed2); // ?
		ctx.write(lews.toByteArray());
	}

	@Override
	public final void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@Override
	public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new SeekableByteArrayStream((byte[]) msg));
		short header = slea.readShort();
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