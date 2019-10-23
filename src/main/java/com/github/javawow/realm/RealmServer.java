package com.github.javawow.realm;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.NetUtil;

public class RealmServer {
	private static final int PORT = 1337;
	private static final Logger LOGGER = LoggerFactory.getLogger(RealmServer.class);
	private static final Scanner sc = new Scanner(System.in);
	private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

	public static final void main(String[] args) {
		ServerBootstrap b = new ServerBootstrap()
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast("encoder", RealmEncoder.getInstance());
						ch.pipeline().addLast("decoder", new RealmDecoder());
						ch.pipeline().addLast("handler", RealmServerHandler.getInstance());
					}
				})
				.option(ChannelOption.SO_BACKLOG, Integer.valueOf(NetUtil.SOMAXCONN))
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
				.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
		ChannelFuture cf = b.bind(PORT).addListener(f -> {
			if (f.isSuccess()) {
				LOGGER.info("Realm Server listening on port {}.", PORT);
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