package dev.watercooler.coolcoin.P2P;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class P2PMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 7) return;

        in.markReaderIndex();
        int magic = in.readInt();
        if (magic != 0xCAFE) {
            ctx.close();
            return;
        }

        P2PMessageType cmd = P2PMessageType.of(in.readByte());
        int length = in.readInt();

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        byte[] bodyArray = new byte[length];
        in.readBytes(bodyArray);

        out.add(new P2PMessage(cmd, new String(bodyArray, StandardCharsets.UTF_8)));
    }
}
