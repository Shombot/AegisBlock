package blockchain;

import java.util.ArrayList;

public class experimentTwoTester {
	public static void main(String[] args) throws InterruptedException {
		long startExperiment = System.nanoTime();
		int numHospitals = 5;
		int numPatients = 5;
		int blocksBefore = 1;
		int blocksAfter = 8;
		int numBlocks = blocksBefore + blocksAfter + 1;
		int numNodes = 600;
		double maliciousPercentage = 0;
		NetworkSimulator verificationOne = new NetworkSimulator();
		NetworkSimulator verificationTwo = new NetworkSimulator();
		
		BlockChain.numHospitals = numHospitals;
		BlockChain.numPatients = numPatients;
		BlockChain bc = new BlockChain(numBlocks);
		
		Researcher researcher = new Researcher();
		
		
		System.gc();
		long startResearcherAccess = System.nanoTime();
		ResearcherBlockOne blockOne = new ResearcherBlockOne(researcher, BlockChain.ledger.get(blocksBefore), blocksBefore, blocksAfter);
		researcher.firstBlocks.add(blockOne);
		
		verificationOne.setupNodes(numNodes, maliciousPercentage);
		verificationOne.simulateVerifyBlockOne(blockOne);
		
		ResearcherBlockTwo blockTwo = new ResearcherBlockTwo(blockOne, blocksBefore, blocksAfter);
		researcher.secondBlocks.add(blockTwo);
		
		verificationTwo.setupNodes(numNodes, maliciousPercentage);
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
		
		System.out.println("The researcher access process for encrypting " + numBlocks + " blocks with " + numNodes + " hospitals in the system is "
				+ elapsedResearcherAccess + " seconds, while the whole experiment took " + elapsedExperiment + " seconds.");
	}
}
