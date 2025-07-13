package blockchain;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class experimentOneTester {
	public static void main(String[] args) {
		long startExperiment = System.nanoTime();
		int numHospitals = 2000;
		int numPatients = 10000;
		int numBlocks = 1; //number of blocks we are generating
		
		for(int i = 0; i < numHospitals; i++) {
			BlockChain.hospitals.add(BlockChain.generateHospital());
			System.out.println("Hospital number " + i);
		}
		
		for(int i = 0; i < numPatients; i++) {
			BlockChain.patients.add(BlockChain.generatePatient());
			System.out.println("Patient number " + i);
		}
		
		//This is where we start measuring the time
		long startBlockChain = System.nanoTime();
		BlockChain bc = new BlockChain(numBlocks); //change nanme of testing zkp
		long end = System.nanoTime();
		
		double elapsedAddBlock = (end - startBlockChain) / 1000000000.0;
		double elapsedExperiment = (end - startExperiment) / 1000000000.0;
		
		System.out.println("The block creation process for " + numPatients + " patients and " + numHospitals + " hospitals in the system to add " + 
				numBlocks + " blocks is " + elapsedAddBlock + " seconds, while the whole experiment took " + elapsedExperiment + " seconds.");
	}
}