package blockchain;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Node {
    private final int id;
    private final int signatureThreshold; // per-instance threshold
    private final Set<Integer> trueSignatures = ConcurrentHashMap.newKeySet();
    private volatile boolean completed = false;
    private volatile long timeCompleted = -1;

    public Node(int id, int threshold) {
        this.id = id;
        this.signatureThreshold = threshold;
    }

    // Thread-safe signature processing
    public synchronized void receiveValidSignature(int signerId, long timeSinceStart) {
        if (completed) return;

        trueSignatures.add(signerId);
        if (trueSignatures.size() >= signatureThreshold && !completed) {
            completed = true;
            timeCompleted = timeSinceStart;
        }
    }

    public boolean hasCompleted() {
        return completed;
    }

    public long getTimeCompleted() {
        return timeCompleted;
    }

    // Defensive copy to avoid external modification
    public Set<Integer> getKnownTrueSignatures() {
        return new HashSet<>(trueSignatures);
    }

    public int getId() {
        return id;
    }
}