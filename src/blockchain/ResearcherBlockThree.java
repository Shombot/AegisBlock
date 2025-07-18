package blockchain;

import java.util.ArrayList;

public class ResearcherBlockThree {
	public ResearcherBlockTwo blockTwo;
	public Researcher researcher;
	public Patient patient;
	private ArrayList<ResearcherBlockInformation> blocksInfo;
	public ArrayList<ResearcherBlockInformation> encryptedInfo;
	public int numBlocks;
	
	public ResearcherBlockThree(ResearcherBlockTwo blockTwo) {
		this.blockTwo = blockTwo;
		patient = this.blockTwo.getPatient();
		researcher = this.blockTwo.getResearcher();
		numBlocks = blockTwo.getNumBlocks();
		blocksInfo = new ArrayList<>(numBlocks); 
		encryptedInfo = new ArrayList<>(numBlocks);
	}
	
	public ResearcherBlockThree(ResearcherBlockTwo blockTwo, BlockNode[] nodes) {
		this.blockTwo = blockTwo;
		patient = this.blockTwo.getPatient();
		researcher = this.blockTwo.getResearcher();
		numBlocks = this.blockTwo.getBlocksAfter() + this.blockTwo.getBlocksBefore() + 1;
		blocksInfo = new ArrayList<>(numBlocks); 
		encryptedInfo = new ArrayList<>(numBlocks);
		
		for(int i = 0; i < numBlocks; i++) {
			BlockNode node = nodes[i];
			int position = 0;
			if(i == 0 || i == numBlocks -1) {
				position = (i == 0) ? -1 : 1;
			}
			ResearcherBlockInformation tempInfo = new ResearcherBlockInformation(node, position, researcher);
			blocksInfo.add(tempInfo);
		}
	}
	
	public ResearcherBlockThree(ResearcherBlockTwo blockTwo, ArrayList<BlockNode> nodes) {
		int numBlocks = this.blockTwo.getBlocksAfter() + this.blockTwo.getBlocksBefore() + 1;
		if(nodes.size() != numBlocks) {
			System.out.println("Number of blocks in the Research block 3 array list parameter do not match approved amount");
			return;
		}
		this.blockTwo = blockTwo;
		patient = this.blockTwo.getPatient();
		researcher = this.blockTwo.getResearcher();
		blocksInfo = new ArrayList<>(numBlocks); 
		encryptedInfo = new ArrayList<>(numBlocks);
		
		for(int i = 0; i < numBlocks; i++) {
			BlockNode node = nodes.get(i);
			int position = 0;
			if(i == 0 || i == numBlocks -1) {
				position = (i == 0) ? -1 : 1;
			}
			ResearcherBlockInformation tempInfo = new ResearcherBlockInformation(node, position, researcher);
			blocksInfo.add(tempInfo);
		}
	}
	
	public ArrayList<ResearcherBlockInformation> generateEncryption() {
		if(blocksInfo.size() != numBlocks) {
			System.out.println("Make sure the size matches");
			return null;
		}
		
		for(ResearcherBlockInformation block : blocksInfo) {
			encryptedInfo.add(new ResearcherBlockInformation(block, block.getPosition(), researcher));
		}
		return encryptedInfo;
	}

	public ResearcherBlockTwo getBlockTwo() {
		return blockTwo;
	}

	public void setBlockTwo(ResearcherBlockTwo blockTwo) {
		this.blockTwo = blockTwo;
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

	public ArrayList<ResearcherBlockInformation> getBlocksInfo() {
		return blocksInfo;
	}

	public void setBlocksInfo(ArrayList<ResearcherBlockInformation> blocksInfo) {
		this.blocksInfo = blocksInfo;
	}

	public ArrayList<ResearcherBlockInformation> getEncryptedInfo() {
		return encryptedInfo;
	}

	public void setEncryptedInfo(ArrayList<ResearcherBlockInformation> encryptedInfo) {
		this.encryptedInfo = encryptedInfo;
	}

	public int getNumBlocks() {
		return numBlocks;
	}

	public void setNumBlocks(int numBlocks) {
		this.numBlocks = numBlocks;
	}
}
