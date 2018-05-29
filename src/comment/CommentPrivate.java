package comment;

import java.math.BigInteger;
import param.Params;
import param.GlobalPara;
import BigNum.BigNumber;
import comment.CommentPublic;
import Commitment.Commitment;
import Exception.libzkpException;

public class CommentPrivate {
	
	private Params params;
	private CommentPublic commentPublic;
	private BigInteger randomnes;
	private BigInteger serialNumber;
	
	
	 private void makeComment(String comment) {
		// Repeat this process up to MAX_COINMINT_ATTEMPTS times until
		// we obtain a prime number
		BigInteger s;
		for(int attempt = 0; attempt < 10000; attempt++) {
			//s是一个随机数，作为serialNumber
			s = BigNumber.randomBigInteger(this.params.coinCommitmentGroup.groupOrder);
//System.out.println("--------- s.bitlength():"+s.bitLength());
			Commitment c = new Commitment(params.coinCommitmentGroup, s);
			//System.out.println(c.getCommitmentValue().bitLength());
			//检查CommimentValue是个素数，然后确保在一定的范围内
			// First verify that the commitment is a prime number
			// in the appropriate range. If not, we'll throw this coin
			// away and generate a new one.
			if (c.getCommitmentValue().isProbablePrime(10)
					&& c.getCommitmentValue().compareTo(params.accumulatorAndproofParams.minCommitmentValue) >= 0
					&& c.getCommitmentValue().compareTo(params.accumulatorAndproofParams.maxCommitmentValue) <= 0
					) {
				this.serialNumber = s;
				this.randomnes = c.getRandomness();
				this.commentPublic = new CommentPublic(params, c.getCommitmentValue(), comment);
				
				return;
			}
		
		}
		// We only get here if we did not find a coin within
		// MAX_COINMINT_ATTEMPTS. Throw an exception.
		throw new libzkpException("Unable to makeComment (too many attempts)");
	}
	 
	 
	private void makeFastComment(String comment) {
		System.out.println("\t\tEntering makeFastComment...");
		// Generate a random serial number in the range 0...{q-1} where
		// "q" is the order of the commitment group.
		BigInteger s = BigNumber.randomBigInteger(this.params.coinCommitmentGroup.groupOrder);
		
		// Generate a random number "r" in the range 0...{q-1}
		BigInteger r = BigNumber.randomBigInteger(this.params.coinCommitmentGroup.groupOrder);
		BigInteger r_delta;
		// Manually compute a Pedersen commitment to the serial number "s" under randomness "r"
		// C = g^s * h^r mod p
		BigInteger commitmentValue = (this.params.coinCommitmentGroup.g.modPow(s, this.params.coinCommitmentGroup.modulus)
				.multiply( this.params.coinCommitmentGroup.h.modPow(r,this.params.coinCommitmentGroup.modulus) )
				).
				mod(this.params.coinCommitmentGroup.modulus);
		//System.out.println(commitmentValue);
		//System.out.println(commitmentValue.bitLength());
		// Repeat this process up to MAX_COINMINT_ATTEMPTS times until
		// we obtain a prime number
		for(int attempt = 0; attempt < 10000; attempt++) {
//			if(commitmentValue.isProbablePrime(10)) {
//				System.out.print("===="+commitmentValue.isProbablePrime(10));
//				System.out.print(" ===="+(commitmentValue.compareTo(this.params.accumulatorAndproofParams.minCommitmentValue) >= 0));
//				System.out.println(" ===="+(commitmentValue.compareTo(this.params.accumulatorAndproofParams.maxCommitmentValue) <= 0)+"\n");
//			}
			// First verify that the commitment is a prime number
			// in the appropriate range. If not, we'll throw this coin
			// away and generate a new one.
			if (commitmentValue.isProbablePrime(10)
					&& commitmentValue.compareTo(this.params.accumulatorAndproofParams.minCommitmentValue) >= 0
					&& commitmentValue.compareTo(this.params.accumulatorAndproofParams.maxCommitmentValue) <= 0
					) {
				this.serialNumber = s;
				this.randomnes = r;
				this.commentPublic = new CommentPublic(params, commitmentValue, comment);

System.out.println("\t\tLeaving makeFastComment...Tried: "+attempt+" times");
				return;
			}
			
			// Generate a new random "r_delta" in 0...{q-1}
			r_delta = BigNumber.randomBigInteger(this.params.coinCommitmentGroup.groupOrder);
		
			// The commitment was not prime. Increment "r" and recalculate "C":
			// r = r + r_delta mod q
			// C = C * h mod p
			r = ( r.add(r_delta) ).mod(this.params.coinCommitmentGroup.groupOrder);
			//commitmentValue = commitmentValue.multiply(this.params.coinCommitmentGroup.h.modPow(r_delta, this.params.coinCommitmentGroup.modulus));
			commitmentValue = commitmentValue.multiply(
					this.params.coinCommitmentGroup.h.modPow(r_delta , this.params.coinCommitmentGroup.modulus) )
					.mod(this.params.coinCommitmentGroup.modulus
					);
		}
		// We only get here if we did not find a coin within
		// MAX_COINMINT_ATTEMPTS. Throw an exception.
		throw new libzkpException("Unable to mint a new Zerocoin (too many attempts)");
	}
	
	
	public CommentPrivate(Params params, String comment) {
		this.params = params;
//		makeComment(comment);
		makeFastComment(comment);
	}
	
	public CommentPublic getCommentPublic() {
		return this.commentPublic;
	}
	
	public BigInteger getSerialNumber() {
		return this.serialNumber;
	}
	
	public BigInteger getRandomness() {
		return this.randomnes;
	}
	
}
