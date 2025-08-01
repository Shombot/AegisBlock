package zero_knowledge_proofs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

import org.bouncycastle.util.Arrays; 

import zero_knowledge_proofs.CryptoData.CryptoData;


public abstract class ZKPProtocol{
	private static ArrayList<ProverProtocolPair> protocols = new ArrayList<ProverProtocolPair>();
	private static ArrayList<ProverProtocolPair> compoundProtocols = new ArrayList<ProverProtocolPair>();

	@SuppressWarnings("rawtypes")
	public static boolean registerProtocol(String uniqueName, Class protocol, boolean isCompound)
	{
		for(ProverProtocolPair ppp : protocols)
		{
			if(ppp.name.equals(uniqueName))
				return false;
		}
		ProverProtocolPair ppp;
		try
		{
			ppp = new ProverProtocolPair(uniqueName, protocol);
		}
		catch (ClassCastException e)
		{
			return false;
		}
		protocols.add(ppp);
		if(isCompound)
			compoundProtocols.add(ppp);
		return true;
	}
	
	/**
	 * An HVZKP protocol
	 * 
	 * @param input Whatever inputs are required for the ZKP
	 * @return 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws MultipleTrueProofException 
	 * @throws ArraySizesDoNotMatchException 
	 * @throws NoTrueProofException 
	 */
	public boolean prove(CryptoData input, CryptoData environment, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException, MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException {
		//System.out.println("input = " + input);
		CryptoData a = initialComm(input, environment);
		//System.out.println("ProverCounter = " + counter);
		out.writeObject(a);
		out.flush();
		BigInteger c = (BigInteger) in.readObject();
		CryptoData z = calcResponse(input, c, environment);
		out.writeObject(z);
		out.flush();
		boolean toReturn = (boolean) in.readObject();
		if(!toReturn)
		{
			System.out.println("i = " + input);
			System.out.println("a = " + a);
			System.out.println("c = " + c);
			System.out.println("z = " + z);
			System.out.println("env = " + environment);
		}
		return toReturn;
	}
	/**
	 * An true ZKP protocol, where the challenge is committed beforehand.
	 * 
	 * @param input Whatever inputs are required for the ZKP
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws MultipleTrueProofException 
	 * @throws ArraySizesDoNotMatchException 
	 * @throws NoTrueProofException 
	 */
	public boolean trueZKProve(CryptoData input, CryptoData environment, CryptoData commitmentEnvironment, ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException, MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException {
		CryptoData a = initialComm(input, environment);
		ECPedersenCommitment cCmt = (ECPedersenCommitment) in.readObject();
		out.writeObject(a);
		out.flush();
		BigInteger[] c = (BigInteger[]) in.readObject();
		boolean valid = cCmt.verifyCommitment(c[0], c[1], commitmentEnvironment);
		out.writeObject(valid);
		out.flush();
		if(valid)
		{
			CryptoData z = calcResponse(input, c[0], environment);
			out.writeObject(z);
			out.flush();
			boolean toReturn = (boolean) in.readObject();
			return toReturn;
		}
		else System.out.println("BAD CHALLENGE COMMITMENT");
		return valid;
	}
	/**
	 * An true ZKP protocol, where the challenge is committed beforehand.
	 * 
	 * @param publicInput Whatever public inputs are required for the ZKP
	 * @param secrets Whatever secret inputs are required for the ZKP
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws MultipleTrueProofException 
	 * @throws ArraySizesDoNotMatchException 
	 * @throws NoTrueProofException 
	 */
	public boolean trueZKProve(CryptoData publicInput, CryptoData secrets, CryptoData environment, CryptoData commitmentEnvironment, ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException, MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException {
		CryptoData a = initialComm(publicInput, secrets, environment);
		PedersenCommitment cCmt = (PedersenCommitment) in.readObject();
		out.writeObject(a);
		out.flush();
		BigInteger[] c = (BigInteger[]) in.readObject();
		boolean valid = cCmt.verifyCommitment(c[0], c[1], commitmentEnvironment);
		out.writeObject(valid);
		out.flush();
		if(valid)
		{
			CryptoData z = calcResponse(publicInput, secrets, c[0], environment);
			out.writeObject(z);
			out.flush();
			boolean toReturn = (boolean) in.readObject();
			return toReturn;
		}
		else System.out.println("BAD CHALLENGE COMMITMENT");
		return valid;
	}
	
	
	/**
	 * @param proverInput
	 * @param verifierInput
	 * @param environment
	 * @param in
	 * @param out
	 * @param verifier
	 * @param myCmt
	 * @param challenge
	 * @param transcript
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws MultipleTrueProofException
	 * @throws NoTrueProofException
	 * @throws ArraySizesDoNotMatchException
	 */
	public boolean parallelZKProve(CryptoData proverInput, CryptoData verifierInput, CryptoData environment, ObjectInputStream in, ObjectOutputStream out, ECPedersenCommitment myCmt, CryptoData commitmentEnvironment, BigInteger[] challenge, StringBuilder transcript) throws ClassNotFoundException, IOException, MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException {
		out.writeObject(myCmt);	
		out.flush();	
		CryptoData a = initialComm(proverInput, environment);
		ECPedersenCommitment otherCmt = new ECPedersenCommitment(BigInteger.ZERO, BigInteger.ZERO, commitmentEnvironment);
		CryptoData otherA = null;
		CryptoData otherZ = null;
		BigInteger[] otherChallenge;
		out.flush();
		otherCmt = (ECPedersenCommitment) in.readObject();
		out.writeObject(a);
		out.flush();
		otherA = (CryptoData) in.readObject();
		out.writeObject(challenge);
		out.flush();
		otherChallenge = (BigInteger[]) in.readObject();
		boolean good = true;
		
		boolean valid = otherCmt.verifyCommitment(otherChallenge[0], otherChallenge[1], commitmentEnvironment);

		if(valid == false) 
		{
			good = false;
			System.out.println("They lied in the commitment.");
			if(myCmt.verifyCommitment(challenge[0], challenge[1], commitmentEnvironment)) System.out.println("Mine is good");
			else System.out.println("mine is bad.  how did this happen???? 1");
		}
		out.writeBoolean(valid);
		out.flush();
		boolean otherValid = in.readBoolean();
		if(!otherValid) {
			good = false;
			System.out.println("They think I lied in the commitment");
			if(myCmt.verifyCommitment(challenge[0], challenge[1], commitmentEnvironment)) System.out.println("Mine is good");
			else System.out.println("mine is bad.  how did this happen???? 2");
		}

		if((valid && otherValid))
		{
			CryptoData z = calcResponse(proverInput, otherChallenge[0], environment);
			out.writeObject(z);
			out.flush();
			otherZ = (CryptoData) in.readObject();
			boolean believeOther = verifyResponse(verifierInput, otherA, otherZ, challenge[0], environment);
			if(!believeOther) {
				good = false;
				System.out.println("I don't believe them");
			}
			out.writeBoolean(believeOther);
			out.flush();
			boolean believeMe = in.readBoolean();
			if(!believeMe){
				good = false;
				System.out.println("They don't believe me..." + challenge[0]);
				boolean flag = verifyResponse(proverInput, a, z, otherChallenge[0], environment);
				if(!flag)
					System.out.println("I don't blame him");
				else System.out.println("We disagree");
//				throw new NullPointerException();
			}
		}
		else good = false;
//			boolean good = true;
//			for(int i = 0; i < out.length; i++)
//			{						
//				out[i].writeObject(z);
//				otherZ = (CryptoData) in[i].readObject();
//				if(i <= verifierInput.length) {
//					if(believeOther == false)
//					{
//						good = false;
//						
//						System.out.println("I do not believe party " + i);
////						System.out.println(verifierInput[i]);
////						System.out.println(otherA[i]);
////						System.out.println(otherZ);
////						
//
//						System.out.println();
//					}
//					
//					out[i].writeBoolean(believeOther);
//					out[i].flush();
//				}
//				boolean believeMe = in[i].readBoolean();
//				if(believeMe == false)
//				{
//					
//				}
//			}	



			
			
			if(transcript != null)
			{
				transcript.append("\nChallenge Commitment:  ");
				transcript.append(otherCmt.toString64());
				transcript.append("\nInput:  ");
				transcript.append(verifierInput.toString64());
				transcript.append("\nInitComm:  ");

				transcript.append(otherA.toString64());
				transcript.append("\nResponse:  ");
				transcript.append(otherZ.toString64());
				transcript.append("\nchallenge:  ");
				transcript.append(Base64.getEncoder().encodeToString(otherChallenge[0].toByteArray()));
				transcript.append("\nchallenge key:  ");
				transcript.append(Base64.getEncoder().encodeToString(otherChallenge[1].toByteArray()));
	//			transcriptOut.append("\nEnvironment:  ");
	//			transcriptOut.append(environment.toString64());
				transcript.append("\n\n");
			
			return good;
			
		}
		else 
		{
			System.out.println("BAD CHALLENGE COMMITMENT");
			return false;
		}
	}
	/**
	 * @param proverInput
	 * @param verifierInput
	 * @param environment
	 * @param in
	 * @param out
	 * @param verifier
	 * @param myCmt
	 * @param challenge
	 * @param transcript
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws MultipleTrueProofException
	 * @throws NoTrueProofException
	 * @throws ArraySizesDoNotMatchException
	 */
	public boolean parallelZKProveWithFriends(CryptoData proverInput, CryptoData verifierInput, CryptoData environment, ObjectInputStream in, ObjectOutputStream out, ECPedersenCommitment myCmt, CryptoData commitmentEnvironment, BigInteger[] challenge, ObjectOutputStream[] friendsOut, ObjectInputStream[] friendsIn, int[] request, StringBuilder transcript) throws ClassNotFoundException, IOException, MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException {
		
		out.writeObject(myCmt);
		out.flush();
		for(int r : request)
		{
			friendsOut[r].writeBoolean(false);
			friendsOut[r].flush();
		}
		CryptoData a = initialComm(proverInput, environment);
		for(int r : request)
		{
			CryptoData friendA = (CryptoData) friendsIn[r].readObject();
			if(friendA == null) throw new NullPointerException("Friend returned null");
			a.addFillDataHole(friendA);
		}
		ECPedersenCommitment cCmt = (ECPedersenCommitment) in.readObject();
		out.writeObject(a);
		out.flush();
		CryptoData otherA = (CryptoData) in.readObject();
		out.writeObject(challenge);
		out.flush();
		BigInteger[] otherC = (BigInteger[]) in.readObject();
		
		boolean valid = cCmt.verifyCommitment(otherC[0], otherC[1], commitmentEnvironment);
		out.writeBoolean(valid);
		out.flush();
		boolean hisValid = in.readBoolean();
		if(valid && hisValid)
		{
//			System.out.println("In ZKPProtocol: " + environment);
			CryptoData z = calcResponse(proverInput, otherC[0], environment);
			ArrayList<BigInteger> challengeList = new ArrayList<BigInteger>(request.length);
			internalNullChallenges(z, otherC[0], challengeList);
			for(int i = 0; i < request.length; i++)
			{
				BigInteger c = challengeList.get(i);
				friendsOut[request[i]].writeObject(c);
				friendsOut[request[i]].flush();
			}
			for(int i = 0; i < request.length; i++)
			{
				z.addFillDataHole((CryptoData) friendsIn[request[i]].readObject());
			}
			out.writeObject(z);
			out.flush();
			CryptoData otherZ = (CryptoData) in.readObject();
			valid = verifyResponse(verifierInput, otherA, otherZ, challenge[0], environment);
			out.writeBoolean(valid);
			out.flush();
			hisValid = in.readBoolean();
			if(hisValid == false)
			{
				System.out.println("He didn't like my proof...");
			}
			if(valid == false) System.out.println("I don't like his proof");
			if(transcript != null)
			{
				transcript.append("\nChallenge Commitment:  ");
				transcript.append(cCmt.toString64());
				transcript.append("\nInput:  ");
				transcript.append(verifierInput.toString64());
				
				transcript.append("\nInitComm:  ");

				transcript.append(otherA.toString64());
				transcript.append("\nResponse:  ");
				transcript.append(otherZ.toString64());
				transcript.append("\nchallenge:  ");
				transcript.append(Base64.getEncoder().encodeToString(otherC[0].toByteArray()));
				transcript.append("\nchallenge key:  ");
				transcript.append(Base64.getEncoder().encodeToString(otherC[1].toByteArray()));
	//			transcriptOut.append("\nEnvironment:  ");
	//			transcriptOut.append(environment.toString64());
				transcript.append("\n\n");
			}
			return hisValid && valid;
		}
		else {
			System.out.println("BAD CHALLENGE COMMITMENT");
			return false;
		}
	}
	
	
	
//	BigInteger order = commitmentEnvironment.getCryptoDataArray()[0].getECCurveData().getOrder();
//	CryptoData a = initialComm(proverInput, environment);
//	ECPedersenCommitment otherCmt = new ECPedersenCommitment(BigInteger.ZERO, BigInteger.ZERO, commitmentEnvironment);
//	CryptoData[] otherA = new CryptoData[verifierInput.length];
//	BigInteger[] totalC = new BigInteger[] {BigInteger.ZERO,BigInteger.ZERO};
//	BigInteger[][] otherChallenges = new BigInteger[out.length][];
//	for(int i = 0; i < out.length; i++)
//	{
//		out[i].writeObject(myCmt);
//		otherCmt = otherCmt.multiplyCommitment((ECPedersenCommitment) in[i].readObject(), commitmentEnvironment);
//		out[i].writeObject(a);
//		/*if(i <= verifierInput.length)*/ otherA[i] = (CryptoData) in[i].readObject();
//		out[i].reset();
//		out[i].writeObject(challenge);
//		otherChallenges[i] = (BigInteger[]) in[i].readObject();
//		totalC[0] = totalC[0].add(otherChallenges[i][0]).mod(order);
//		totalC[1] = totalC[1].add(otherChallenges[i][1]).mod(order);
//		
//	}
//	boolean valid = otherCmt.verifyCommitment(totalC[0], totalC[1], commitmentEnvironment);
//
//	if(valid == false) 
//	{
//		System.out.println("Someone lied in the commitment.");
//		if(myCmt.verifyCommitment(challenge[0], challenge[1], commitmentEnvironment)) System.out.println("Mine is good");
//		else System.out.println("mine is bad.  how did this happen????");
//	}
//	boolean otherValid = true;
//	for(int i = 0; i < out.length; i++)
//	{
//		out[i].writeBoolean(valid);
//		out[i].flush();
//	}
//	for(int i = 0; i < out.length; i++)
//	{
//		boolean party = in[i].readBoolean();
//		if(!party) System.out.println("Party " + i + " believes someone lied about the commitment");
//		otherValid = otherValid && party;				
//	}
//	
//	if((valid && otherValid))
//	{
//		CryptoData otherZ = null;
//		boolean good = true;
//		CryptoData z = calcResponse(proverInput, totalC[0], environment);
//		for(int i = 0; i < out.length; i++)
//		{						
//			out[i].writeObject(z);
//			otherZ = (CryptoData) in[i].readObject();
//			if(i <= verifierInput.length) {
//				boolean believeOther = verifyResponse(verifierInput[i], otherA[i], otherZ, totalC[0].subtract(otherChallenges[i][0]).add(challenge[0]), environment);
//				if(believeOther == false)
//				{
//					good = false;
//					
//					System.out.println("I do not believe party " + i);
////					System.out.println(verifierInput[i]);
////					System.out.println(otherA[i]);
////					System.out.println(otherZ);
////					
//
//					System.out.println();
//				}
//				
//				out[i].writeBoolean(believeOther);
//				out[i].flush();
//			}
//			boolean believeMe = in[i].readBoolean();
//			if(believeMe == false)
//			{
//				good = false;
//				System.out.println("Party " + i + " does not believe me.  Double checking proof...");
//				boolean flag = verifyResponse(proverInput, a, z, totalC[0], environment);
//				if(!flag)
//					System.out.println("I don't blame him");
//				else System.out.println("We disagree");
////				throw new NullPointerException();
//			}
//		}	
	
	
	public boolean verify(CryptoData input, BigInteger challenge, CryptoData environment, ObjectInputStream in, ObjectOutputStream out, StringBuilder transcriptOut) throws IOException, ClassNotFoundException
	{
		//System.out.println("vInput = " + input);
		CryptoData a = (CryptoData) in.readObject();
		out.writeObject(challenge);
		out.flush();
		//System.out.println("vA = " + a);
		CryptoData z = (CryptoData) in.readObject();

		//System.out.println("vZ = " + z);
		boolean toReturn = verifyResponse(input, a, z, challenge, environment);
		out.writeObject(toReturn);
		out.flush();
		if(transcriptOut != null)
		{
			transcriptOut.append("Input:  ");
			transcriptOut.append(input.toString64());
			transcriptOut.append("\nInitComm:  ");
			transcriptOut.append(a.toString64());
			transcriptOut.append("\nResponse:  ");
			transcriptOut.append(z.toString64());
	
			transcriptOut.append("\nchallenge:  ");
			transcriptOut.append(Base64.getEncoder().encodeToString(challenge.toByteArray()));
			transcriptOut.append("\nEnvironment:  ");
			transcriptOut.append(environment.toString64());
			transcriptOut.append("\n\n");
		}
		
		return toReturn;
	}
	public boolean maliciousVerify(CryptoData input, PedersenCommitment cCmt, BigInteger[] challenge, CryptoData environment, ObjectInputStream in, ObjectOutputStream out, StringBuilder transcriptOut) throws IOException, ClassNotFoundException
	{
		out.writeObject(cCmt);
		out.flush();
		CryptoData a = (CryptoData) in.readObject();
		out.writeObject(challenge);
		out.flush();
		boolean verified = (boolean) in.readObject();
		if(verified) {
			CryptoData z = (CryptoData) in.readObject();
			boolean toReturn = verifyResponse(input, a, z, challenge[0], environment);
			out.writeObject(toReturn);
			out.flush();
			if(transcriptOut != null)
			{
				transcriptOut.append("\nChallenge Commitment:  ");
				transcriptOut.append(cCmt.toString64());
				transcriptOut.append("\nInput:  ");
				transcriptOut.append(input.toString64());
				transcriptOut.append("\nInitComm:  ");
				transcriptOut.append(a.toString64());
				transcriptOut.append("\nResponse:  ");
				transcriptOut.append(z.toString64());
				transcriptOut.append("\nchallenge:  ");
				transcriptOut.append(Base64.getEncoder().encodeToString(challenge[0].toByteArray()));
				transcriptOut.append("\nchallenge key:  ");
				transcriptOut.append(Base64.getEncoder().encodeToString(challenge[1].toByteArray()));
	//			transcriptOut.append("\nEnvironment:  ");
	//			transcriptOut.append(environment.toString64());
				transcriptOut.append("\n\n");
			}
			return toReturn;
		}
		return verified;
	}
	
	public abstract boolean verifyResponse(CryptoData input, CryptoData a, CryptoData z, BigInteger challenge, CryptoData environment);
 
	
	public abstract CryptoData initialComm(CryptoData input, CryptoData environment) throws MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException;
	public abstract CryptoData initialComm(CryptoData publicInput, CryptoData secrets, CryptoData environment) throws MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException;
	public abstract CryptoData initialCommSim (CryptoData input, BigInteger challenge, CryptoData environment) throws MultipleTrueProofException, ArraySizesDoNotMatchException, NoTrueProofException;
	public abstract CryptoData initialCommSim (CryptoData publicInput, CryptoData secrets, BigInteger challenge, CryptoData environment) throws MultipleTrueProofException, ArraySizesDoNotMatchException, NoTrueProofException;
	
	public abstract CryptoData calcResponse(CryptoData input, BigInteger challenge, CryptoData environment) throws NoTrueProofException, MultipleTrueProofException;
	public abstract CryptoData calcResponse(CryptoData publicInput, CryptoData secrets, BigInteger challenge, CryptoData environment) throws NoTrueProofException, MultipleTrueProofException;
	public abstract CryptoData simulatorGetResponse(CryptoData input);
	public abstract CryptoData simulatorGetResponse(CryptoData publicInput, CryptoData secrets);
	
	public ArrayList<BigInteger> nullChallenges(CryptoData response, BigInteger challenge){
		return internalNullChallenges(response, challenge, new ArrayList<BigInteger>());
	}
	protected ArrayList<BigInteger> internalNullChallenges(CryptoData response, BigInteger challenge, ArrayList<BigInteger> list){
		if(response == null) {
			list.add(challenge);
			return list;
		}
		if(!response.hasNull()) return list;
		if(response.getCryptoDataArray() == null) return list;
		
		return list;
	}

	@SuppressWarnings("rawtypes")
	public static ZKPProtocol generateProver(String string) throws InvalidStringFormatException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException {
		String str = string;
		int place = string.indexOf('(');
		ZKPProtocol toReturn = null;
		if(place != -1) //If it is compound, it has a paren.
		{
			if(string.charAt(string.length()-1) != ')')
				throw new InvalidStringFormatException();
			str = string.substring(0, place);
			int protocol = -1;
			for(int i = 0; i < compoundProtocols.size(); i++)
			{
				if(compoundProtocols.get(i).name.equals(str))
				{
					protocol = i;
					break;
				}
			}
			
			if(protocol == -1)
				throw new InvalidStringFormatException();	//Maybe I should make a better exception name for this one.
			
			String args = string.substring(place+1, string.length()-1);
			int inParens = 0;
			int start = 0;
			int proversIndex = 0;  
			int proverNum = 1;
			for(int i = 0; i < args.length(); i++)
			{
				char p = args.charAt(i);
				if(p == '(') inParens++;
				if(p == ')') inParens--;
				if(p == ',' && inParens == 0)
				{
					if(start == i) throw new InvalidStringFormatException();
					proverNum++;
					start = i + 1;
				}
			}
			if(inParens != 0) throw new InvalidStringFormatException();
			start = 0;
			ZKPProtocol[] provers = new ZKPProtocol[proverNum];
			for(int i = 0; i < args.length(); i++)
			{
				char p = args.charAt(i);
				if(p == '(') inParens++;
				if(p == ')') inParens--;
				if(inParens < 0) throw new InvalidStringFormatException();
				if((p == ',') && inParens == 0)
				{
					provers[proversIndex] = generateProver(args.substring(start, i));
					proversIndex++;
					start = i + 1;
				}
			}
			provers[proversIndex] = generateProver(args.substring(start));
			proversIndex++;
			
			toReturn = (ZKPProtocol) compoundProtocols.get(protocol).protocol.getConstructors()[0].newInstance(new Object[] {provers});
		} 
		else { // It is not a compound statement or it not presented correctly.
			Class toMake = null;
			for(int i = 0; i < protocols.size(); i++)
			{
				if(string.equals(protocols.get(i).name))
				{
					toMake = protocols.get(i).protocol;
					break;
				}
			}
			try
			{
				toReturn = (ZKPProtocol) toMake.newInstance();
			}catch(NullPointerException e)
			{
				System.out.println(string);
				System.out.println(toMake);
				for(int i = 0; i < protocols.size(); i++)
				{
					System.out.println(protocols.get(i).name);
					
				}
				throw e;
			}
		}
		return toReturn;
	}
	/**
	 * A non-interactive version of a Zero Knowledge Protocol
	 * 
	 * @param input Whatever inputs are required for the ZKP
	 * @return The initial communication and the response
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws MultipleTrueProofException 
	 * @throws ArraySizesDoNotMatchException 
	 * @throws NoTrueProofException 
	 */
	public CryptoData[] proveFiatShamir(CryptoData publicInput, CryptoData secrets, CryptoData environment) throws IOException, ClassNotFoundException, MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException {
		
		CryptoData a = initialComm(publicInput, secrets, environment);
		BigInteger c = fiatShamirChallange(publicInput, a, environment);
		CryptoData z = calcResponse(publicInput, secrets, c, environment);
		return new CryptoData[] {a, z};
	}
	/**
	 * Creates Fiat Shamir challenge
	 * 
	 * @param input Whatever inputs are required for the challenge
	 * @return The challenge
	 */
	public BigInteger fiatShamirChallange(CryptoData publicInput, CryptoData a, CryptoData environment) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[][] bytes = new byte[][] {
				a.getBytes(),
				environment.getBytes(),
				publicInput.getBytes()};
			BigInteger c = new BigInteger(digest.digest(Arrays.concatenate(bytes))).mod(BigInteger.ONE.shiftLeft(255));
			return c;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * A non-interactive version of a Zero Knowledge Protocol -- verifier.
	 * 
	 * @param publicInput Whatever inputs are required for the ZKP
	 * @param a The initial communications from the prover
	 * @param z Response from the prover.
	 * @param enviroment 
	 * @return The initial communication and the response
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws MultipleTrueProofException 
	 * @throws ArraySizesDoNotMatchException 
	 * @throws NoTrueProofException 
	 */
	public boolean verifyFiatShamir(CryptoData publicInput, CryptoData a, CryptoData z, CryptoData environment) throws IOException, ClassNotFoundException, MultipleTrueProofException, NoTrueProofException, ArraySizesDoNotMatchException {
		
		if(a == null || environment == null || publicInput == null) {
			System.out.println(a);
			System.out.println(environment);
			System.out.println(publicInput);
			System.out.println();
		}
		BigInteger c = fiatShamirChallange(publicInput, a, environment);
		
		return verifyResponse(publicInput, a, z, c, environment);
	}
}