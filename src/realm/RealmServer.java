package realm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import auth.AuthCodecFactory;

public class RealmServer {
	private static final int PORT = 1119;
	private static final Logger LOGGER = LoggerFactory.getLogger(RealmServer.class);
	private static final Scanner sc = new Scanner(System.in);
	private static SocketAcceptor acceptor;

	public static final void main(String[] args) {
		IoBuffer.setUseDirectBuffer(false);
		IoBuffer.setAllocator(new SimpleBufferAllocator());
		acceptor = new NioSocketAcceptor();
		acceptor.getSessionConfig().setReuseAddress(true); // XXX For debug only
		acceptor.getSessionConfig().setTcpNoDelay(true);
		acceptor.setCloseOnDeactivation(true);
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(AuthCodecFactory.getInstance()));
		acceptor.setHandler(new RealmServerHandler());
		for (;;) {
			try {
				acceptor.bind(new InetSocketAddress(PORT));
				break;
			} catch (IOException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					break;
				}
			}
		}
		LOGGER.info("Realm Server listening on port {}.", PORT);
		/*Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public final void run() {
				acceptor.unbind();
				acceptor.dispose();
			}
		});*/
		while (sc.hasNextLine()) {
			if (sc.nextLine().equalsIgnoreCase("q")) {
				System.out.println("Shutting down...");
				for (IoSession session : acceptor.getManagedSessions().values()) {
					session.close(true);
				}
				acceptor.unbind();
				acceptor.dispose(true);
				break;
			}
		}
	}
}