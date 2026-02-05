package dev.watercooler.coolcoin.P2P;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P2PGroupManager {
    private static final Logger log = LoggerFactory.getLogger(P2PGroupManager.class);
    private static final ChannelGroup peers =  new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void addPeer(Channel channel) {
        peers.add(channel);
       log.info("현재 연결된 피어 수: {}", peers.size());
    }

    public static void removePeer(Channel channel) {
        log.info("피어 연결 해제됨.");
    }

    public static void broadcast(Object message, Channel sender) {
        peers.writeAndFlush(message, channel -> channel != sender);
    }

    public static void broadcastAll(Object message) {
        peers.writeAndFlush(message);
    }
}
