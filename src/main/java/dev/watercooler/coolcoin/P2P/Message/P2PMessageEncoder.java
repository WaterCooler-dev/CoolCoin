package dev.watercooler.coolcoin.P2P.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class P2PMessageEncoder extends MessageToByteEncoder<P2PMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, P2PMessage msg, ByteBuf out) {
        out.writeInt(0xCAFE);
        out.writeByte(msg.getMessageCommand().getValue());
        byte[] bodyBytes = msg.getMessageBody().getBytes(StandardCharsets.UTF_8);

        out.writeInt(bodyBytes.length);
        out.writeBytes(bodyBytes);
    }
}
