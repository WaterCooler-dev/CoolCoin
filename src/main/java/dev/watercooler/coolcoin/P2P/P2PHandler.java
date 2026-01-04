package dev.watercooler.coolcoin.P2P;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class P2PHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        log.debug("P2P를 통해 특정 값을 전달받음");
        String context = byteBuf.toString(CharsetUtil.UTF_8);
        ctx.writeAndFlush(Unpooled.copiedBuffer("값 받음" + context, CharsetUtil.UTF_8));
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        log.error("P2P 핸들러 에러 발생", cause);
        ctx.close();
    }
}
