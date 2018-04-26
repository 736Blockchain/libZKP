package Commitment;

import java.math.BigInteger;
import java.lang.String;
import param.IntegerGroupParams;
import param.Params;
import param.Hash;
import BigNum.BigNumber;
import Exception.libzkpException;


public class CommitmentProofOfKnowledge {

	public int COMMITMENT_EQUALITY_CHALLENGE_SIZE = 256;
	public int COMMITMENT_EQUALITY_SECMARGIN = 512;
	private IntegerGroupParams ap, bp;
	BigInteger S1, S2, S3, challenge;
	
	
	public CommitmentProofOfKnowledge(IntegerGroupParams ap, IntegerGroupParams bp) {
		this.ap = ap;
		this.bp = bp;
	}
	public CommitmentProofOfKnowledge(IntegerGroupParams aParams, IntegerGroupParams bParams, Commitment a, Commitment b) {
		ap = aParams;
		bp = bParams;
		 
		BigInteger r1, r2, r3;
		
		if (!(a.getContents().compareTo(b.getContents())==0)) {
			throw new libzkpException("Both commitments must contain the same value");
		}

		
		int[] nums = {this.ap.modulus.bitLength(), this.bp.modulus.bitLength(), this.ap.groupOrder.bitLength(), this.bp.groupOrder.bitLength()};
		int max = 0;
		for (int i = 0; i < nums.length; i++) {
			if( nums[i] > max ) {
				max = nums[i];
			}
		}
		//System.out.println("最大值："+max);
		
		int randomSize = COMMITMENT_EQUALITY_CHALLENGE_SIZE + COMMITMENT_EQUALITY_SECMARGIN + max;
		BigInteger maxRange = BigInteger.valueOf(2).pow(randomSize).subtract(BigInteger.ONE);
		
		r1 = BigNum.BigNumber.randomBigInteger(maxRange);
		r2 = BigNum.BigNumber.randomBigInteger(maxRange);
		r3 = BigNum.BigNumber.randomBigInteger(maxRange);
		
		//T1 = g1^r1 * h1^r2 mod p1
		//T2 = g2^r1 * h2^r3 mod p2
		BigInteger T1 = (this.ap.g.modPow(r1, this.ap.modulus).multiply(this.ap.h.modPow(r2, this.ap.modulus))).mod(this.ap.modulus);
		BigInteger T2 = (this.bp.g.modPow(r1, this.bp.modulus).multiply(this.bp.h.modPow(r3, this.bp.modulus))).mod(this.bp.modulus);
		
		this.challenge = calculateChallenge(a.getCommitmentValue(), b.getCommitmentValue(), T1, T2);
		
		//  S1 = r1 + (m * challenge)   -- note, not modular arithmetic
		//  S2 = r2 + (x * challenge)   -- note, not modular arithmetic
		//  S3 = r3 + (y * challenge)   -- note, not modular arithmetic
		this.S1 = r1.add(a.getContents().multiply(this.challenge));
		this.S2 = r2.add(a.getRandomness().multiply(this.challenge));
		this.S3 = r3.add(b.getRandomness().multiply(this.challenge));
		
	}
	
	//bool CommitmentProofOfKnowledge::Verify(const Bignum& A, const Bignum& B)
	public boolean Verify(BigInteger A, BigInteger B) {//A=serialCommitmentToCoinValue, B=accCommitmentToCoinValue

		//Compute T1 = g1^S1 * h1^S2 * inverse(A^{challenge}) mod p1
		BigInteger T1 = (A.modPow(this.challenge, ap.modulus).modInverse(ap.modulus).multiply(ap.g.modPow(S1, ap.modulus)).multiply(ap.h.modPow(S2, ap.modulus))).mod(ap.modulus);
		//BigInteger T1 = (A.modPow(this.challenge, ap.modulus).modInverse(ap.modulus).multiply((ap.g.modPow(S1, ap.modulus)).multiply(ap.h.modPow(S2, ap.modulus))).mod(ap.modulus)).mod(ap.modulus);
		// Compute T2 = g2^S1 * h2^S3 * inverse(B^{challenge}) mod p2
		BigInteger T2 = (B.modPow(this.challenge, bp.modulus).modInverse(bp.modulus).multiply(bp.g.modPow(S1, bp.modulus)).multiply(bp.h.modPow(S3, bp.modulus))).mod(bp.modulus);
		//BigInteger T2 = (B.modPow(this.challenge, bp.modulus).modInverse(bp.modulus).multiply((bp.g.modPow(S1, bp.modulus)).multiply(bp.h.modPow(S3, bp.modulus))).mod(bp.modulus)).mod(bp.modulus);
		// Hash T1 and T2 along with all of the public parameters
		BigInteger computedChallenge = calculateChallenge(A, B, T1, T2);
		
		// Return success if the computed challenge matches the incoming challenge
		if(/*computedChallenge == this.challenge*/computedChallenge.compareTo(this.challenge) == 0) {
			return true;
		}

		// Otherwise return failure
		return false;

	}
	
	/**
	 * 基本上就是根据参数计算一个相应的大数返回
	 * @param a
	 * @param b
	 * @param commitOne
	 * @param commitTwo
	 * @return
	 */
	public BigInteger calculateChallenge(BigInteger a, BigInteger b, BigInteger commitOne, BigInteger commitTwo) {
		
		String string = "COMMITMENT_EQUALITY_PROOF"+commitOne.toString()+commitTwo.toString()+a.toString()+b.toString();
		byte[] getbytes = string.getBytes();
		byte[] gethash = param.Hash.calculateHash(getbytes);
		BigInteger hash= new BigInteger(gethash);
		//String hashString = new String(gethash);
		//BigInteger hash= new BigInteger(hashString);
		return  hash;
		
	}
	
	
}
