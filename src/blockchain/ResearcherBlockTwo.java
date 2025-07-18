package blockchain;

import java.util.Date;

public class ResearcherBlockTwo {
	public ResearcherBlockOne blockOne;
	public Date date;
	public Researcher researcher;
	public Patient patient;
	public String patientSignature; //signing blockOne
	public int blocksBefore;
	public int blocksAfter;
	public int numBlocks;
	
	public ResearcherBlockTwo(ResearcherBlockOne blockOne, int blocksBefore, int blocksAfter) {
		date = new Date();
		this.blockOne = blockOne;
		this.blocksBefore = blocksBefore;
		this.blocksAfter = blocksAfter;
		this.numBlocks = this.blocksAfter + this.blocksBefore + 1;
		this.patient = this.blockOne.getPatient();
		this.researcher = this.blockOne.getResearcher();
		patientSignature = BlockChain.signData(this.blockOne.toString(), this.patient.getPrivKey());
	}
	
	/*
	 * Make sure the blockOne is verified already
	 */
	public static boolean verifyBlockTwo(ResearcherBlockTwo blockTwo) {
		if(blockTwo.getBlocksBefore() > blockTwo.getBlockOne().getBlocksBefore() || blockTwo.getBlocksAfter() > blockTwo.getBlockOne().getBlocksAfter()) {
			System.out.println("Patient cannot allow more of a range than researcher");
			return false;
		}
		
		if(!BlockChain.verifySignature(blockTwo.getBlockOne().toString(), blockTwo.getPatientSignature(), blockTwo.getPatient().getPubKey())) {
			System.out.println("Signatures do not match on block 2 and block 1");
			return false;
		}
		
		if(!blockTwo.getPatient().equals(blockTwo.getBlockOne().getPatient()) || !blockTwo.getResearcher().equals(blockTwo.getBlockOne().getResearcher())) {
			System.out.println("Patient signing block 2 isn't the same as the one on block 1");
			return false;
		}
		
		if(blockTwo.getDate().compareTo(blockTwo.getBlockOne().getDate()) < 0) {
			System.out.println("Date for block 2 cannot be before block 1");
			return false;
		}
		
		return true;
	}

	public ResearcherBlockOne getBlockOne() {
		return blockOne;
	}

	public void setBlockOne(ResearcherBlockOne blockOne) {
		this.blockOne = blockOne;
		setPatient(this.blockOne.getPatient());
		setResearcher(this.blockOne.getResearcher());
		setPatientSignature(BlockChain.signData(this.blockOne.toString(), patient.getPrivKey()));
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Researcher getResearcher() {
		return researcher;
	}

	public void setResearcher(Researcher researcher) {
		this.researcher = researcher;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getPatientSignature() {
		return patientSignature;
	}

	public void setPatientSignature(String patientSignature) {
		if(patientSignature.equals(BlockChain.signData(this.blockOne.toString(), this.patient.getPrivKey()))) {
			System.out.println("Signature inputted manually does not match actual signature");
			return;
		}
		this.patientSignature = patientSignature;
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
}
