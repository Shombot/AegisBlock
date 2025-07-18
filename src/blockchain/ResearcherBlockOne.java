package blockchain;

import java.security.PublicKey;
import java.util.Date;

import org.bouncycastle.util.encoders.Base64;

public class ResearcherBlockOne {
	public BlockNode patientBlock;
	public Date date;
	public Researcher researcher;
	public PublicKey researcherKey;
	public String researcherString;
	public Patient patient;
	public PublicKey patientKey;
	public String patientString;
	public int blocksBefore;
	public int blocksAfter;
	public int numBlocks;
	public String signature; //researcher signature on patient block
	
	/*
	 * Make sure to manually add this block to the researcher firstBlocks list
	 */
	public ResearcherBlockOne(Researcher researcher, BlockNode patientBlock, int blocksBefore, int blocksAfter) {
		date = new Date();
		this.patientBlock = patientBlock;
		setResearcher(researcher);
		setPatient(patientBlock.getPatient());
		this.blocksBefore = blocksBefore;
		this.blocksAfter = blocksAfter;
		this.numBlocks = this.blocksAfter + this.blocksBefore + 1;
		signature = BlockChain.signData(this.patientBlock.toString(), researcher.getPrivKey());
	}
	
	public static boolean verifyBlockOne(ResearcherBlockOne blockOne) {
		if(!blockOne.getPatient().equals(blockOne.getPatientBlock().getPatient())) {
			System.out.println("Patients do not match on block 1 and patient block");
			return false;
		}
		
		if(!BlockChain.verifySignature(blockOne.getPatientBlock().toString(), blockOne.getSignature(), blockOne.getResearcherKey())) {
			System.out.println("Signatures do not match on block 1 and patient block");
			return false;
		}
		
		if(blockOne.getBlocksAfter() < 0 || blockOne.getBlocksBefore() < 0 || blockOne.getPatientBlock().getDate().compareTo(blockOne.getDate()) > 0) {
			System.out.println("Blocks after or before is negative, or blockOne is created before patient block");
			return false;
		}
		
		return true;
	}

	public Researcher getResearcher() {
		return researcher;
	}

	public void setResearcher(Researcher researcher) {
		this.researcher = researcher;
		setResearcherKey(this.researcher.getPubKey()); //sets string as well
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
		setPatientKey(this.patient.getPubKey()); //sets string as well
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
		return researcher.getPubKey();
	}

	public void setResearcherKey(PublicKey researcherKey) {
		this.researcherKey = researcherKey;
		setResearcherString(generateStringFromPublicKey(researcherKey)); //makes sure the string is always updated
	}

	public String getResearcherString() {
		return generateStringFromPublicKey(researcher.getPubKey());
	}

	private void setResearcherString(String researcherString) {
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
		return generateStringFromPublicKey(patient.getPubKey());
	}

	private void setPatientString(String patientString) {
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

	public int getNumBlocks() {
		return numBlocks;
	}

	public void setNumBlocks(int numBlocks) {
		this.numBlocks = numBlocks;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
