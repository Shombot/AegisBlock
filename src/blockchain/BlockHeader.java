package blockchain;

import java.util.Date;

import zero_knowledge_proofs.CryptoData.CryptoData;

public class BlockHeader {
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
	
	//maybe add a link to the patient and hospital
	public String ptrRegular; //hash | BlockNode.toString()
	public CryptoData[][] ZKPPatient;
	public CryptoData[][] ZKPHospital;
	public Date date;
	public int prevBlock;
	public String patientSign;
	public String hospitalSign; 

	//There is no ptrRegular since we add this when we are mining
	public BlockHeader(CryptoData[][] ZKPPatient, CryptoData[][] ZKPHospital, int prevBlock, String patientSign, String hospitalSign) {
		this(ZKPPatient, ZKPHospital, prevBlock);
		this.patientSign = patientSign;
		this.hospitalSign = hospitalSign;
	}
	
	//Does NOT sign for patients and hospitals
	public BlockHeader(CryptoData[][] ZKPPatient, CryptoData[][] ZKPHospital, int prevBlock) {
		this.ZKPPatient = ZKPPatient;
		this.ZKPHospital = ZKPHospital;
		date = new Date();
		this.prevBlock = prevBlock;
	}

	public String getPtrRegular() {
		return ptrRegular;
	}

	public void setPtrRegular(String ptrRegular) {
		this.ptrRegular = ptrRegular;
	}

	public CryptoData[][] getZKPPatient() {
		return ZKPPatient;
	}

	public void setZKPPatient(CryptoData[][] ZKPPatient) {
		this.ZKPPatient = ZKPPatient;
	}

	public CryptoData[][] getZKPHospital() {
		return ZKPHospital;
	}

	public void setZKPHospital(CryptoData[][] ZKPHospital) {
		this.ZKPHospital = ZKPHospital;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getPatientSign() {
		return patientSign;
	}
	
	public void setPatientSign(String patientSign) {
		this.patientSign = patientSign;
	}
	
	public String getHospitalSign() {
		return hospitalSign;
	}
	
	public void sethospitalSign(String hospitalSign) {
		this.hospitalSign = hospitalSign;
	}

	public int getPrevBlock() {
		return prevBlock;
	}

	public void setPrevBlock(int prevBlock) {
		this.prevBlock = prevBlock;
	}

	public void setHospitalSign(String hospitalSign) {
		this.hospitalSign = hospitalSign;
	}
}
