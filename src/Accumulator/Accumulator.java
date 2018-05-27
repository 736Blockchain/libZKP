package Accumulator;

import java.math.BigInteger;
import java.lang.String;
import param.Params;
import param.AccumulatorAndProofParams;
import comment.CommentPrivate;
import comment.CommentPublic;
import Exception.libzkpException;

public class Accumulator {

	private AccumulatorAndProofParams params;
	private BigInteger accumulateValue;
	private String comment;
	
	
	public Accumulator(AccumulatorAndProofParams p, String c) {
		
		this.params = p;
		this.comment = c;
		
		if (!params.initialized) {
			throw new libzkpException("Invalid parameters for accumulator");
		}
		
		this.accumulateValue = this.params.accumulatorBase;
		
	}
	
    public Accumulator(Params p, String c) {
		
    		this.params = p.accumulatorAndproofParams;
    		this.comment = c;
		
		if (!params.initialized) {
			throw new libzkpException("Invalid parameters for accumulator");
		}
		
		this.accumulateValue = this.params.accumulatorBase;
		
	}
    
    /**
     * copy an accumulator
     * but we leave comment blank
     */
    public Accumulator(Accumulator a) {
    	this.params = a.params;
    	this.accumulateValue = a.accumulateValue;
    	this.comment = "";
    }
    
    /**
     * 聚合进一个评论
     * @param commentpublic
     */
    public void accumulate(CommentPublic commentpublic) {
	    	
	    	if(this.accumulateValue == null) {
	    		throw new libzkpException("Accumulator is not initialized");
	    	}
	    	
	    	/*if(this.comment != comment.getComment()) {
	    		String msg = "Wrong comment for PublicComment. Expected PublicComment of comment: ";
	    		msg += this.comment;
	    		msg += ". Instead, got a PublicComment of comment: ";
	    		msg += comment.getComment();
	    		throw new libzkpException(msg);
	    	}*/
	    	
	    	if(commentpublic.validate()) {
	    		// Compute new accumulator = "old accumulator"^{element} mod N
	    		this.accumulateValue = this.accumulateValue.modPow(commentpublic.getValue(), this.params.accumulatorModulus);
	    	}else {
	    		throw new libzkpException("PublicCoin is not valid");
	    	}
    	
    }
    
    
    /*public String getComment() {
    	return this.getComment();
    }*/
    
    
    public BigInteger getValue() {
    		return this.accumulateValue;
    }
    
    public Accumulator AddCommentPublic(CommentPublic commentpublic) {
    		this.accumulate(commentpublic);
    	return this;
    }
    
    public boolean EqualAccumulator(Accumulator a) {
    		return this.accumulateValue.compareTo(a.accumulateValue)==0;
    }
} 
   
