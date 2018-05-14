package SerialNumberSignatureOfKnowledge;

import java.math.BigInteger;
import BigNum.BigNumber;
import java.lang.String;
import param.Params;
import param.Hash;
import param.AccumulatorAndProofParams;
import comment.CommentPrivate;
import comment.CommentPublic;
import Commitment.Commitment;
import Commitment.CommitmentProofOfKnowledge;
import Accumulator.Accumulator;
import Accumulator.AccumulatorWitness;
import Exception.libzkpException;

public class SerialNumberSignatureOfKnowledge {

	private Params params;
	private byte[] hash;
	private BigInteger[] s_notprime;
	private BigInteger[] sprime;
	
	public SerialNumberSignatureOfKnowledge(Params p) {
		params = p;
	}
	
	public SerialNumberSignatureOfKnowledge(Params p, CommentPrivate commentPrivate, Commitment commitmentToCoin, byte[] msghash){
		params = p;
		s_notprime = new BigInteger[params.zkp_iterations];
		sprime = new BigInteger[params.zkp_iterations];
		
		if (params.coinCommitmentGroup.modulus != params.serialNumberSoKCommitmentGroup.groupOrder) {
			throw new libzkpException("Groups are not structured correctly.");
		}
		
		BigInteger a = params.coinCommitmentGroup.g;
		BigInteger b = params.coinCommitmentGroup.h;
		BigInteger g = params.serialNumberSoKCommitmentGroup.g;
		BigInteger h = params.serialNumberSoKCommitmentGroup.h;
		
		String string = params.toString() + commitmentToCoin.getCommitmentValue().toString() + commentPrivate.getSerialNumber().toString();
		//byte[] getbytes = string.getBytes();
		//byte[] gethash = param.Hash.calculateHash(getbytes);
		
		BigInteger[] r = new BigInteger[params.zkp_iterations];
		BigInteger[] v = new BigInteger[params.zkp_iterations];
		BigInteger[] c = new BigInteger[params.zkp_iterations];
		
		for(int i = 0; i < params.zkp_iterations; i++) {
			r[i] = BigNum.BigNumber.randomBigInteger(params.coinCommitmentGroup.groupOrder);
			v[i] = BigNum.BigNumber.randomBigInteger(params.serialNumberSoKCommitmentGroup.groupOrder);
		}
		
		for(int i = 0; i < params.zkp_iterations; i++) {
			// compute g^{ {a^x b^r} h^v} mod p2
			c[i] = challengeCalculation(commentPrivate.getSerialNumber(), r[i], v[i]);
		}
		
		for (int i = 0; i < params.zkp_iterations; i++) {
			string += c[i].toString();
		}
		byte[] getbytes = string.getBytes();
		byte[] gethash = param.Hash.calculateHash(getbytes);
		this.hash = gethash;
		byte[] hashbytes = this.hash;
		//System.out.println(hashbytes);
		for (int i =0; i < params.zkp_iterations; i++) {
			
			int bit = i % 8;
			int by = i / 8;
			
			boolean challenge_bit = ((hashbytes[by] >> bit) & 0x01)==0? false:true;
			if (challenge_bit) {
				s_notprime[i] = r[i];
				sprime[i] = v[i];
			}else {
				s_notprime[i] = r[i].subtract(commentPrivate.getRandomness());
				sprime[i] = v[i].subtract(commitmentToCoin.getRandomness().multiply(b.modPow(r[i].subtract(commentPrivate.getRandomness()), params.serialNumberSoKCommitmentGroup.groupOrder)));
			}
			
		}
		//System.out.println(s_notprime);
		//System.out.println(sprime);
	}
	
	
	public BigInteger challengeCalculation( BigInteger a_exp, BigInteger b_exp, BigInteger h_exp) {
		
		BigInteger a = params.coinCommitmentGroup.g;
		BigInteger b = params.coinCommitmentGroup.h;
		BigInteger g = params.serialNumberSoKCommitmentGroup.g;
		BigInteger h = params.serialNumberSoKCommitmentGroup.h;
		
		BigInteger exponent = (a.modPow(a_exp, params.serialNumberSoKCommitmentGroup.groupOrder)
				.multiply(b.modPow(b_exp, params.serialNumberSoKCommitmentGroup.groupOrder)))
				.mod(params.serialNumberSoKCommitmentGroup.groupOrder);
		
		return (g.modPow(exponent, params.serialNumberSoKCommitmentGroup.modulus)
				.multiply(h.modPow(h_exp, params.serialNumberSoKCommitmentGroup.modulus)))
				.mod(params.serialNumberSoKCommitmentGroup.modulus);
		
	}
	
	
	public Boolean Verify(BigInteger commentSerialNumber, BigInteger valueOfCommitmentToCoin, byte[] msghash) {
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		//System.out.println(s_notprime);
		//System.out.println(sprime);
		BigInteger a = params.coinCommitmentGroup.g;
		BigInteger b = params.coinCommitmentGroup.h;
		BigInteger g = params.serialNumberSoKCommitmentGroup.g;
		BigInteger h = params.serialNumberSoKCommitmentGroup.h;
		
		String string = params.toString()+valueOfCommitmentToCoin.toString()+commentSerialNumber.toString();
		//byte[] getbytes = string.getBytes();
		//byte[] gethash = param.Hash.calculateHash(getbytes);
		byte[] hashbytes = this.hash;
		//System.out.println(hashbytes);
		BigInteger[] tprime = new BigInteger[params.zkp_iterations];
		
        for (int i =0; i < params.zkp_iterations; i++) {
			
			int bit = i % 8;
			int by = i / 8;
			
			boolean challenge_bit = ((hashbytes[by] >> bit) & 0x01)==0? false:true;
			
			if (challenge_bit) {
				tprime[i] = challengeCalculation(commentSerialNumber, s_notprime[i], sprime[i]);
			}else {
				BigInteger exp = b.modPow(s_notprime[i], params.serialNumberSoKCommitmentGroup.groupOrder);
				tprime[i] = ((valueOfCommitmentToCoin.modPow(exp, params.serialNumberSoKCommitmentGroup.modulus)
						.mod(params.serialNumberSoKCommitmentGroup.modulus))
						.multiply(h.modPow(sprime[i], params.serialNumberSoKCommitmentGroup.modulus)
						.mod(params.serialNumberSoKCommitmentGroup.modulus)))
						.mod(params.serialNumberSoKCommitmentGroup.modulus);			
			}	
		}
        
        for (int i = 0; i < params.zkp_iterations; i++) {
        	string += tprime[i].toString();
        }
        
        byte[] getbytes = string.getBytes();
        byte[] gethash = param.Hash.calculateHash(getbytes);
        
		//return gethash == this.hash;
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        return param.Hash.equals(gethash, this.hash);
	}
	
}
