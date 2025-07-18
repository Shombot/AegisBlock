package blockchain;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ResearcherBlockInformation {
	public SecretKey symK;
	public String symString;
	public String ptrData;
	public String hashData;
	public BlockNode block;
	public String hashPrev;
	private String salt;
	public int position;
	public Researcher researcher;
	
	/*
	 * Encrypted conversion given ResearcherBlockInformation of unencrypted data
	 */
	public ResearcherBlockInformation(ResearcherBlockInformation block, int position, Researcher researcher) {
		this.position = position;
		while(true) {
			try {
				if(position == -1) {
					this.hashPrev = block.getHashPrev();
				} else if(position == 1) {
					this.salt = publicKeyEncryption(block.getSalt(), researcher.getPubKey());
				}
				
				this.symString = publicKeyEncryption(BlockChain.generateStringFromSymmetricKey
						(block.getSymK()), researcher.getPubKey());
				this.ptrData = publicKeyEncryption(block.getPtrData(), researcher.getPubKey());
				this.hashData = publicKeyEncryption(block.getHashData(), researcher.getPubKey());
				break;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static String publicKeyEncryption(String plaintext, PublicKey publicKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return BlockChain.publicKeyEncryption(plaintext, publicKey);
	}
	
	public ResearcherBlockInformation(BlockNode block, int position, Researcher researcher) {
		this.researcher = researcher;
		this.block = block;
		this.symK = block.getBody().getSymmetricKey();
		this.symString = BlockChain.generateStringFromSymmetricKey(symK);
		this.position = position;
		while(true) {
			try {
				this.ptrData = BlockChain.symmetricKeyDecryption(block.getBody().getSymKEncPtrData(), this.symK);
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.hashData = block.getBody().getHashData();
		
		if(position == -1) {
			this.hashPrev = block.getBody().getHashPrevPatient();
		} else if(position == 1) {
			this.salt = block.getSalt();
		}
	}

	public SecretKey getSymK() {
		return symK;
	}

	public void setSymK(SecretKey symK) {
		this.symK = symK;
	}

	public String getPtrData() {
		return ptrData;
	}

	public void setPtrData(String ptrData) {
		this.ptrData = ptrData;
	}

	public String getHashData() {
		return hashData;
	}

	public void setHashData(String hashData) {
		this.hashData = hashData;
	}

	public BlockNode getBlock() {
		return block;
	}

	public void setBlock(BlockNode block) {
		this.block = block;
	}

	public String getHashPrev() {
		return hashPrev;
	}

	public void setHashPrev(String hashPrev) {
		this.hashPrev = hashPrev;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSymString() {
		return symString;
	}

	public void setSymString(String symString) {
		this.symString = symString;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Researcher getResearcher() {
		return researcher;
	}

	public void setResearcher(Researcher researcher) {
		this.researcher = researcher;
	}
}
