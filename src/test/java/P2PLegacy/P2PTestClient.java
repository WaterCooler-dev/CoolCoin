package P2PLegacy;

import dev.watercooler.coolcoin.P2PLegacy.Server.P2PMessage;
import dev.watercooler.coolcoin.P2PLegacy.Server.P2PMessageDecoder;
import dev.watercooler.coolcoin.P2PLegacy.Server.P2PMessageEncoder;
import dev.watercooler.coolcoin.P2PLegacy.Server.P2PMessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class P2PTestClient {
    public static void main(String[] args){
        String host = "127.0.0.1";
        int port = 8080;
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
                    ch.pipeline().addLast(new P2PTestHandler());
                }
            });

            ChannelFuture f = b.connect(host, port).sync();
            f.channel().writeAndFlush(new P2PMessage(P2PMessageType.TRANSACTION_CREATED, "대충 트랜잭션 만들었어용"))
                .addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        log.error("메세지 전송 실패", future.cause());
                    } else {
                        log.debug("메세지 보냄");
                    }
                });

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}

@Slf4j
class P2PTestHandler extends SimpleChannelInboundHandler<P2PMessage> {
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, P2PMessage message){
        log.info("서버한테 데이터 성공적으로 전송받음: {}", message.toString());
        channelHandlerContext.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
