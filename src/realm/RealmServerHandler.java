package realm;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import realm.handler.RealmVerifyHandler;
import tools.BasicHandler;
import data.input.GenericSeekableLittleEndianAccessor;
import data.input.SeekableByteArrayStream;
import data.input.SeekableLittleEndianAccessor;
import data.output.LittleEndianWriterStream;

final class RealmServerHandler extends IoHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealmServerHandler.class);
	private static final RealmServerHandler INSTANCE = new RealmServerHandler();
	private static final Map<Short, BasicHandler> handlers = new HashMap<Short, BasicHandler>();

	static {
		handlers.put((short) 0x1ED, RealmVerifyHandler.getInstance());
	}

	private RealmServerHandler() {
	}

	public static final RealmServerHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public final void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		LOGGER.error(cause.getLocalizedMessage(), cause);
	}

	@Override
	public final void inputClosed(IoSession session) throws Exception {
		super.inputClosed(session);
	}

	@Override
	public final void messageReceived(IoSession session, Object msg) throws Exception {
		SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new SeekableByteArrayStream((byte[]) msg));
		short header = slea.readShort();
		BasicHandler handler = handlers.get(header);
		if (handler != null) {
			if (handler.hasValidState(session)) {
				handler.handlePacket(session, slea);
			} else {
				LOGGER.warn("Invalid state detected for handler: {}", handler.getClass().getName());
			}
		} else {
			LOGGER.warn("Unhandled Packet. Header: 0x{}", Integer.toHexString(header));
		}
	}

	@Override
	public final void messageSent(IoSession session, Object msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public final void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public final void sessionCreated(IoSession session) throws Exception {
		super.sessionClosed(session);
	}

	@Override
	public final void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public final void sessionOpened(IoSession session) throws Exception {
		LOGGER.info("IoSession opened with {}.", session.getRemoteAddress());
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
		session.write(lews.toByteArray());
	}
}