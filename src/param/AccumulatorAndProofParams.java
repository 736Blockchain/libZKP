package param;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * 用于Accumulator的参数集合
 * @category
 * 包括  模数(accumulatorModulus)
 * 	    累加器初始值(accumulatorBase)
 * 		承诺值的范围(minCommitmentValue ~ maxCommitmentValue)
 * @category
 * 还包括两个群
 * 		①不知道什么群 accumulatorPoKCommitmentGroup
 * 		②不知道什么群 accumulatorQRNCommitmentGroup
 * @author jiaxyan
 *
 */
public class AccumulatorAndProofParams  implements Serializable{
	
	public Boolean initialized;
	
	//累加器Accumulator用的模数，由两个不可分解的安全素数产生
	//Product of two safe primes who's factorization is unknown.
	public BigInteger accumulatorModulus;
	
//累加器的初始值  一个随机的Quadratic residue mod n thats not 1 （二次剩余）
	public BigInteger accumulatorBase;
	
	//累加器证明 accumulator proof 要求的承诺值下限
	public BigInteger minCommitmentValue;
	
	//累加器证明 accumulator proof 要求的承诺值上限
	public BigInteger maxCommitmentValue;
	
	/*
	 * 用来生成承诺的第二个群（自己就是一个对serial number的的承诺）
	 * 根据dynamic的论文的限制，这个和serialNumberSokCommitment不一样
	 */
	public IntegerGroupParams accumulatorPoKCommitmentGroup;
	
	/*
	 * Hidden order quadratic residue group mod N.
	 * 用在累加器证明中
	 */
	public IntegerGroupParams accumulatorQRNCommitmentGroup;
	
	/*
	 * 安全参数
	 * 在accumulator proof中使用的 challenge 的比特长度bit length
	 */
	public int k_prime;
	
	/*
	 * 安全参数
	 * The statistical zero-knowledgeness of the accumulator proof.
	 */
	public int k_dprime;
	
	/**
	 * @param N					一个可信的RSA模数
	 * @param securityLevel 		一个用对称比特(symmetric bits)构造的安全等级水平(默认是80)
	 * 
	 * 从模数 N 构建一套Zerocoin的参数(不知道什么有用什么没用)
	 * 用一种可验证的，确定(verifiable, deterministic)的步骤，在N的基础上计算全部剩下的参数(group descriptions etc.)，
	 * 
	 * Note:
	 * 这个构造方法的假设基础是：
	 * "N" encodes a valid RSA-style modulus of the form "e1 * e2" where
	 * "e1" and "e2" are safe primes. The factors "e1", "e2" MUST NOT
	 * be known to any party, or the security of Zerocoin is
	 * compromised. The integer "N" must be a MINIMUM of 1024
	 * in length. 3072 bits is strongly recommended.
	 */
	public AccumulatorAndProofParams() {
		accumulatorPoKCommitmentGroup = new IntegerGroupParams();
		accumulatorQRNCommitmentGroup = new IntegerGroupParams();
		this.initialized = false;
	}
	

}
