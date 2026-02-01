package P2P;

import dev.watercooler.coolcoin.P2P.P2PNode;

public class NodeConnectionTest {
    public static void main(String[] args) throws InterruptedException {
        P2PNode node = new P2PNode(8080);
        node.start();
        Thread.sleep(2000);
        node.connectToPeer("127.0.0.1", 8080);
    }
}
