package com.github.javawow.auth;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javawow.tools.srp.WoWSRP6Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.NetUtil;

public final class AuthServer {
	private static final int PORT = 3724;
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServer.class);
	private static final Scanner sc = new Scanner(System.in);
	private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
	public static final AttributeKey<WoWSRP6Server> SRP_ATTR = AttributeKey.newInstance("SRP");

	private AuthServer() {
	}

	public static final void main(String[] args) {
		ServerBootstrap b = new ServerBootstrap()
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast("encoder", AuthEncoder.getInstance());
						ch.pipeline().addLast("decoder", new AuthDecoder());
						ch.pipeline().addLast("handler", AuthServerHandler.getInstance());
					}
				})
				.option(ChannelOption.SO_BACKLOG, Integer.valueOf(NetUtil.SOMAXCONN))
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
				.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
		ChannelFuture cf = b.bind(PORT).addListener(f -> {
			if (f.isSuccess()) {
				LOGGER.info("Auth Server listening on port {}.", PORT);
			} else {
				LOGGER.error("Binding to port {} failed", Integer.valueOf(PORT), f.cause());
			}
		});
		// Wait for the bind to complete
		try {
			cf.await();
		} catch (InterruptedException e) {
			// Restore interrupted status
			Thread.currentThread().interrupt();
		}
		while (sc.hasNextLine()) {
			if (sc.nextLine().equalsIgnoreCase("q")) {
				System.out.println("Shutting down...");
				workerGroup.shutdownGracefully();
				bossGroup.shutdownGracefully();
				break;
			}
		}
	}
}