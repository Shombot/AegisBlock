package blockchain;

import java.util.Date;
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
	public BlockHeader header;
	public BlockBody body;
	private String hash = null; //H(symK || ptr(D) || H(D) || H(n-1))
	private String salt;
	public String safeHash; //H(H(n) || salt)
	public int prevBlock;
	
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
	}
	
	//WRITE THIS FUNCTION
	public boolean verifyBlockNode() {
		BlockHeader header = this.getHeader();
		BlockBody body = this.getBody();
		
		//hospital signature is valid
		if(!BlockChain.verifySignature(this.hash, header.getHospitalSign(), this.getHospital().getPubKey())) {
			System.out.println("Hospital signature does not match");
			return false;
		}
		
		//patient signature is valid
		if(!BlockChain.verifySignature(this.hash, header.getPatientSign(), this.getPatient().getPubKey())) {
			System.out.println("Patient signature does not match");
			return false;
		}
		
		//hash is correctly done
		if(hash != this.getHash()) {
			System.out.println("Hash does not match");
			return false;
		}
		
		if(header.getPrevBlock() < -1 || header.getPrevBlock() >= BlockChain.ledger.size()) {
			System.out.println("Prev block is invalid");
			return false;
		}
		
		
		return true;
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
			hash = generateHash();
		}
		return hash;
	}
	
	/*
	 * Hash(n) = Hash(symK + ptr(data) + Hash(D) + Hash(n - 1));
	 */
	public String generateHash() {
		if(hash != null) {
			return hash;
		}

		String prevHashPtr;
		if(prevBlock == -1) {
			prevHashPtr = UUID.randomUUID().toString();
		} else {
			prevHashPtr = BlockChain.ledger.get(prevBlock).getHash(); 
		}
		
		hash = this.getBody().getSymmetricString() + this.getBody().getPatient().getPtrData()
				+ this.getBody().getHashData() + prevHashPtr;
		
		salt = BlockChain.generateRandomSHA256();
		
		hash = BlockChain.hash(hash);
		setSafeHash(hash, salt);
		return hash;
	}
	
	public String getSafeHash() {
		return safeHash;
	}

	public void setSafeHash(String hash, String salt) {
		this.safeHash = BlockChain.hash(hash + salt);
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
	
	public Date getDate() {
		return header.getDate();
	}
	
	public void setDate(Date date) {
		header.setDate(date);
	}

	public String getSalt() {
		return salt;
	}

	public int getPrevBlock() {
		return prevBlock;
	}

	public void setPrevBlock(int prevBlock) {
		this.prevBlock = prevBlock;
	}
}
