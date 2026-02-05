package P2P;

import dev.watercooler.coolcoin.P2P.Message.P2PMessage;
import dev.watercooler.coolcoin.P2P.Message.P2PMessageType;
import dev.watercooler.coolcoin.P2P.P2PNode;

public class NodeGossipTest {
    public static void main(String[] args) throws InterruptedException {
        P2PNode node1 = new P2PNode(8080);
        P2PNode node2 = new P2PNode(8081);
        P2PNode node3 = new P2PNode(8082);
        P2PNode node4 = new P2PNode(8083);

        node1.start();
        node2.start();
        node3.start();
        node4.start();

        Thread.sleep(2000);

        node1.connectToPeer("127.0.0.1", 8081);
        node2.connectToPeer("127.0.0.1", 8083);
        node2.connectToPeer("127.0.0.1", 8082);

        Thread.sleep(2000);

        node1.addTask(new P2PMessage(P2PMessageType.TEST, "테스트 메세지 전송"));
    }
}
