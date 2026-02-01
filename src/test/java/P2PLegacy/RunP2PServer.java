package P2PLegacy;

import dev.watercooler.coolcoin.P2PLegacy.Server.P2PServer;

public class RunP2PServer {
    public static void main(String[] args) {
        Thread thread = new Thread(new P2PServer());
        thread.setName("CoolCoinP2PServer");
        thread.start();
    }
}
