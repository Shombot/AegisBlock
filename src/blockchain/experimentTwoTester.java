package blockchain;

import java.util.ArrayList;

public class experimentTwoTester {
	public static void main(String[] args) throws InterruptedException {
		long startExperiment = System.nanoTime();
		int numHospitals = 5;
		int numPatients = 5;
		int blocksBefore = 10;
		int blockIndex = blocksBefore;
		int blocksAfter = 9;
		int numBlocks = blocksBefore + blocksAfter + 1;
		int numNodes = 12000;
		int numThreads = 200;
		NetworkSimulator verificationOne = new NetworkSimulator();
		NetworkSimulator verificationTwo = new NetworkSimulator();
		
		BlockChain.numHospitals = numHospitals;
		BlockChain.numPatients = numPatients;
		BlockChain bc = new BlockChain(numBlocks);
		
		Researcher researcher = new Researcher();
		
		
		System.gc();
		long startResearcherAccess = System.nanoTime();
		ResearcherBlockOne blockOne = new ResearcherBlockOne(researcher, BlockChain.ledger.get(blockIndex), blocksBefore, blocksAfter);
		researcher.firstBlocks.add(blockOne);
		
		verificationOne.setupNodes(numNodes, numThreads);
		verificationOne.simulateVerifyBlockOne(blockOne);
		
		ResearcherBlockTwo blockTwo = new ResearcherBlockTwo(blockOne, blocksBefore, blocksAfter);
		researcher.secondBlocks.add(blockTwo);
		
		verificationTwo.setupNodes(numNodes, numThreads);
		verificationTwo.simulateVerifyBlockTwo(blockTwo);
		
		BlockNode[] blocksArr = new BlockNode[blockTwo.getNumBlocks()];
		for(int i = 0; i < blockTwo.getNumBlocks(); i++) {
			blocksArr[i] = BlockChain.ledger.get(i);
		}
		
		ResearcherBlockThree plaintextBlockThree = new ResearcherBlockThree(blockTwo, blocksArr);
		researcher.thirdBlocks.add(plaintextBlockThree);
		
		ArrayList<ResearcherBlockInformation> encryptedBlockThreeList = plaintextBlockThree.generateEncryption();
		long end = System.nanoTime();
		
		double elapsedResearcherAccess = (end - startResearcherAccess) / 1000000000.0;
		double elapsedExperiment = (end - startExperiment) / 1000000000.0;
		
		System.out.println("The researcher access process for encrypting " + numBlocks + " blocks with " + numNodes + " hospitals in the system with " + 
				numThreads + " threads is " + elapsedResearcherAccess + " seconds, while the whole experiment took " + elapsedExperiment + " seconds.");
		
		/*
		 * long startExperiment = System.nanoTime();
		long startBlockChain = System.nanoTime();
		BlockChain bc = new BlockChain(numBlocks); //change nanme of testing zkp
		long end = System.nanoTime();
		
		double elapsedAddBlock = (end - startBlockChain) / 1000000000.0;
		double elapsedExperiment = (end - startExperiment) / 1000000000.0;
		
		System.out.println("The block creation process for " + numPatients + " patients and " + numHospitals + " hospitals in the system to add " + 
				numBlocks + " blocks is " + elapsedAddBlock + " seconds, while the whole experiment took " + elapsedExperiment + " seconds.");
		*/
	}
}
