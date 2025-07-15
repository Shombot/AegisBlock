package blockchain;

import java.security.PublicKey;
import java.util.Date;

import org.bouncycastle.util.encoders.Base64;

public class ResearcherBlockOne {
	public BlockNode patientBlock;
	public Date date;
	public PublicKey researcherKey;
	public String researcherString;
	public PublicKey patientKey;
	public String patientString;
	public int blocksBefore;
	public int blocksAfter;
	public String signature; //researcher signature on patient block
	
	public ResearcherBlockOne(PublicKey researcher, BlockNode patientBlock, int blocksBefore, int blocksAfter) {
		this.patientBlock = patientBlock;
		setResearcherKey(researcher);
		setPatientKey(patientBlock.getPatient().getPubKey());
		this.blocksBefore = blocksBefore;
		this.blocksAfter = blocksAfter;
		//add signature and date
	}
	
	public String generateStringFromPublicKey(PublicKey pubKey) {
		return new String(Base64.encode(pubKey.getEncoded())); 
	}
	
	public BlockNode getPatientBlock() {
		return patientBlock;
	}

	public void setPatientBlock(BlockNode patientBlock) {
		this.patientBlock = patientBlock;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public PublicKey getResearcherKey() {
		return researcherKey;
	}

	public void setResearcherKey(PublicKey researcherKey) {
		this.researcherKey = researcherKey;
		setResearcherString(generateStringFromPublicKey(researcherKey)); //makes sure the string is always updated
	}

	public String getResearcherString() {
		return researcherString;
	}

	public void setResearcherString(String researcherString) {
		this.researcherString = researcherString;
	}

	public PublicKey getPatientKey() {
		return patientKey;
	}

	public void setPatientKey(PublicKey patientKey) {
		this.patientKey = patientKey;
		setPatientString(generateStringFromPublicKey(patientKey)); //makes sure the string is always updated
	}

	public String getPatientString() {
		return patientString;
	}

	public void setPatientString(String patientString) {
		this.patientString = patientString;
	}

	public int getBlocksBefore() {
		return blocksBefore;
	}

	public void setBlocksBefore(int blocksBefore) {
		this.blocksBefore = blocksBefore;
	}

	public int getBlocksAfter() {
		return blocksAfter;
	}

	public void setBlocksAfter(int blocksAfter) {
		this.blocksAfter = blocksAfter;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
