package auth;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthServer {
	private static final int PORT = 3724;
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServer.class);
	private static final Scanner sc = new Scanner(System.in);
	private static SocketAcceptor acceptor;

	private AuthServer() {
	}

	public static final void main(String[] args) {
		IoBuffer.setUseDirectBuffer(false);
		IoBuffer.setAllocator(new SimpleBufferAllocator());
		acceptor = new NioSocketAcceptor();
		acceptor.getSessionConfig().setReuseAddress(true); // XXX For debug only
		acceptor.getSessionConfig().setTcpNoDelay(true);
		acceptor.setCloseOnDeactivation(true);
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(WoWCodecFactory.getInstance()));
		acceptor.setHandler(new AuthServerHandler());
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
		LOGGER.info("Auth Server listening on port {}.", PORT);
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
				acceptor.unbind();
				acceptor.dispose(true);
				break;
			}
		}
	}
}