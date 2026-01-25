package dev.watercooler.coolcoin.P2PLegacy.Client;

import dev.watercooler.coolcoin.P2PLegacy.P2PNode;
import dev.watercooler.coolcoin.P2PLegacy.P2PNodeList;
import dev.watercooler.coolcoin.P2PLegacy.Server.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class P2PClient extends Thread {
    @Getter
    private final BlockingQueue<P2PMessage> queue = new LinkedBlockingQueue<>();
    private static final Logger log = LoggerFactory.getLogger(P2PClient.class);

    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch){
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 5, 4, 0, 0));
                    ch.pipeline().addLast(new P2PMessageDecoder());
                    ch.pipeline().addLast(new P2PMessageEncoder());
                }
            });

            while (!Thread.currentThread().isInterrupted()) {
                P2PMessage task = this.queue.take();

                for (P2PNode n : P2PNodeList.getInstance().getList()) {
                    ChannelFuture f = b.connect(n.getAddress(), n.getPort()).sync();

                    f.channel().writeAndFlush(task)
                            .addListener((ChannelFutureListener) future -> {
                                if (!future.isSuccess()) {
                                    log.error("메세지 전송 실패", future.cause());
                                } else {
                                    log.debug("메세지 보냄");
                                }
                            });

                    f.channel().closeFuture().sync();
                }
            }


        } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
