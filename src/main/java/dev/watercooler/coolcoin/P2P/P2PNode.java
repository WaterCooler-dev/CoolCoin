package dev.watercooler.coolcoin.P2P;

import dev.watercooler.coolcoin.P2P.Message.P2PMessage;
import dev.watercooler.coolcoin.P2P.Message.P2PMessageDecoder;
import dev.watercooler.coolcoin.P2P.Message.P2PMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class P2PNode extends Thread {
    private static final Logger log = LoggerFactory.getLogger(P2PNode.class);
    private final int port;
    private final BlockingQueue<P2PMessage> taskQueue = new LinkedBlockingQueue<>();
    private final MessageDeduplicator deduplicator;

    public P2PNode(int port){
        this.port = port;
        deduplicator = new MessageDeduplicator();
    }
    public P2PNode() {
        this(21232);
    }

    @Override
    public void run() {
        log.info("P2P 노드를 실행시키는 중...");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new IdleStateHandler(0, 30, 0));
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 5, 4, 0, 0));
                            ch.pipeline().addLast(new P2PMessageDecoder());
                            ch.pipeline().addLast(new P2PMessageEncoder());
                            ch.pipeline().addLast(new P2PNodeHandler());
                            ch.pipeline().addLast(new P2PGossipHandler(deduplicator));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(this.port).sync();
            log.info("P2P 노드가 {}에서 성공적으로 실행됨", this.port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("P2P 노드 종료 요청 받음");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            log.info("p2p 노드가 성공적으로 종료됨");
        }
    }

    public void connectToPeer(String host, int port) {
        log.info("{}:{} 노드에 연결중...", host, port);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new IdleStateHandler(0, 30, 0));
                            ch.pipeline().addLast(new P2PMessageDecoder());
                            ch.pipeline().addLast(new P2PMessageEncoder());
                            ch.pipeline().addLast(new P2PNodeHandler());
                            ch.pipeline().addLast(new P2PGossipHandler(deduplicator));
                        }
                    });

            ChannelFuture f = b.connect(host, port).sync();

            while(!Thread.currentThread().isInterrupted()){
                P2PMessage msg = taskQueue.take();
                deduplicator.markAsSeen(msg);
                P2PGroupManager.broadcastAll(msg);
                log.info("메세지 네트워크 전파 시작");
            }

            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            group.shutdownGracefully();
        }
    }

    public void addTask(P2PMessage message) throws InterruptedException {
        taskQueue.put(message);
    }
}
