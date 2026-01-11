package dev.watercooler.coolcoin.P2P.Server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class P2PHandler extends SimpleChannelInboundHandler<P2PMessage> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, P2PMessage message) {
        log.debug("P2P를 통해 특정 값을 전달받음");
        log.debug("명령 해더: {}, 값: {}", message.getMessageCommand(), message.getMessageBody());

        P2PMessage context = new P2PMessage(message.getMessageCommand(), "메세지 받음: " + message.getMessageBody());
        ctx.writeAndFlush(context);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
