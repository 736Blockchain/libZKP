package CoinSpend;

import java.math.BigInteger;
import BigNum.BigNumber;
import java.lang.String;
import param.Params;
import param.Hash;
import param.AccumulatorAndProofParams;
import param.IntegerGroupParams;
import param.ParamGeneration;
import comment.CommentPrivate;
import comment.CommentPublic;
import Commitment.Commitment;
import Commitment.CommitmentProofOfKnowledge;
import Accumulator.Accumulator;
import Accumulator.AccumulatorWitness;
import AccumulatorProofOfKnowledge.AccumulatorProofOfKnowledge;
import SerialNumberSignatureOfKnowledge.SerialNumberSignatureOfKnowledge;
import SpendMetaData.SpendMetaData;
import Exception.libzkpException;

public class CoinSpend {

	private Params params;
	private String PublicComment;
	private BigInteger accCommitmentToCoinValue;
	private BigInteger serialCommitmentToCoinValue;
	private BigInteger coinSerialNumber;
	private AccumulatorProofOfKnowledge accumulatorPoK;
	private SerialNumberSignatureOfKnowledge serialNumberSoK;
	private CommitmentProofOfKnowledge commitmentPoK;
	
	private byte[] signatureHash(SpendMetaData m) {
		String string = m.toString()+serialCommitmentToCoinValue.toString()+accCommitmentToCoinValue.toString()+commitmentPoK+accumulatorPoK.toString();
		byte[] getbytes = string.getBytes();
		byte[] gethash = param.Hash.calculateHash(getbytes);
		byte[] hash = gethash;
		
		return hash;
	}
	
	public CoinSpend(Params p, CommentPrivate comment, Accumulator a, AccumulatorWitness witness, SpendMetaData m) {
		params = p;
		PublicComment = comment.getCommentPublic().getComment();//取出评论字符串
		coinSerialNumber = comment.getSerialNumber();//CommentPrivate里面的serialNumber
		accumulatorPoK = new AccumulatorProofOfKnowledge(p.accumulatorAndproofParams);
		serialNumberSoK = new SerialNumberSignatureOfKnowledge(p);
		commitmentPoK = new CommitmentProofOfKnowledge(p.serialNumberSoKCommitmentGroup, p.accumulatorAndproofParams.accumulatorPoKCommitmentGroup);
		
		if (!(witness.VerifyWitness(a, comment.getCommentPublic()))) {
			throw new libzkpException("Accumulator witness does not verify");
		}
System.out.println("--------witness verify success");
		if (!HasValidSerial()) {
			throw new libzkpException("Invalid serial # range");
		}
		
		
		// 1: Generate two separate commitments to the public comment (C).
		Commitment fullCommitmentToCoinUnderSerialParams = new Commitment(p.serialNumberSoKCommitmentGroup,comment.getCommentPublic().getValue());
		this.serialCommitmentToCoinValue = fullCommitmentToCoinUnderSerialParams.getCommitmentValue();
		//System.out.println(this.serialCommitmentToCoinValue);
		Commitment fullCommitmentToCoinUnderAccParams = new Commitment(p.accumulatorAndproofParams.accumulatorPoKCommitmentGroup, comment.getCommentPublic().getValue());		
		this.accCommitmentToCoinValue = fullCommitmentToCoinUnderAccParams.getCommitmentValue();
		//System.out.println(this.accCommitmentToCoinValue);
		// 2. Generate a ZK proof that the two commitments contain the same public comment.
		this.commitmentPoK = new CommitmentProofOfKnowledge(
				p.serialNumberSoKCommitmentGroup,
				p.accumulatorAndproofParams.accumulatorPoKCommitmentGroup,
				fullCommitmentToCoinUnderSerialParams, 
				fullCommitmentToCoinUnderAccParams);
		
		// Now generate the two core ZK proofs:
		// 3. Proves that the committed public comment is in the Accumulator (PoK of "witness")
		this.accumulatorPoK = new AccumulatorProofOfKnowledge(p.accumulatorAndproofParams, fullCommitmentToCoinUnderAccParams, witness, a);
		
		// 4. Proves that the comment is correct w.r.t. serial number and hidden comment secret
		// (This proof is bound to the comment 'metadata', i.e., transaction hash)
		
		this.serialNumberSoK = new SerialNumberSignatureOfKnowledge(p, comment, fullCommitmentToCoinUnderSerialParams, signatureHash(m));
			
	}
	 

	public BigInteger getCoinSerialNumber() {
		return this.coinSerialNumber;
	}
	
	public String getComment() {
		return this.PublicComment;
	}

	//Verify both of the sub-proofs using the given meta-data
	public Boolean Verify(Accumulator a, SpendMetaData m) {
		System.out.println("	\n\"commitmentPoK & accumulatorPoK & serialNumberSoK\" verify		");
		System.out.println("===========================");
		System.out.println(commitmentPoK.Verify(serialCommitmentToCoinValue, accCommitmentToCoinValue));
		System.out.println("===========================");
		System.out.println(accumulatorPoK.Verify(a, accCommitmentToCoinValue));
		System.out.println("===========================");
		System.out.println(serialNumberSoK.Verify(coinSerialNumber, serialCommitmentToCoinValue, signatureHash(m)));
		return   /*(a.getComment() == this.getComment())
				&& */commitmentPoK.Verify(serialCommitmentToCoinValue, accCommitmentToCoinValue)
		        && accumulatorPoK.Verify(a, accCommitmentToCoinValue)
		        && serialNumberSoK.Verify(coinSerialNumber, serialCommitmentToCoinValue, signatureHash(m));
	}
	
	
	public Boolean HasValidSerial() {
		 return coinSerialNumber.compareTo(BigInteger.ZERO) == 1 && coinSerialNumber.compareTo(params.coinCommitmentGroup.groupOrder) == -1;
	}
	

}
