package realm;

import java.util.Random;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.input.GenericSeekableLittleEndianAccessor;
import data.input.SeekableByteArrayStream;
import data.input.SeekableLittleEndianAccessor;
import data.output.LittleEndianWriterStream;

final class RealmServerHandler extends IoHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealmServerHandler.class);

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
		if (!(msg instanceof byte[])) { // wtf
			LOGGER.info("Received unknown object: {}", msg.getClass().getName());
			return;
		}
		SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new SeekableByteArrayStream((byte[]) msg));
		short header = slea.readShort();
		switch (header) {
			default: {
				LOGGER.info("Header [0x{}] Unhandled Packet: {}", Integer.toHexString(header), slea.toString());
				break;
			}
		}
		//LOGGER.info("Received: {}", HexTool.toString((byte[]) msg));
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