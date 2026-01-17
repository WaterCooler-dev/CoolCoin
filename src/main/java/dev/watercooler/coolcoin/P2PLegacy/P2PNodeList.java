package dev.watercooler.coolcoin.P2PLegacy;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class P2PNodeList {
    @Getter
    private final List<P2PNode> list = Collections.synchronizedList(new ArrayList<>());

    private P2PNodeList() {}

    public static P2PNodeList getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final P2PNodeList instance = new P2PNodeList();
    }
}
