package P2P;

import dev.watercooler.coolcoin.P2P.P2PServer;

public class RunP2PServer {
    public static void main(String[] args) {
        Thread thread = new Thread(new P2PServer());
        thread.setName("CoolCoinP2PServer");
        thread.start();
    }
}
