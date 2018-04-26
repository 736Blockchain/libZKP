package comment;

import java.math.BigInteger;
import param.Params;
import Exception.libzkpException;

public class CommentPublic {
	
	private Params params;
	private BigInteger commitmentvalue;//c
	private String comment;	
	
	
	public CommentPublic(Params p, BigInteger PublicValue, String PublicComment) {
		params = p;
		commitmentvalue = PublicValue;
		comment = PublicComment;
		if (this.params.initialized == false) {
			throw new libzkpException("Params are not initialized");
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
	
	public BigInteger getValue() {
		return this.commitmentvalue;
	}
	
	public Boolean validate(){
	    return (this.params.accumulatorParams.minCoinValue.compareTo(this.commitmentvalue) == -1) && (this.commitmentvalue.compareTo(this.params.accumulatorParams.maxCoinValue) == -1) && this.commitmentvalue.isProbablePrime(10);
		//return this.value.isProbablePrime(10);
	}
	
}
