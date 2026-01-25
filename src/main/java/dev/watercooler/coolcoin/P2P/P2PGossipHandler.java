package dev.watercooler.coolcoin.P2P;

import dev.watercooler.coolcoin.P2P.Message.P2PMessage;
import dev.watercooler.coolcoin.Utility.HashUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P2PGossipHandler extends SimpleChannelInboundHandler<P2PMessage> {
    private static final Logger log = LoggerFactory.getLogger(P2PGossipHandler.class);
    private final MessageDeduplicator deduplicator = new MessageDeduplicator();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        P2PGroupManager.addPeer(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, P2PMessage msg) throws Exception {
        String msgHash = HashUtil.applySha256(msg.getMessageBody());
        if (deduplicator.isAlreadySeen(msgHash)) {
            return;
        }

        deduplicator.markAsSeen(msgHash);
        P2PGroupManager.broadcast(msg, ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
