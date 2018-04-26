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
			
			Commitment c = new Commitment(params.coinCommitmentGroup, s);
			//System.out.println(c.getCommitmentValue().bitLength());
			//检查CommimentValue是个素数，然后确保在一定的范围内
			// First verify that the commitment is a prime number
			// in the appropriate range. If not, we'll throw this coin
			// away and generate a new one.
			if (c.getCommitmentValue().isProbablePrime(10)
					&& c.getCommitmentValue().compareTo(params.accumulatorParams.minCoinValue) >= 0
					&& c.getCommitmentValue().compareTo(params.accumulatorParams.maxCoinValue) <= 0
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
	 
	 /*
	private void makeFastComment(String comment) {
		
		// Generate a random serial number in the range 0...{q-1} where
		// "q" is the order of the commitment group.
		BigInteger s = BigNumber.randomBigInteger(this.params.coinCommitmentGroup.groupOrder);
		
		// Generate a random number "r" in the range 0...{q-1}
		BigInteger r = BigNumber.randomBigInteger(this.params.coinCommitmentGroup.groupOrder);
		
		// Manually compute a Pedersen commitment to the serial number "s" under randomness "r"
		// C = g^s * h^r mod p
		BigInteger commitmentValue = (this.params.coinCommitmentGroup.g.mod(s).multiply(this.params.coinCommitmentGroup.h.mod(r))).mod(this.params.coinCommitmentGroup.modulus);
		//System.out.println(commitmentValue);
		//System.out.println(commitmentValue.bitLength());
		// Repeat this process up to MAX_COINMINT_ATTEMPTS times until
		// we obtain a prime number
		for(int attempt = 0; attempt < 10000; attempt++) {
			
			// First verify that the commitment is a prime number
			// in the appropriate range. If not, we'll throw this coin
			// away and generate a new one.
			if (commitmentValue.isProbablePrime(10)
					&& commitmentValue.compareTo(params.accumulatorParams.minCoinValue) >= 0
					&& commitmentValue.compareTo(params.accumulatorParams.maxCoinValue) <= 0
					) {
				this.serialNumber = s;
				this.randomnes = r;
				this.commentPublic = new CommentPublic(params, commitmentValue, comment);
			
				return;
			}
			
			// Generate a new random "r_delta" in 0...{q-1}
			BigInteger r_delta = BigNumber.randomBigInteger(this.params.coinCommitmentGroup.groupOrder);
		
			// The commitment was not prime. Increment "r" and recalculate "C":
			// r = r + r_delta mod q
			// C = C * h mod p
			r = r.add(r_delta).mod(params.coinCommitmentGroup.groupOrder);
			//commitmentValue = commitmentValue.multiply(this.params.coinCommitmentGroup.h.modPow(r_delta, this.params.coinCommitmentGroup.modulus));
			commitmentValue = (this.params.coinCommitmentGroup.g.mod(s).multiply(this.params.coinCommitmentGroup.h.mod(r))).mod(this.params.coinCommitmentGroup.modulus);
		}
		// We only get here if we did not find a coin within
		// MAX_COINMINT_ATTEMPTS. Throw an exception.
		throw new libzkpException("Unable to mint a new Zerocoin (too many attempts)");
	}
	*/
	
	public CommentPrivate(Params params, String comment) {
		this.params = params;
		makeComment(comment);
		//makeFastComment(comment);
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
