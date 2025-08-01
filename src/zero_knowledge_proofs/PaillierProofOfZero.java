package zero_knowledge_proofs;

import java.math.BigInteger;

import zero_knowledge_proofs.CryptoData.CryptoData;
import zero_knowledge_proofs.CryptoData.CryptoDataArray;

public class PaillierProofOfZero extends ZKPProtocol {





	@Override
	public CryptoData initialComm(CryptoData publicInput, CryptoData secrets, CryptoData environment)
			throws MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException {
		if (publicInput == null || secrets == null) return null;
		try {
			BigInteger[] data = new BigInteger[1];				//cipher = g^0*R^n
			CryptoData[] e = environment.getCryptoDataArray();  // e = [n, n2, g]
			CryptoData[] s = secrets.getCryptoDataArray();		// s = [rp, r]
			
			BigInteger g = e[2].getBigInt();
			BigInteger n = e[0].getBigInt();
			BigInteger n2 = e[1].getBigInt();
			
			BigInteger r = s[0].getBigInt();
			BigInteger ephemeral = s[1].getBigInt();
			
			data[0] = r.modPow(n, n2); // r^n mod n^2
			
			
			CryptoData toReturn = new CryptoDataArray(data);
			return toReturn;
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println(publicInput);
			System.out.println(secrets);
			System.out.println(environment);
			throw new NullPointerException(e.getMessage());
		
		}
	}

	
	@Override
	public CryptoData initialCommSim(CryptoData publicInput, CryptoData secrets, BigInteger challenge,
			CryptoData environment)
					throws MultipleTrueProofException, ArraySizesDoNotMatchException, NoTrueProofException {
		if (publicInput == null || secrets == null) return null;
		try {

			CryptoData[] p = publicInput.getCryptoDataArray();			// s = [rp, r]  -- rp is r for proof, r is r for ciphertext
			CryptoData[] s = secrets.getCryptoDataArray();			// s = [rp, r]  -- rp is r for proof, r is r for ciphertext
			CryptoData[] e = environment.getCryptoDataArray();		// e = [n, n2, g]
			
			BigInteger n = e[0].getBigInt();
			BigInteger n2 = e[1].getBigInt();
			
			BigInteger cipher = p[0].getBigInt();

			BigInteger z = s[0].getBigInt();
			
			BigInteger[] data = new BigInteger[1];
			
			data[0] = cipher.modPow(challenge.negate(), n2).multiply(z.modPow(n, n2)).mod(n2);
			
			return new CryptoDataArray(data);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println(publicInput);
			System.out.println(secrets);
			System.out.println(environment);
			throw new NullPointerException(e.getMessage());
		}
	}
	
	@Override
	public CryptoData calcResponse(CryptoData publicInput, CryptoData secrets, BigInteger challenge,
			CryptoData environment) throws NoTrueProofException, MultipleTrueProofException {
		if(publicInput == null || secrets == null) return null;
		BigInteger[] array = new BigInteger[1];
		CryptoData[] s = secrets.getCryptoDataArray();			// s = [rp, r]  -- rp is r for proof, r is r for ciphertext
		CryptoData[] e = environment.getCryptoDataArray();		// e = [n, n2, g]

		BigInteger rp = s[0].getBigInt();
		BigInteger r = s[1].getBigInt();
		
		BigInteger n = e[0].getBigInt();
		BigInteger n2 = e[1].getBigInt();
		
		array[0] = rp.multiply(r.modPow(challenge, n2)).mod(n);  //r'*r^e
		return new CryptoDataArray(array);
	}


	@Override
	public CryptoData simulatorGetResponse(CryptoData publicInput, CryptoData secrets) {
		if(secrets == null) return null;
		CryptoData[] in = secrets.getCryptoDataArray();
		BigInteger[] out = new BigInteger[1];
		out[0] = in[0].getBigInt();
		return new CryptoDataArray(out); 
	}
	
	@Override
	public boolean verifyResponse(CryptoData input, CryptoData a, CryptoData z, BigInteger challenge,
			CryptoData environment) {
		CryptoData[] e = environment.getCryptoDataArray();
		CryptoData[] resp = z.getCryptoDataArray();
		CryptoData[] i = input.getCryptoDataArray();
		CryptoData[] a_pack = a.getCryptoDataArray();

		BigInteger n = e[0].getBigInt();
		BigInteger n2 = e[1].getBigInt();
		
		BigInteger cipher = i[0].getBigInt();
		
		BigInteger a0 = a_pack[0].getBigInt();
		BigInteger z0 = resp[0].getBigInt();

		BigInteger side1 = z0.modPow(n, n2);
		BigInteger side2 = cipher.modPow(challenge, n2).multiply(a0).mod(n2);
		
		if(side1.compareTo(side2) != 0) {
			System.out.printf("Error:  %s != %s\n", side1, side2);
			return false;
		}
		return true;
	}
	@Override
	public CryptoData initialComm(CryptoData input, CryptoData environment)
			throws MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException {	//depricated
		return null;
	}
	@Override
	public CryptoData initialCommSim(CryptoData input, BigInteger challenge, CryptoData environment)
			throws MultipleTrueProofException, ArraySizesDoNotMatchException, NoTrueProofException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CryptoData calcResponse(CryptoData input, BigInteger challenge, CryptoData environment)
			throws NoTrueProofException, MultipleTrueProofException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public CryptoData simulatorGetResponse(CryptoData input) {
		if(input == null) return null;
		CryptoData[] in = input.getCryptoDataArray();
		BigInteger[] out = new BigInteger[1];
		out[0] = in[1].getBigInt();
		return new CryptoDataArray(out); 
	}

}
