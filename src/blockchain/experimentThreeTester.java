package blockchain;

import java.util.ArrayList;

public class experimentThreeTester {
	public static void main(String[] args) throws InterruptedException {
//		long startExperiment = System.nanoTime();
		int numHospitals = 5;
		int numPatients = 5;
		int numBlocks = 20;
		int blocksBefore = numBlocks / 2;
		int blocksAfter = (numBlocks % 2 == 0) ? (numBlocks / 2) - 1 : (numBlocks / 2);
		int numNodes = 6_000;
		double maliciousPercentage = 50;
		NetworkSimulator verificationOne = new NetworkSimulator();
		NetworkSimulator verificationTwo = new NetworkSimulator();
		
		BlockChain.numHospitals = numHospitals;
		BlockChain.numPatients = numPatients;
		BlockChain bc = new BlockChain(numBlocks);
		
		Researcher researcher = new Researcher();
		long end;
		
		
		System.gc();
		long startCreateBlockOne = System.nanoTime();
		ResearcherBlockOne blockOne = new ResearcherBlockOne(researcher, BlockChain.ledger.get(blocksBefore), blocksBefore, blocksAfter);
		researcher.firstBlocks.add(blockOne);
		end = System.nanoTime();
		double createBlockOne = (end - startCreateBlockOne) / 1000000000.0;
		
		long startVerifyBlockOne = System.nanoTime();
		verificationOne.setupNodes(numNodes, maliciousPercentage);
		verificationOne.simulateVerifyBlockOne(blockOne);
		end = System.nanoTime();
		double verifyBlockOne = (end - startVerifyBlockOne) / 1000000000.0;
		
		long startCreateBlockTwo = System.nanoTime();
		ResearcherBlockTwo blockTwo = new ResearcherBlockTwo(blockOne, blocksBefore, blocksAfter);
		researcher.secondBlocks.add(blockTwo);
		end = System.nanoTime();
		double createBlockTwo = (end - startCreateBlockTwo) / 1000000000.0;
		
		long startVerifyBlockTwo = System.nanoTime();
		verificationTwo.setupNodes(numNodes, maliciousPercentage);
		verificationTwo.simulateVerifyBlockTwo(blockTwo);
		end = System.nanoTime();
		double verifyBlockTwo = (end - startVerifyBlockTwo) / 1000000000.0;
		
		
		/*
		BlockNode[] blocksArr = new BlockNode[blockTwo.getNumBlocks()];
		for(int i = 0; i < blockTwo.getNumBlocks(); i++) {
			blocksArr[i] = BlockChain.ledger.get(i);
		}
		
		ResearcherBlockThree plaintextBlockThree = new ResearcherBlockThree(blockTwo, blocksArr);
		researcher.thirdBlocks.add(plaintextBlockThree);
		
		ArrayList<ResearcherBlockInformation> encryptedBlockThreeList = plaintextBlockThree.generateEncryption();
		*/
		end = System.nanoTime();
		double elapsedExperiment = (end - startCreateBlockOne) / 1000000000.0;
		
		System.out.println("\n\n\nThe researcher access process:"); 
		System.out.println("Creating block one: " + createBlockOne); 
		System.out.println("Verifying block one: " + verifyBlockOne); 
		System.out.println("Creating block two: " + createBlockTwo); 
		System.out.println("Verifying block two: " + verifyBlockTwo); 
		System.out.println("The whole experiment took " + elapsedExperiment + " seconds.");
	}
}
