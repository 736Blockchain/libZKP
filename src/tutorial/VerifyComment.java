package tutorial;

import java.math.BigInteger;

import Accumulator.Accumulator;
import Accumulator.AccumulatorWitness;
import AccumulatorProofOfKnowledge.AccumulatorProofOfKnowledge;
import Commitment.Commitment;
import Commitment.CommitmentProofOfKnowledge;
import SerialNumberSignatureOfKnowledge.SerialNumberSignatureOfKnowledge;
import comment.CommentPrivate;
import param.Params;

public class VerifyComment {
	public static boolean verifyWithoutAccumu(CommentPrivate commentprivate, Params p) {
		// 1: Generate two separate commitments to the public comment (C).
		Commitment fullCommitmentToCoinUnderSerialParams = new Commitment(p.serialNumberSoKCommitmentGroup, commentprivate.getCommentPublic().getValue());
		BigInteger serialCommitmentToCoinValue = fullCommitmentToCoinUnderSerialParams.getCommitmentValue();
		//System.out.println(this.serialCommitmentToCoinValue);
		Commitment fullCommitmentToCoinUnderAccParams = new Commitment(p.accumulatorAndproofParams.accumulatorPoKCommitmentGroup, commentprivate.getCommentPublic().getValue());		
		BigInteger accCommitmentToCoinValue = fullCommitmentToCoinUnderAccParams.getCommitmentValue();
		//System.out.println(this.accCommitmentToCoinValue);
		// 2. Generate a ZK proof that the two commitments contain the same public comment.
		CommitmentProofOfKnowledge commitmentPoK = new CommitmentProofOfKnowledge(
				p.serialNumberSoKCommitmentGroup,
				p.accumulatorAndproofParams.accumulatorPoKCommitmentGroup,
				fullCommitmentToCoinUnderSerialParams, 
				fullCommitmentToCoinUnderAccParams);
		
		SerialNumberSignatureOfKnowledge serialNumberSoK = new SerialNumberSignatureOfKnowledge(p, commentprivate, fullCommitmentToCoinUnderSerialParams);
		
		return commitmentPoK.Verify(serialCommitmentToCoinValue, accCommitmentToCoinValue)
				&& serialNumberSoK.Verify(commentprivate.getSerialNumber(), serialCommitmentToCoinValue);
	}
	
	/**
	 * 验证一个CommentPrivate是否被聚合到 聚合器 中
	 * @param commentprivate 需要Verify的CommentPrivate
	 * @param p 生成这个CommentPrivate的参数params
	 * @param accumulator 当前一段时间这些Comment的聚合器
	 * @param witness 跟这个CommentPrivate相对应的witness
	 * @return 验证true/false
	 */
	public static boolean verifyWithAccumu(CommentPrivate commentprivate, Params p, Accumulator accumulator, AccumulatorWitness witness) {
		// 1: Generate two separate commitments to the public comment (C).
		Commitment fullCommitmentToCoinUnderSerialParams = new Commitment(p.serialNumberSoKCommitmentGroup, commentprivate.getCommentPublic().getValue());
		BigInteger serialCommitmentToCoinValue = fullCommitmentToCoinUnderSerialParams.getCommitmentValue();
		//System.out.println(this.serialCommitmentToCoinValue);
		Commitment fullCommitmentToCoinUnderAccParams = new Commitment(p.accumulatorAndproofParams.accumulatorPoKCommitmentGroup, commentprivate.getCommentPublic().getValue());		
		BigInteger accCommitmentToCoinValue = fullCommitmentToCoinUnderAccParams.getCommitmentValue();
		//System.out.println(this.accCommitmentToCoinValue);
		// 2. Generate a ZK proof that the two commitments contain the same public comment.
		CommitmentProofOfKnowledge commitmentPoK = new CommitmentProofOfKnowledge(
				p.serialNumberSoKCommitmentGroup,
				p.accumulatorAndproofParams.accumulatorPoKCommitmentGroup,
				fullCommitmentToCoinUnderSerialParams,
				fullCommitmentToCoinUnderAccParams);
		AccumulatorProofOfKnowledge accumulatorPoK = new AccumulatorProofOfKnowledge(p.accumulatorAndproofParams, fullCommitmentToCoinUnderAccParams, witness, accumulator);
		SerialNumberSignatureOfKnowledge serialNumberSoK = new SerialNumberSignatureOfKnowledge(p, commentprivate, fullCommitmentToCoinUnderSerialParams);
		
		return commitmentPoK.Verify(serialCommitmentToCoinValue, accCommitmentToCoinValue)
				&& serialNumberSoK.Verify(commentprivate.getSerialNumber(), serialCommitmentToCoinValue)
				&& accumulatorPoK.Verify(accumulator, fullCommitmentToCoinUnderAccParams.getCommitmentValue());
	}
}
