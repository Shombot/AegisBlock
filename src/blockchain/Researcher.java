package blockchain;

public class Researcher {
	/*
	 * no need to worry aobut the ZKP, its taking too much time
	 * 
	 * 
	 * FIRST BLOCK CONTENTS (researcher requesting the data)
	 * timeframe (maybe only going back in time?)
	 * time the researcher made the request
	 * pointer to the block they are forking
	 * signature of the block using their public key (this includes th researcher public key)
	 * request is a signed block by researcher
	 * how am I going to create the pointers and stuff?
	 * to get the links to the other blocks we can just choose them randomly
	 * 
	 * what hospital needs to verify
	 * hash pointer is valid (needs to point to a patient block and the hash should match)
	 * end date is not after the patient block that got forked
	 * verify that signature is valid, and the public key is a part of the list of approved researchers
	 * 
	 * 
	 * 
	 * 
	 * 
	 * SECOND BLOCK (patient approves and signs off on the request)
	 * we need a valid hash pointer pointing to the first block (directly above) that the researcher made to request the data
	 * patient needs to sign this block using the SAME public key that the data is being forked from
	 * possibly (?) add the symmetric key that has been encrypted with the public key of the researcher
	 * ^^^we should add this
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * THIRD BLOCK (hospitals giving researchers access)
	 * patient signature is valid (from legitimate patient, and that the 
	 * 		patient matches the one from the data block, and patient hash pointer points to a valid researcher request for their own block)
	 * 
	 * 
	 * Talk about how we thought encrypting the pointer refutes future attacks
	 * Hospitals do all of the stuff with the salt
	 * 
	 * 
	 * 
	 * 
	 * FOURTH BLOCK
	 * 
	 * hospital or patient (whoever is doing the hard work) 
	 * contains the 4 values for all the blocks except for the forked one
	 * the 4 values are the symmetric key for that block, pointer to the data for this block, merkel hash of previous block and the salt
	 * for the forked block, we do not include the salt since this would give enough info to create the merkel hash for 
	 * 		the next block in the chain
	 * 
	 * 
	 * 
	 * have the patient do all the encryption and stuff, this way they can switch hospitals and have control over the salt
	 * 		the downside of this is that the patient needs lots of computational power.
	 * 
	 * 
	 * 
	 * 
	 * HOW ARE WE GOING TO GIVE ACCESS TO RESEARCHER???
	 * give the symK unlocks everything (for the main block they forked)
	 * 
	*/
}
