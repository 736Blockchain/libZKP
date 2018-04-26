package Commitment;

import java.math.BigInteger;
import BigNum.BigNumber;
import param.Hash;
import param.IntegerGroupParams;
import param.Params;
import Exception.libzkpException;

public class Commitment {

	private IntegerGroupParams params;
	private BigInteger commitmentValue;
	private BigInteger randomness;//h右肩上的部分
	private BigInteger contents;//g右肩上的部分
	
	public Commitment(IntegerGroupParams p, BigInteger value){
		params = p;
		//当在CoinSpend中生成Commitment时，value 为 c
		//当在CommentPrivate中生成Comment时，value是随机生成的随机数(其实是作为CommentPrivate的serialNumber)
		contents = value;//contents就是生成Commitment时在g右肩上的部分
		this.randomness = BigNumber.randomBigInteger(params.groupOrder);
		this.commitmentValue = (params.g.modPow(this.contents, params.modulus).multiply(params.h.modPow(this.randomness, params.modulus))).mod(params.modulus);
		//哈希
//		this.commitmentValue = new BigInteger(Hash.MD5(((params.g.modPow(this.contents, params.modulus).multiply(params.h.modPow(this.randomness, params.modulus))).mod(params.modulus)).toString()));
		//BigInteger hashValue = new BigInteger(Hash.MD5(this.commitmentValue.toString()));
		//this.commitmentValue = hashValue;
	}
	
	
	public BigInteger getCommitmentValue() {
		return this.commitmentValue;
	}
	
	public BigInteger getRandomness() {
		return this.randomness;
	}
	
	public BigInteger getContents() {
		return this.contents;
	}
	
}
