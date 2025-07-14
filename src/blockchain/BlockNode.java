package blockchain;

import java.util.UUID;

import examples.AND_Fiat_Shamir_AABProverBasicDLSchnorrANDExample;
import examples.OR_Fiat_Shamir_AADProverBasicECSchnorrORExample;
import zero_knowledge_proofs.CryptoData.CryptoData;
import zero_knowledge_proofs.CryptoData.CryptoDataArray;

/*
 * HEADER
 * hash of this block (hash of everything besides this part) h
 * pointer to previous block (to establish linked list structure) h
 * ZKP patient signature h
 * ZKP hospital signature h
 * date d
 * 
 * BLOCK
 * pointer to data, encrypted by symK b
 * symK encrypted by groupK b
 * HashPointer to the previous block for this patient, encrypted with the new public key b
 * Patient new public key b
 * Hospital new public key b
 * hash of the unencrypted off chain data for this block b
*/

public class BlockNode {
	private BlockHeader header;
	private BlockBody body;
	private String hash = null; //H(H(header) + H(body))
	private String salt;
	int prevBlock;
	
	public BlockNode(String conditionCodes, String data, String ptrData, String hashPtrPrevBlock, int prevBlock) throws Exception {
		while(true) {
			try {
				body = new BlockBody(conditionCodes, data, ptrData, hashPtrPrevBlock);
				this.prevBlock = prevBlock;
				break;
			} catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		
		String[] args = {"5001"};
		int n_patient;
		int i_real_patient;
		int n_hospital;
		int i_real_hospital;
		CryptoData[] key1 = new CryptoDataArray[2];
		CryptoData[] key2 = new CryptoDataArray[2];
		
		//patient proof
		n_patient = BlockChain.numPatients; //patients is the OR
		i_real_patient = (int) (Math.random() * n_patient);
		
		//System.out.println(n_patient + " " + i_real_patient);
		CryptoData[] patientOr = OR_Fiat_Shamir_AADProverBasicECSchnorrORExample.prover(n_patient, i_real_patient);
		key1[0] = new CryptoDataArray(patientOr);
		
		CryptoData[] patientAND = AND_Fiat_Shamir_AABProverBasicDLSchnorrANDExample.prover(args, 2); //AND	call;
		key1[1] = new CryptoDataArray(patientAND);
		//System.out.println("patient proof works");
		
		
		
		//hospital proof
		n_hospital = BlockChain.numHospitals; //hospitals is the OR
		i_real_hospital = (int) (Math.random() * n_hospital);
		
		//System.out.println(n_hospital + " " + i_real_hospital);
		CryptoData[] hospitalOr = OR_Fiat_Shamir_AADProverBasicECSchnorrORExample.prover(n_hospital, i_real_hospital);
		key2[0] = new CryptoDataArray(hospitalOr);
		
		CryptoData[] hospitalAND = AND_Fiat_Shamir_AABProverBasicDLSchnorrANDExample.prover(args, 2);; //AND call;
		key2[1] = new CryptoDataArray(hospitalAND);
		//System.out.println("hospital proof works");
		
		hash = getHash(); //makes sure the hash cannot be null
		String patientSign = BlockChain.signData(hash, this.getPatient().getPrivKey());
		String hospitalSign = BlockChain.signData(hash, this.getHospital().getPrivKey());
		
		header = new BlockHeader(key1, key2, prevBlock, patientSign, hospitalSign);
		
		
		
		/*
		
		String headerHash = BlockChain.hash(header.toString());
		String bodyHash = BlockChain.hash(body.toString());
		
		hash = BlockChain.hash(headerHash + bodyHash);
		
		if(BlockChain.ledger.size() == 0) {
			header.setPtrRegular(this.getHashPointer()); //hash this block itself if this is the first one 
		} else {
			header.setPtrRegular(BlockChain.ledger.getLast().getHashPointer());
		} */
	}

	public BlockHeader getHeader() {
		return header;
	}

	public void setHeader(BlockHeader header) {
		this.header = header;
	}

	public BlockBody getBody() {
		return body;
	}

	public void setBody(BlockBody body) {
		this.body = body;
	}

	public String getHash() {
		if(hash == null) {
			hash = generateHashPointer();
		}
		return hash;
	}
	
	/*
	 * Hash(n) = Hash(symK + ptr(data) + Hash(n - 1) + salt);
	 */
	public String generateHashPointer() {
		if(hash != null) {
			return hash;
		}

		String prevHashPtr;
		if(prevBlock == -1) {
			prevHashPtr = UUID.randomUUID().toString();
		} else {
			prevHashPtr = BlockChain.ledger.get(prevBlock).getHash(); 
		}
		
		String preHash = this.getBody().getSymmetricString() + this.getBody().getPatient().getPtrData() + prevHashPtr;
		salt = BlockChain.generateRandomSHA256();
		String hash = preHash + salt;
		
		hash = BlockChain.hash(hash);
		return hash;
	}
	
	public Patient getPatient() {
		return getBody().getPatient();
	}
	
	public void setPatient(Patient patient) {
		getBody().setPatient(patient);
	}
	
	public Hospital getHospital() {
		return getBody().getHospital();
	}
	
	public void setHospital(Hospital hospital) {
		getBody().setHospital(hospital);
	}
}
