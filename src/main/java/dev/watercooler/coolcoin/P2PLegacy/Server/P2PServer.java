package dev.watercooler.coolcoin.P2PLegacy.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P2PServer implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(P2PServer.class);
    private final int port;

    public P2PServer(int port) {
        this.port = port;
    }

    public P2PServer() {
        this(8080);
    }

    @Override
    public void run() {
        log.info("P2P 서버 실행중...");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 5, 4, 0, 0));
                            ch.pipeline().addLast(new P2PMessageDecoder());
                            ch.pipeline().addLast(new P2PMessageEncoder());
                            ch.pipeline().addLast(new P2PHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(this.port).sync();
            log.info("P2P 서버가 {}에서 성공적으로 실행됨", this.port);
            f.channel().closeFuture().sync();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            log.info("p2p 서버가 성공적으로 종료됨");
        }
    }
}
