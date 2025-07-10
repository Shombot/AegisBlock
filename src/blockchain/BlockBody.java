package blockchain;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.Security;

import blockchain.BlockChain;


/*
 * HEADER
 * hash of this block (hash of everything besides this part) BlockNode
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
 * condition codes b
*/
public class BlockBody {
	private String symKEncPtrData;
	private String groupKEncSymK;
	private String pubKeyEncHashPtrPrevBlock;
	private Patient patient;
	private Hospital hospital;
	private String hashData;
	private SecretKey symmetricKey;
	private String symmetricString;
	private String conditionCodes; //256 bit string, which when represented as binary, each 1 signifies the patient has a condition that the specific bit represents
	
	public BlockBody(String conditionCodes, String data, String ptrData, String hashPtrPrevBlock) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		setConditionCodes(conditionCodes);
		setPatient(new Patient(data, ptrData, hashPtrPrevBlock));
		setHospital(new Hospital(data, ptrData, hashPtrPrevBlock));
		hashData = BlockChain.hash(data);
		
		//only time we do not make new keys are if the keys are equal and neither are null
		if(!((patient.getSymmetricKey() == hospital.getSymmetricKey()) && (patient.getSymmetricKey() != null))) {
			setSymmetricKey(BlockChain.symmetricKeyGenerator());
		}
		
		//take the first 3 fields and encrypt them here. also deal with the hashData field. 
		//need data, ptrData, hashPtrPrevBlock
		setSymKEncPtrData(BlockChain.symmetricKeyEncryption(ptrData, symmetricKey)); 
		setGroupKEncSymK(BlockChain.publicKeyEncryption(getSymmetricString(), BlockChain.groupKey)); //this is where we have an error
		setPubKeyEncHashPtrPrevBlock(BlockChain.publicKeyEncryption(hashPtrPrevBlock, patient.getPubKey()));
	}
	
	public BlockBody(Patient patient, Hospital hospital, String conditionCodes, String data, String ptrData, String hashPtrPrevBlock) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		setConditionCodes(conditionCodes);
		setPatient(patient);
		setHospital(hospital);
		hashData = BlockChain.hash(data);
		
		//only time we do not make new keys are if the keys are equal and neither are null
		if(!((patient.getSymmetricKey() == hospital.getSymmetricKey()) && (patient.getSymmetricKey() != null))) {
			setSymmetricKey(BlockChain.symmetricKeyGenerator());
		}
		
		//take the first 3 fields and encrypt them here. also deal with the hashData field. 
		//need data, ptrData, hashPtrPrevBlock
		
		setSymKEncPtrData(BlockChain.symmetricKeyEncryption(ptrData, symmetricKey));
		setGroupKEncSymK(BlockChain.publicKeyEncryption(getSymmetricString(), BlockChain.groupKey));
		setPubKeyEncHashPtrPrevBlock(BlockChain.publicKeyEncryption(hashPtrPrevBlock, patient.getPubKey()));
	}

	public String getSymKEncPtrData() {
		return symKEncPtrData;
	}

	public void setSymKEncPtrData(String symKEncPtrData) {
		this.symKEncPtrData = symKEncPtrData;
	}

	public String getGroupKEncSymK() {
		return groupKEncSymK;
	}

	public void setGroupKEncSymK(String groupKEncSymK) {
		this.groupKEncSymK = groupKEncSymK;
	}

	public String getPubKeyEncHashPtrPrevBlock() {
		return pubKeyEncHashPtrPrevBlock;
	}

	public void setPubKeyEncHashPtrPrevBlock(String pubKeyEncHashPtrPrevBlock) {
		this.pubKeyEncHashPtrPrevBlock = pubKeyEncHashPtrPrevBlock;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public String getHashData() {
		return hashData;
	}

	public void setHashData(String hashData) {
		this.hashData = hashData;
	}

	public SecretKey getSymmetricKey() {
		return symmetricKey;
	}
	
	//updates patient and hospital's symmetric keys as well
	public void setSymmetricKey(SecretKey symmetricKey) {
		this.symmetricKey = symmetricKey;
		symmetricString = BlockChain.generateStringFromSymmetricKey(symmetricKey);
		patient.setSymmetricKey(symmetricKey);
		hospital.setSymmetricKey(symmetricKey);
	}

	public String getSymmetricString() {
		return symmetricString;
	}

	public void setSymmetricString(String symmetricString) {
		this.symmetricString = symmetricString;
	}

	public String getConditionCodes() {
		return conditionCodes;
	}

	public void setConditionCodes(String conditionCodes) {
		this.conditionCodes = conditionCodes;
	}
}
