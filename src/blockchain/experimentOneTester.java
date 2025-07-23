package blockchain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import zero_knowledge_proofs.CryptoData.CryptoData;
//import org.openjdk.jol.info.GraphLayout;


public class experimentOneTester {
	public static void main(String[] args) {
		long startExperiment = System.nanoTime();
		int numHospitals = 6_000;
		int numPatients = 1_000_000;
		int numBlocks = 1; //number of blocks we are generating
		

		for(int i = 0; i < 5; i++) {
			//takes generates about 3000 a second, doing the 50M test case would take about 5 hours. overhead now is less than a second
			//earlier when generating blocks manually, it could do about 13 a second, or about 5-6 weeks (1200 hours)
			BlockChain.numHospitals = numHospitals;
			BlockChain.numPatients = numPatients;
			
			//This is where we start measuring the time
			System.gc();
			long startBlockChain = System.nanoTime();
			BlockChain bc = new BlockChain(numBlocks); //change nanme of testing zkp
			long end = System.nanoTime();
			
			double elapsedAddBlock = (end - startBlockChain) / 1000000000.0;
			double elapsedExperiment = (end - startExperiment) / 1000000000.0;


			BlockHeader header = BlockChain.ledger.get(BlockChain.ledger.size() - 1).getHeader();
			CryptoData[][] ZKPHospital = header.getZKPHospital();
			CryptoData[][] ZKPPatient = header.getZKPPatient();
			
			int ZKPHospitalSize = 0;
			int ZKPPatientSize = 0;
					
			for(int j = 0; j < 2; j++) {
			    for(int k = 0; k < 4; k++) {
			        try {
			            // Serialize hospital ZKP
			            String nameHospital = "ZKPHospital_" + j + "_" + k + ".ser";
			            try (ObjectOutputStream oosH = new ObjectOutputStream(new FileOutputStream(nameHospital))) {
			                oosH.writeObject(ZKPHospital[j][k]);
			            }
			            ZKPHospitalSize += Files.size(Paths.get(nameHospital));

			            // Serialize patient ZKP
			            String namePatient = "ZKPPatient_" + j + "_" + k + ".ser";
			            try (ObjectOutputStream oosP = new ObjectOutputStream(new FileOutputStream(namePatient))) {
			                oosP.writeObject(ZKPPatient[j][k]);
			            }
			            ZKPPatientSize += Files.size(Paths.get(namePatient));
			        } catch(Exception e) {
			            //e.printStackTrace();
			        }
			    }
			}

	        
			
			System.out.println("The block creation process for " + numPatients + " patients and " + numHospitals + " hospitals in the system to add " + 
					numBlocks + " blocks is " + elapsedAddBlock + " seconds, while the whole experiment took " + elapsedExperiment + " seconds. \nThe "
							+ "ZKPPatient file size is " + ZKPPatientSize + " bytes, while hospital is " + ZKPHospitalSize + " bytes.\n\n");
			
			numPatients += 1_000_000;
			System.gc();
		}
			
		
		
		//generating manually (takes too long)
		/*for(int i = 0; i < numHospitals; i++) {
			BlockChain.hospitals.add(BlockChain.generateHospital());
			System.out.println("Hospital number " + i);
		}
		
		for(int i = 0; i < numPatients; i++) {
			BlockChain.patients.add(BlockChain.generatePatient());
			System.out.println("Patient number " + i);
		}*/
		
	}
}