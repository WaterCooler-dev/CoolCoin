package dev.watercooler.coolcoin.P2P;

import com.google.gson.Gson;
import dev.watercooler.coolcoin.P2P.Message.P2PMessage;
import dev.watercooler.coolcoin.Utility.HashUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MessageDeduplicator {
    private final Set<String> seenHashes = Collections.newSetFromMap(
            new LinkedHashMap<String, Boolean>(10000, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
                    return size() > 10000;
                }
            }
    );

    private final Gson gson = new Gson();

    public boolean isAlreadySeen(P2PMessage msg) {
        String jsonMessage = gson.toJson(msg);
        return seenHashes.contains(HashUtil.applySha256(jsonMessage));
    }

    public void markAsSeen(P2PMessage message) {
        String jsonMessage = gson.toJson(message);
        seenHashes.add(HashUtil.applySha256(jsonMessage));
    }
}
