package auth;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.BasicHandler;
import auth.handler.LoginRequestHandler;
import auth.handler.LoginVerifyHandler;
import auth.handler.RealmListRequestHandler;
import data.input.GenericSeekableLittleEndianAccessor;
import data.input.SeekableByteArrayStream;
import data.input.SeekableLittleEndianAccessor;

final class AuthServerHandler extends IoHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerHandler.class);
	private static final AuthServerHandler INSTANCE = new AuthServerHandler();
	private static final Map<Byte, BasicHandler> handlers = new HashMap<Byte, BasicHandler>();

	static {
		handlers.put((byte) 0x00, LoginRequestHandler.getInstance());
		handlers.put((byte) 0x01, LoginVerifyHandler.getInstance());
		handlers.put((byte) 0x10, RealmListRequestHandler.getInstance());
	}

	private AuthServerHandler() {
	}

	public static final AuthServerHandler getInstance() {
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
		byte header = slea.readByte();
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
		// TODO Auto-generated method stub

	}
}