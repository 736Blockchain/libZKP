package AccumulatorProofOfKnowledge;

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

public class AccumulatorProofOfKnowledge {

	private AccumulatorAndProofParams params;
	
	private BigInteger C_e;
	private BigInteger C_u;
	private BigInteger C_r;

	private BigInteger st_1;
	private BigInteger st_2;
	private BigInteger st_3;

	private BigInteger t_1;
	private BigInteger t_2;
	private BigInteger t_3;
	private BigInteger t_4;

	private BigInteger s_alpha;
	private BigInteger s_beta;
	private BigInteger s_zeta;
	private BigInteger s_sigma;
	private BigInteger s_eta;
	private BigInteger s_epsilon;
	private BigInteger s_delta;
	private BigInteger s_xi;
	private BigInteger s_phi;
	private BigInteger s_gamma;
	private BigInteger s_psi;
	
	public AccumulatorProofOfKnowledge(AccumulatorAndProofParams p) {
		params = p;
	}
	
	//														p.accumulatorParams,            fullCommitmentToCoinUnderAccParams, witness, a					
	public AccumulatorProofOfKnowledge(AccumulatorAndProofParams p, Commitment commitmentToComment, AccumulatorWitness witness, Accumulator a) {
		params = p;
		
		BigInteger sg = params.accumulatorPoKCommitmentGroup.g;//Pa.g
		BigInteger sh = params.accumulatorPoKCommitmentGroup.h;//Pa.h
		
		BigInteger g_n = params.accumulatorQRNCommitmentGroup.g;
		BigInteger h_n = params.accumulatorQRNCommitmentGroup.h;
		
		BigInteger e = commitmentToComment.getContents();//Cs的g右肩上的部分，也就是c
		BigInteger r = commitmentToComment.getRandomness();//Cs的rs
		
		BigInteger r_1 = BigNumber.randomBigInteger(params.accumulatorModulus.divide(BigInteger.valueOf(4)));
		BigInteger r_2 = BigNum.BigNumber.randomBigInteger(params.accumulatorModulus.divide(BigInteger.valueOf(4))); 
		BigInteger r_3 = BigNum.BigNumber.randomBigInteger(params.accumulatorModulus.divide(BigInteger.valueOf(4)));
		
		this.C_e = g_n.modPow(e, params.accumulatorModulus).multiply(h_n.modPow(r_1, params.accumulatorModulus));
		this.C_u = (witness.getValue()).multiply(h_n.modPow(r_2, params.accumulatorModulus));
		this.C_r = g_n.modPow(r_2, params.accumulatorModulus).multiply(h_n.modPow(r_3, params.accumulatorModulus));
		
		BigInteger r_alpha = BigNum.BigNumber.randomBigInteger(params.maxCoinValue.multiply(BigInteger.valueOf(2).pow(params.k_prime + params.k_dprime)));		
		if (
				(BigNumber.randomBigInteger(BigInteger.valueOf(3)).mod(BigInteger.valueOf(2))).compareTo(BigInteger.ONE) == 0
				) {
			r_alpha = BigInteger.ZERO.subtract(r_alpha);
		}
		
		BigInteger r_gamma = BigNum.BigNumber.randomBigInteger(params.accumulatorPoKCommitmentGroup.modulus);
		BigInteger r_phi = BigNum.BigNumber.randomBigInteger(params.accumulatorPoKCommitmentGroup.modulus);
		BigInteger r_psi = BigNum.BigNumber.randomBigInteger(params.accumulatorPoKCommitmentGroup.modulus);
		BigInteger r_sigma = BigNum.BigNumber.randomBigInteger(params.accumulatorPoKCommitmentGroup.modulus);
		BigInteger r_xi = BigNum.BigNumber.randomBigInteger(params.accumulatorPoKCommitmentGroup.modulus);
		
		BigInteger r_epsilon = BigNum.BigNumber.randomBigInteger(params.accumulatorModulus.divide(BigInteger.valueOf(4)).multiply(BigInteger.valueOf(2).pow(params.k_prime + params.k_dprime)));		
		if ((BigNumber.randomBigInteger(BigInteger.valueOf(3)).mod(BigInteger.valueOf(2))).compareTo(BigInteger.ONE) == 0) {
			r_epsilon = BigInteger.ZERO.subtract(r_epsilon);
		}
		
		BigInteger r_eta = BigNum.BigNumber.randomBigInteger(params.accumulatorModulus.divide(BigInteger.valueOf(4)).multiply(BigInteger.valueOf(2).pow(params.k_prime + params.k_dprime)));		
		if ((BigNumber.randomBigInteger(BigInteger.valueOf(3)).mod(BigInteger.valueOf(2))).compareTo(BigInteger.ONE) == 0) {
			r_eta = BigInteger.ZERO.subtract(r_eta);
		}
		
		BigInteger r_zeta = BigNum.BigNumber.randomBigInteger(params.accumulatorModulus.divide(BigInteger.valueOf(4)).multiply(BigInteger.valueOf(2).pow(params.k_prime + params.k_dprime)));		
		if ((BigNumber.randomBigInteger(BigInteger.valueOf(3)).mod(BigInteger.valueOf(2))).compareTo(BigInteger.ONE) == 0) {
			r_zeta = BigInteger.ZERO.subtract(r_zeta);
		}
		
		BigInteger r_beta = BigNum.BigNumber.randomBigInteger(params.accumulatorModulus.divide(BigInteger.valueOf(4)).multiply(BigInteger.valueOf(2).pow(params.k_prime + params.k_dprime)));		
		if ((BigNumber.randomBigInteger(BigInteger.valueOf(3)).mod(BigInteger.valueOf(2))).compareTo(BigInteger.ONE) == 0) {
			r_beta = BigInteger.ZERO.subtract(r_beta);
		}
		
		BigInteger r_delta = BigNum.BigNumber.randomBigInteger(params.accumulatorModulus.divide(BigInteger.valueOf(4)).multiply(BigInteger.valueOf(2).pow(params.k_prime + params.k_dprime)));		
		if ((BigNumber.randomBigInteger(BigInteger.valueOf(3)).mod(BigInteger.valueOf(2))).compareTo(BigInteger.ONE) == 0) {
			r_delta = BigInteger.ZERO.subtract(r_delta);
		}
		
		this.st_1 = (sg.modPow(r_alpha, params.accumulatorPoKCommitmentGroup.modulus).multiply(sh.modPow(r_phi, params.accumulatorPoKCommitmentGroup.modulus))).mod(params.accumulatorPoKCommitmentGroup.modulus);
		this.st_2 = (((commitmentToComment.getCommitmentValue().multiply(sg.modInverse(params.accumulatorPoKCommitmentGroup.modulus))).modPow(r_gamma, params.accumulatorPoKCommitmentGroup.modulus)).multiply(sh.modPow(r_psi, params.accumulatorPoKCommitmentGroup.modulus))).mod(params.accumulatorPoKCommitmentGroup.modulus);
		this.st_3 = ((sg.multiply(commitmentToComment.getCommitmentValue()).modPow(r_sigma, params.accumulatorPoKCommitmentGroup.modulus)).multiply(sh.modPow(r_xi, params.accumulatorPoKCommitmentGroup.modulus))).mod(params.accumulatorPoKCommitmentGroup.modulus);
		
		this.t_1 = (h_n.modPow(r_zeta, params.accumulatorModulus).multiply(g_n.modPow(r_epsilon, params.accumulatorModulus))).mod(params.accumulatorModulus);
		this.t_2 = (h_n.modPow(r_eta, params.accumulatorModulus).multiply(g_n.modPow(r_alpha, params.accumulatorModulus))).mod(params.accumulatorModulus);
		this.t_3 = (C_u.modPow(r_alpha, params.accumulatorModulus).multiply(h_n.modInverse(params.accumulatorModulus).modPow(r_beta, params.accumulatorModulus))).mod(params.accumulatorModulus);
		this.t_4 = (C_r.modPow(r_alpha, params.accumulatorModulus).multiply(h_n.modInverse(params.accumulatorModulus).modPow(r_delta, params.accumulatorModulus)).multiply((g_n.modInverse(params.accumulatorModulus)).modPow(r_beta, params.accumulatorModulus))).mod(params.accumulatorModulus);
		
		String string = params.toString()+sg.toString()+sh.toString()+g_n.toString()+h_n.toString()+commitmentToComment.getCommitmentValue().toString()+C_e.toString()+C_u.toString()+C_r.toString()+st_1.toString()+st_2.toString()+st_3.toString()+t_1.toString()+t_2.toString()+t_3.toString()+t_4.toString();
		byte[] getbytes = string.getBytes();
		byte[] gethash = param.Hash.calculateHash(getbytes);
		BigInteger c = new BigInteger(gethash);
		
		this.s_alpha = r_alpha.subtract(c.multiply(e));
		this.s_beta = r_beta.subtract(c.multiply(r_2).multiply(e));
		this.s_zeta = r_zeta.subtract(c.multiply(r_3));
		this.s_sigma = r_sigma.subtract(c.multiply((e.add(BigInteger.ONE)).modInverse(params.accumulatorPoKCommitmentGroup.groupOrder)));
		this.s_eta = r_eta.subtract(c.multiply(r_1));
		this.s_epsilon = r_epsilon.subtract(c.multiply(r_2));
		this.s_delta = r_delta.subtract(c.multiply(r_3).multiply(e));
		this.s_xi = r_xi.add(c.multiply(r).multiply((e.add(BigInteger.ONE)).modInverse(params.accumulatorPoKCommitmentGroup.groupOrder)));
		this.s_phi = r_phi.subtract(c.multiply(r)).mod(params.accumulatorPoKCommitmentGroup.groupOrder);
		this.s_gamma = r_gamma.subtract(c.multiply((e.subtract(BigInteger.ONE)).modInverse(params.accumulatorPoKCommitmentGroup.groupOrder)));
		this.s_psi = r_psi.add(c.multiply(r).multiply((e.subtract(BigInteger.ONE)).modInverse(params.accumulatorPoKCommitmentGroup.groupOrder)));
		
		}
		
	public boolean  Verify(Accumulator a, BigInteger valueOfCommitmentToComment) {
		 
		BigInteger sg = params.accumulatorPoKCommitmentGroup.g;
		BigInteger sh = params.accumulatorPoKCommitmentGroup.h;
		BigInteger g_n = params.accumulatorQRNCommitmentGroup.g;
		BigInteger h_n = params.accumulatorQRNCommitmentGroup.h;
		
		String string = params.toString()+sg.toString()+sh.toString()+g_n.toString()+h_n.toString()+valueOfCommitmentToComment.toString()+C_e.toString()+C_u.toString()+C_r.toString()+st_1.toString()+st_2.toString()+st_3.toString()+t_1.toString()+t_2.toString()+t_3.toString()+t_4.toString();
		byte[] getbytes = string.getBytes();
		byte[] gethash = param.Hash.calculateHash(getbytes);
		BigInteger c = new BigInteger(gethash);
		
		BigInteger st_1_prime = (valueOfCommitmentToComment.modPow(c, params.accumulatorPoKCommitmentGroup.modulus).multiply(sg.modPow(s_alpha, params.accumulatorPoKCommitmentGroup.modulus)).multiply(sh.modPow(s_phi, params.accumulatorPoKCommitmentGroup.modulus))).mod(params.accumulatorPoKCommitmentGroup.modulus);
		BigInteger st_2_prime = (sg.modPow(c, params.accumulatorPoKCommitmentGroup.modulus).multiply((valueOfCommitmentToComment.multiply(sg.modInverse(params.accumulatorPoKCommitmentGroup.modulus))).modPow(s_gamma, params.accumulatorPoKCommitmentGroup.modulus)).multiply(sh.modPow(s_psi, params.accumulatorPoKCommitmentGroup.modulus))).mod(params.accumulatorPoKCommitmentGroup.modulus);
		BigInteger st_3_prime = (sg.modPow(c, params.accumulatorPoKCommitmentGroup.modulus).multiply((sg.multiply(valueOfCommitmentToComment)).modPow(s_sigma, params.accumulatorPoKCommitmentGroup.modulus)).multiply(sh.modPow(s_xi, params.accumulatorPoKCommitmentGroup.modulus))).mod(params.accumulatorPoKCommitmentGroup.modulus);

		BigInteger t_1_prime = (C_r.modPow(c, params.accumulatorModulus).multiply(h_n.modPow(s_zeta, params.accumulatorModulus)).multiply(g_n.modPow(s_epsilon, params.accumulatorModulus))).mod(params.accumulatorModulus);
		BigInteger t_2_prime = (C_e.modPow(c, params.accumulatorModulus).multiply(h_n.modPow(s_eta, params.accumulatorModulus)).multiply(g_n.modPow(s_alpha, params.accumulatorModulus))).mod(params.accumulatorModulus);
		BigInteger t_3_prime = (((a.getValue()).modPow(c, params.accumulatorModulus)).multiply(C_u.modPow(s_alpha, params.accumulatorModulus)).multiply((h_n.modInverse(params.accumulatorModulus)).modPow(s_beta, params.accumulatorModulus))).mod(params.accumulatorModulus);
		BigInteger t_4_prime = (C_r.modPow(s_alpha, params.accumulatorModulus).multiply(h_n.modInverse(params.accumulatorModulus).modPow(s_delta, params.accumulatorModulus)).multiply((g_n.modInverse(params.accumulatorModulus)).modPow(s_beta, params.accumulatorModulus))).mod(params.accumulatorModulus);
	
		
		boolean result = false;
		 
		boolean result_st1 = (st_1.compareTo(st_1_prime) == 0);
		boolean result_st2 = (st_2.compareTo(st_2_prime) == 0);
		boolean result_st3 = (st_3.compareTo(st_3_prime) == 0);
		
		boolean result_t1 = (t_1.compareTo(t_1_prime) == 0);
		boolean result_t2 = (t_2.compareTo(t_2_prime) == 0);
		boolean result_t3 = (t_3.compareTo(t_3_prime) == 0);
		boolean result_t4 = (t_4.compareTo(t_4_prime) == 0);
		
		
		boolean result_range = (s_alpha.compareTo(params.maxCoinValue.multiply(BigInteger.valueOf(2).pow(params.k_prime + params.k_dprime +1)).negate()) >= 0) && (s_alpha.compareTo(params.maxCoinValue.multiply(BigInteger.valueOf(2).pow(params.k_prime + params.k_dprime +1))) <= 0);
		
		System.out.print("result_st1:");
		System.out.println(result_st1);
		System.out.print("result_st2:");
		System.out.println(result_st2);
		System.out.print("result_st3:");
		System.out.println(result_st3);
		System.out.print("result_t1:");
		System.out.println(result_t1);
		System.out.print("result_t2:");
		System.out.println(result_t2);
		System.out.print("result_t3:");//等于false
		System.out.println(result_t3);
		System.out.print("result_t4:");
		System.out.println(result_t4);
		System.out.print("result_range:");
		System.out.println(result_range);
		
		result = result_st1 && result_st2 && result_st3 && result_t1 && result_t2 && result_t3 && result_t4 && result_range;
		 
		return result;
	}
	
	
}
