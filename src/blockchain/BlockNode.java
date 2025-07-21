package blockchain;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import examples.AND_Fiat_Shamir_AABProverBasicDLSchnorrANDExample;
import examples.OR_Fiat_Shamir_AADProverBasicECSchnorrORExample;
import zero_knowledge_proofs.ArraySizesDoNotMatchException;
import zero_knowledge_proofs.DLSchnorrProver;
import zero_knowledge_proofs.ECSchnorrProver;
import zero_knowledge_proofs.MultipleTrueProofException;
import zero_knowledge_proofs.NoTrueProofException;
import zero_knowledge_proofs.ZKPProtocol;
import zero_knowledge_proofs.ZeroKnowledgeAndProver;
import zero_knowledge_proofs.ZeroKnowledgeOrProver;
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
		CryptoData[][] key1 = new CryptoData[2][4];
		CryptoData[][] key2 = new CryptoData[2][4];
		
		//patient proof
		n_patient = BlockChain.numPatients; //patients is the OR
		i_real_patient = (int) (Math.random() * n_patient);
		
		//System.out.println(n_patient + " " + i_real_patient);
		CryptoData[] patientOr = OR_Fiat_Shamir_AADProverBasicECSchnorrORExample.prover(n_patient, i_real_patient);
		key1[0] = patientOr;
		
		CryptoData[] patientAND = AND_Fiat_Shamir_AABProverBasicDLSchnorrANDExample.prover(args, 2); //AND	call;
		key1[1] = patientAND;
		//System.out.println("patient proof works");
		
		
		
		//hospital proof
		n_hospital = BlockChain.numHospitals; //hospitals is the OR
		i_real_hospital = (int) (Math.random() * n_hospital);
		
		//System.out.println(n_hospital + " " + i_real_hospital);
		CryptoData[] hospitalOr = OR_Fiat_Shamir_AADProverBasicECSchnorrORExample.prover(n_hospital, i_real_hospital);
		key2[0] = hospitalOr;
		
		CryptoData[] hospitalAND = AND_Fiat_Shamir_AABProverBasicDLSchnorrANDExample.prover(args, 2); //AND call;
		key2[1] = hospitalAND;
		//System.out.println("hospital proof works");
		
		hash = generateHash(); //makes sure the hash cannot be null
		String patientSign = BlockChain.signData(hash, this.getPatient().getPrivKey());
		String hospitalSign = BlockChain.signData(hash, this.getHospital().getPrivKey());
		
		header = new BlockHeader(key1, key2, prevBlock, patientSign, hospitalSign);
	}

	public boolean verifyBlockNode() throws Exception {
		BlockHeader header = this.getHeader();
		
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
		
		
		
		
		
		/*
		 * verify ZKPs down here
		 */
		
		//ZKPProtocol proofAND;
		ZKPProtocol[] inners;
		
		//setup patient OR
		ZKPProtocol[] innerProtocolsPatient = new ZKPProtocol[BlockChain.numPatients];
		Arrays.setAll(innerProtocolsPatient, i -> new ECSchnorrProver());
		ECPoint gUnwrappedP = ECNamedCurveTable.getParameterSpec("secp256k1").getG();
        ECCurve cUnwrappedP = gUnwrappedP.getCurve();
        BigInteger orderP = cUnwrappedP.getOrder();
		ZKPProtocol proof = new ZeroKnowledgeOrProver(innerProtocolsPatient, orderP);

		CryptoData[] patientOR = header.getZKPPatient()[0];
		if (!proof.verifyFiatShamir(patientOR[0], patientOR[1], patientOR[2], patientOR[3])) {
			System.out.println("ZKP Patient OR does not match");
			return false;
		}
		
		//setup for patient AND
		inners = new ZKPProtocol[BlockChain.numPatients];
		for(int i = 0; i < BlockChain.numPatients; i++) {
			inners[i] = new DLSchnorrProver();
		}
		//proofAND = new ZeroKnowledgeAndProver(inners);
		
		CryptoData[] patientAND = header.getZKPPatient()[0];
		if (!proof.verifyFiatShamir(patientAND[0], patientAND[1], patientAND[2], patientAND[3])) {
			System.out.println("ZKP Patient AND does not match");
			return false;
		}
		
		//setup for hospital OR
		ZKPProtocol[] innerProtocolsHospital = new ZKPProtocol[BlockChain.numHospitals];
		Arrays.setAll(innerProtocolsHospital, i -> new ECSchnorrProver());
		ECPoint gUnwrappedH = ECNamedCurveTable.getParameterSpec("secp256k1").getG();
        ECCurve cUnwrappedH = gUnwrappedH.getCurve();
        BigInteger orderH = cUnwrappedH.getOrder();
		proof = new ZeroKnowledgeOrProver(innerProtocolsHospital, orderH);
		
		CryptoData[] hospitalOR = header.getZKPHospital()[0];
		if (!proof.verifyFiatShamir(hospitalOR[0], hospitalOR[1], hospitalOR[2], hospitalOR[3])) {
			System.out.println("ZKP Hospital OR does not match");
			return false;
		}
		
		
		CryptoData[] hospitalAND = header.getZKPHospital()[0];
		if (!proof.verifyFiatShamir(hospitalAND[0], hospitalAND[1], hospitalAND[2], hospitalAND[3])) {
			System.out.println("ZKP Hospital AND does not match");
			return false;
		}
		
		
		return true;
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
			prevHashPtr = BlockChain.ledger.get(prevBlock).getSafeHash(); 
		}
		
		hash = this.getBody().getSymmetricString() + this.getBody().getPatient().getPtrData()
				+ this.getBody().getHashData() + prevHashPtr;
		
		salt = BlockChain.generateRandomSHA256();
		
		hash = BlockChain.hash(hash);
		setSafeHash(hash, salt);
		return hash;
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

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setSalt(String salt) {
		this.salt = salt;
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
