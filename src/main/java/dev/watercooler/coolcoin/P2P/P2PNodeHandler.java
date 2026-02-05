package dev.watercooler.coolcoin.P2P;

import dev.watercooler.coolcoin.P2P.Message.P2PMessage;
import dev.watercooler.coolcoin.P2P.Message.P2PMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P2PNodeHandler extends SimpleChannelInboundHandler<P2PMessage> {
    private static final Logger log = LoggerFactory.getLogger(P2PNodeHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("피어와 연결됨. 핸드쉐이크 시작...");
        P2PMessage msg = new P2PMessage(P2PMessageType.HANDSHAKE_VERSION, "{ \"version\": 1, \"height\": 100 }");
        ctx.writeAndFlush(msg);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, P2PMessage msg) throws Exception {
        log.info("다른 노드에서 메세지 받음.");

        switch (msg.getMessageCommand()) {
            case HANDSHAKE_VERSION:
                log.info("다른 노드의 버전 확인됨. 핸드셰이크 노드에 응답 전송...");
                ctx.writeAndFlush(new P2PMessage(P2PMessageType.HANDSHAKE_VERACK, "OK"));
                break;
            case HANDSHAKE_VERACK:
                log.info("성공적으로 핸드쉐이크 완료됨.");
                P2PGroupManager.addPeer(ctx.channel());
                break;
            case HEARTBEAT_PING:
                log.info("Ping 수신됨. Pong 전송...");
                ctx.writeAndFlush(new P2PMessage(P2PMessageType.HEARTBEAT_PONG, msg.getMessageBody()));
                break;
            case TEST:
                log.info("테스트 메세지 수신");
                break;
        }

        ctx.fireChannelRead(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            if (event.state() == IdleState.WRITER_IDLE) {
                log.info("유휴 상태가 감지되어 Ping 전송");
                ctx.writeAndFlush(new P2PMessage(P2PMessageType.HEARTBEAT_PING, String.valueOf(System.currentTimeMillis())));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
