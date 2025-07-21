package blockchain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class NetworkSimulator {
    private int NUM_NODES;
    private int THRESHOLD;
    private final List<Node> nodes = new ArrayList<>();
    private ExecutorService executor;
    private final AtomicInteger completedCount = new AtomicInteger(0);
    private final List<Long> completionTimes = Collections.synchronizedList(new ArrayList<>());

    public void setupNodes(int numNodes, double maliciousPercentage) {
    	NUM_NODES = numNodes;
        THRESHOLD = numNodes / 2;
        int numMalicious = (int) ((maliciousPercentage * NUM_NODES) / 100.0);
        executor = Executors.newCachedThreadPool();
        nodes.clear();

        for (int i = 0; i < NUM_NODES; i++) {
            boolean isMalicious = i < numMalicious;
            nodes.add(new Node(i, THRESHOLD, isMalicious));
        }

        System.out.printf("Initialized %d nodes (%.2f%% malicious = %d nodes)%n",
                NUM_NODES, maliciousPercentage, 
                numMalicious);
    }

    public void simulateVerifyBlockOne(ResearcherBlockOne blockOne) throws InterruptedException {
    	completedCount.set(0);
    	completionTimes.clear();
        long startTime = System.nanoTime();

        for (int i = 0; i < NUM_NODES; i++) {
            int finalI = i;
            executor.submit(() -> {
                Random rand = new Random();
                Node current = nodes.get(finalI);
                
                if (current.getMalicious()) return;

                // Attempt verification ONCE — only nodes that pass will propagate their ID
                boolean verified = ResearcherBlockOne.verifyBlockOne(blockOne);
                if (!verified) return;

                // Add own signature if verified
                current.receiveValidSignature(finalI, System.nanoTime() - startTime);
                
                for (int j = 0; j < 5; j++) {
                    int targetId = rand.nextInt(NUM_NODES);
                    if (targetId == finalI) continue;
                    Node targetNode = nodes.get(targetId);
                    if (!targetNode.getMalicious()) {
                        targetNode.receiveValidSignature(finalI, System.nanoTime() - startTime);
                    }
                }


                while (true) {
                    if (completedCount.get() >= THRESHOLD) break;

                    int targetId = rand.nextInt(NUM_NODES);
                    if (targetId == finalI) continue;

                    Node targetNode = nodes.get(targetId);

                    // Share only verified (true) signatures
                    Set<Integer> knownValid = current.getKnownTrueSignatures();
                    long now = System.nanoTime();

                    boolean wasDone = targetNode.hasCompleted();

                    for (Integer signerId : knownValid) {
                        targetNode.receiveValidSignature(signerId, now - startTime);
                    }

                    if (!wasDone && targetNode.hasCompleted()) {
                        int updated = completedCount.incrementAndGet();
                        if (updated <= THRESHOLD) {
                            completionTimes.add(targetNode.getTimeCompleted());
                        }
                    }

                    try {
                    	Thread.sleep(rand.nextInt(30) + 60); // simulate delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("First " + THRESHOLD + " nodes completed verifying Block 1.");
        if (completionTimes.size() >= THRESHOLD) {
            System.out.printf("Node %d completed at %.3f seconds\n", THRESHOLD - 1, completionTimes.get(THRESHOLD - 1) / 1_000_000_000.0);
        } else {
            System.out.printf("Only %d nodes reached the signature threshold. Not enough to hit target of %d.\n", completionTimes.size(), THRESHOLD);
        }

    }
    
    public void simulateVerifyBlockTwo(ResearcherBlockTwo blockTwo) throws InterruptedException {
    	completedCount.set(0);
    	completionTimes.clear();
        long startTime = System.nanoTime();

        for (int i = 0; i < NUM_NODES; i++) {
            int finalI = i;
            executor.submit(() -> {
                Random rand = new Random();
                Node current = nodes.get(finalI);
                
                if (current.getMalicious()) return;

                // Attempt verification ONCE — only nodes that pass will propagate their ID
                boolean verified = ResearcherBlockTwo.verifyBlockTwo(blockTwo);
                if (!verified) return;

                // Add own signature if verified
                current.receiveValidSignature(finalI, System.nanoTime() - startTime);
                
                for (int j = 0; j < 5; j++) {
                    int targetId = rand.nextInt(NUM_NODES);
                    if (targetId == finalI) continue;
                    Node targetNode = nodes.get(targetId);
                    if (!targetNode.getMalicious()) {
                        targetNode.receiveValidSignature(finalI, System.nanoTime() - startTime);
                    }
                }

                while (true) {
                    if (completedCount.get() >= THRESHOLD) break;

                    int targetId = rand.nextInt(NUM_NODES);
                    if (targetId == finalI) continue;

                    Node targetNode = nodes.get(targetId);

                    // Share only verified (true) signatures
                    Set<Integer> knownValid = current.getKnownTrueSignatures();
                    long now = System.nanoTime();

                    boolean wasDone = targetNode.hasCompleted();

                    for (Integer signerId : knownValid) {
                        targetNode.receiveValidSignature(signerId, now - startTime);
                    }

                    if (!wasDone && targetNode.hasCompleted()) {
                        int updated = completedCount.incrementAndGet();
                        if (updated <= THRESHOLD) {
                            completionTimes.add(targetNode.getTimeCompleted());
                        }
                    }

                    try {
                        Thread.sleep(rand.nextInt(30) + 60); // simulate delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("First " + THRESHOLD + " nodes completed verifying Block 2.");
        if (completionTimes.size() >= THRESHOLD) {
            System.out.printf("Node %d completed at %.3f seconds\n", THRESHOLD - 1, completionTimes.get(THRESHOLD - 1) / 1_000_000_000.0);
        } else {
            System.out.printf("Only %d nodes reached the signature threshold. Not enough to hit target of %d.\n", completionTimes.size(), THRESHOLD);
        }

    }
    
    public void simulateVerifyBlockNode(BlockNode block) throws InterruptedException {
    	completedCount.set(0);
    	completionTimes.clear();
        long startTime = System.nanoTime();

        for (int i = 0; i < NUM_NODES; i++) {
            int finalI = i;
            executor.submit(() -> {
                Random rand = new Random();
                Node current = nodes.get(finalI);
                
                if (current.getMalicious()) return;
                
                
                // Attempt verification ONCE — only nodes that pass will propagate their ID
                boolean verified;
                while(true) {
                	try {
                		verified = block.verifyBlockNode();
                		break;
                	} catch(Exception e) {
                		e.printStackTrace();
                	}
                }
                
                if (!verified) return;

                // Add own signature if verified
                current.receiveValidSignature(finalI, System.nanoTime() - startTime);
                
                for (int j = 0; j < 5; j++) {
                    int targetId = rand.nextInt(NUM_NODES);
                    if (targetId == finalI) continue;
                    Node targetNode = nodes.get(targetId);
                    if (!targetNode.getMalicious()) {
                        targetNode.receiveValidSignature(finalI, System.nanoTime() - startTime);
                    }
                }

                while (true) {
                    if (completedCount.get() >= THRESHOLD) break;

                    int targetId = rand.nextInt(NUM_NODES);
                    if (targetId == finalI) continue;

                    Node targetNode = nodes.get(targetId);

                    // Share only verified (true) signatures
                    Set<Integer> knownValid = current.getKnownTrueSignatures();
                    long now = System.nanoTime();

                    boolean wasDone = targetNode.hasCompleted();

                    for (Integer signerId : knownValid) {
                        targetNode.receiveValidSignature(signerId, now - startTime);
                    }

                    if (!wasDone && targetNode.hasCompleted()) {
                        int updated = completedCount.incrementAndGet();
                        if (updated <= THRESHOLD) {
                            completionTimes.add(targetNode.getTimeCompleted());
                        }
                    }

                    try {
                        Thread.sleep(rand.nextInt(30) + 60); // simulate delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("First " + THRESHOLD + " nodes completed verifying Block 2.");
        if (completionTimes.size() >= THRESHOLD) {
            System.out.printf("Node %d completed at %.3f seconds\n", THRESHOLD - 1, completionTimes.get(THRESHOLD - 1) / 1_000_000_000.0);
        } else {
            System.out.printf("Only %d nodes reached the signature threshold. Not enough to hit target of %d.\n", completionTimes.size(), THRESHOLD);
        }

    }
}
