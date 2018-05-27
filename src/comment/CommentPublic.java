package comment;

import java.math.BigInteger;
import param.Params;
import Exception.libzkpException;

public class CommentPublic {
	
	private Params params;
	private BigInteger commitmentvalue;//c
	private String comment;	
	
	
	public CommentPublic(Params p, BigInteger commitment_value, String PublicCommentString) {
		params = p;
		commitmentvalue = commitment_value;
		comment = PublicCommentString;
		if (this.params.initialized == false) {
			throw new libzkpException("Params are not initialized yet.");
		}
	}
	
	public boolean EqualCommentPublic(CommentPublic c) {
		return this.commitmentvalue == c.commitmentvalue;
	}
	
	public boolean NotEqualCommentPublic(CommentPublic c) {
		return !(this.commitmentvalue == c.commitmentvalue);
	}
	
	public String getComment() {
		return this.comment;
	}
	
	/**
	 * 返回保存在评论公开部分的承诺值
	 * @return commitmentvalue(BigInteger)
	 */
	public BigInteger getValue() {
		return this.commitmentvalue;
	}
	
	/**
	 * 检验生成的承诺值c是否符合要求
	 * 要求: 小于最大值，大于最小值，是个素数.
	 * @return 检验结果true/false
	 */
	public Boolean validate(){
	    return (this.params.accumulatorAndproofParams.minCommitmentValue.compareTo(this.commitmentvalue) == -1) 
	    		&& (this.commitmentvalue.compareTo(this.params.accumulatorAndproofParams.maxCommitmentValue) == -1) 
	    		&& this.commitmentvalue.isProbablePrime(10);
		//return this.value.isProbablePrime(10);
	}
	
}
