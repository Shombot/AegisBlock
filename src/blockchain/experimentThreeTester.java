package blockchain;

import java.util.ArrayList;

public class experimentThreeTester {
	public static void main(String[] args) throws InterruptedException {
		long startExperiment = System.nanoTime();
		int numNodes = 1_000;
		double maliciousPercentage = 10;
		int numHospitals = 5;
		int numPatients = 5;
		int numBlocks = 1;
		
		NetworkSimulator verification = new NetworkSimulator();
		BlockChain.numHospitals = numHospitals;
		BlockChain.numPatients = numPatients;
		BlockChain chain = new BlockChain(numBlocks);
		BlockNode node;
		while(true) {
			try {
				node = new BlockNode(BlockChain.generateRandomSHA256(), BlockChain.generateRandomSHA256(),
						BlockChain.generateRandomSHA256(), BlockChain.generateRandomSHA256(), 0);
				break;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
		System.gc();
		long startHospitalConsensus = System.nanoTime();
		
		
		verification.setupNodes(numNodes, maliciousPercentage);
		verification.simulateVerifyBlockNode(node);

		
		long end = System.nanoTime();
		
		double elapsedHospitalAccess = (end - startHospitalConsensus) / 1000000000.0;
		double elapsedExperiment = (end - startExperiment) / 1000000000.0;
		
		System.out.println("The hospital consensus protocol with " + numNodes  + " hospitals took " + elapsedHospitalAccess + " seconds, "
				+ "while the whole experiment took " + elapsedExperiment + " seconds.");
	}
}

