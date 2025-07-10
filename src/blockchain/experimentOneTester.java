package blockchain;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class experimentOneTester {
	public static void main(String[] args) {
		String data;
		String dataPtr;
		String prevDataHashPtr;
		int numHospitals = 10;
		int numPatients = 10;
		int numBlocks = 10; //number of blocks we are generating
		
		
		//generate a random block for hashptrprevdata and hash it, rather than making it random 
			//this is for experiment 1 only, can make a different setup for experiment 3 
		//fix the ZKP OR and AND to make sure they work together //dont need to do this anymore
		//chanmge blockchain ledger away from linked list (linked list has pointer to next, we need pointer to prev and not next)
		for(int i = 0; i < numPatients; i++) {
			BlockChain.patients.add(BlockChain.generatePatient());
		}
		
		for(int i = 0; i < numHospitals; i++) {
			BlockChain.hospitals.add(BlockChain.generateHospital());
		}
		
		//This is where we start measuring the time
		long start = System.nanoTime();
		BlockChain bc = new BlockChain(numBlocks); //change nanme of testing zkp
		long end = System.nanoTime();
		
		double elapsed = (end - start) / 1000000000.0;
		
		System.out.println("The block creation process for " + numPatients + " patients and " + numHospitals + " hospitals "
				+ "in the system to add " + numBlocks + " blocks is " + elapsed + " seconds");
	}
}