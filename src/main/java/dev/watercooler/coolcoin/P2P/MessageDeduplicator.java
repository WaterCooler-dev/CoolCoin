package dev.watercooler.coolcoin.P2P;

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

    public boolean isAlreadySeen(String hash) {
        return seenHashes.contains(hash);
    }

    public void markAsSeen(String hash) {
        seenHashes.add(hash);
    }
}
