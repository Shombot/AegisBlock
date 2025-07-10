package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import examples.Working_AADProverBasicECSchnorrORExample;
import examples.Working_AADVerifierBasicECSchnorrORExample;

public class BlockChain {
	public static PublicKey groupKey = groupKeyGen();
	private static PrivateKey groupPrivateKey;
	public static final HashSet<Patient> patients = new HashSet<>();
	public static final HashSet<Hospital> hospitals = new HashSet<>();
	public static LinkedList<BlockNode> ledger = new LinkedList<>();
	
	public BlockChain(int n) {
		for(int i = 0; i < n; i++) {
			generateBlockNode();
		}
	}
	
	public static String hash(String input) {
        MessageDigest digest;
        while(true) {
        	try {
    			digest = MessageDigest.getInstance("SHA-256");
    			break;
    		} catch (NoSuchAlgorithmException e) {
    			e.printStackTrace();
    			continue;
    		}
        }
		
        byte[] hashBytes = digest.digest((input).getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encode(hashBytes)); 
    }
	
	public static String hashPointer(BlockNode blockNode) {
		return blockNode.getHash() + " " + blockNode.toString();
	}
	
	// Encrypt a string and return Base64-encoded ciphertext using BouncyCastle Base64
    public static String publicKeyEncryption(String plaintext, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        byte[] encrypted = encryptWithPublicKey(plaintext.getBytes(StandardCharsets.UTF_8), publicKey);
        return new String(Base64.encode(encrypted), StandardCharsets.UTF_8);
    }
    
    // Encrypt plaintext bytes with RSA public key
    public static byte[] encryptWithPublicKey(byte[] plaintext, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        return c.doFinal(plaintext);
    }
    
    public static String decryptWithPrivateKey(String encryption, PrivateKey privateKey) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] ciphertextBytes = Base64.decode(encryption.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    public static String symmetricKeyEncryption(String plaintext, SecretKey key) {
    	byte[] encryptedBytes;
    	while(true) {    
	    	try {
	        	Cipher cipher = Cipher.getInstance("AES");
	            cipher.init(Cipher.ENCRYPT_MODE, key);
	            encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
	            break;
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	continue;
	        }
	    }
        return new String(Base64.encode(encryptedBytes), StandardCharsets.UTF_8);
    }
    
    public static String symmetricKeyDecryption(String encryption, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.decode(encryption.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    public static SecretKey symmetricKeyGenerator() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator kg = KeyGenerator.getInstance("AES");
		kg.init(256);
		
		SecretKey skTemp = kg.generateKey();
		return skTemp;
	}
    
    public static String generateStringFromSymmetricKey(SecretKey symmetricKey) {
		return new String(Base64.encode(symmetricKey.getEncoded()));
	}
    
    public static KeyPair genKeys() {
        KeyPairGenerator keyGen;
        while(true) {
			try {
				keyGen = KeyPairGenerator.getInstance("RSA");
				break;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				continue;
			}
        }

        SecureRandom secureRandom = new SecureRandom();
        keyGen.initialize(2048, secureRandom); // 2048-bit RSA key

        KeyPair keyPair = keyGen.generateKeyPair();
        
        return keyPair;
    }
    
    public static String generateStringFromPublicKey(PublicKey pubKey) {
		return new String(Base64.encode(pubKey.getEncoded())); 
	}
	
	public static String generateStringFromPrivateKey(PrivateKey privKey) {
		return new String(Base64.encode(privKey.getEncoded()));
	}
	
	public static PublicKey groupKeyGen() {
        KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (Exception e) {
			e.printStackTrace();
		}

        SecureRandom secureRandom = new SecureRandom();
        keyGen.initialize(2048, secureRandom); // 2048-bit RSA key

        KeyPair keyPair = keyGen.generateKeyPair();

        groupPrivateKey = keyPair.getPrivate();
        groupKey = keyPair.getPublic();
        
        return groupKey;
	}
	
	public static Patient generatePatient() {
		String data = UUID.randomUUID().toString();
		String dataPtr = UUID.randomUUID().toString();
		String prevDataHashPtr = UUID.randomUUID().toString();
		
		while(true) {
			try {
				Patient p = new Patient(data, dataPtr, prevDataHashPtr);
				return p;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public static Hospital generateHospital() {
		String data = UUID.randomUUID().toString();
		String dataPtr = UUID.randomUUID().toString();
		String prevDataHashPtr = UUID.randomUUID().toString();
		
		while(true) {
			try {
				Hospital h = new Hospital(data, dataPtr, prevDataHashPtr);
				return h;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public BlockNode generateBlockNode() {
		String data;
		String dataPtr;
		String prevDataHashPtr;
		BlockNode blockNode;
		Integer conditionsSeed;
		
		while(true) {
			data = UUID.randomUUID().toString();
			dataPtr = UUID.randomUUID().toString();
			conditionsSeed = (int) (Math.random() * Integer.MAX_VALUE); //we will hash this to get the actual condition code.
			
			
			//if this is the first block, generate random string. else, choose a random block and hash it.
			if(ledger.size() == 0) {
				prevDataHashPtr = UUID.randomUUID().toString();
			} else {
				int randomBlock = (int) (Math.random()) * (ledger.size());
				prevDataHashPtr = ledger.get(randomBlock).getHashPointer();
			}
			

			try {
				blockNode = new BlockNode(hash(conditionsSeed.toString()), data, dataPtr, prevDataHashPtr);
				if(ledger.add(blockNode)) {
					break;
				}
			} catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		patients.add(blockNode.getPatient());
		hospitals.add(blockNode.getHospital());
		return blockNode;
	}
}
