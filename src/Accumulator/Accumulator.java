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
	private BigInteger value;
	private String comment;
	
	
	public Accumulator(AccumulatorAndProofParams p, String c) {
		
		this.params = p;
		this.comment = c;
		
		if (!params.initialized) {
			throw new libzkpException("Invalid parameters for accumulator");
		}
		
		this.value = this.params.accumulatorBase;
		
	}
	
    public Accumulator(Params p, String c) {
		
    	this.params = p.accumulatorParams;
    	this.comment = c;
		
		if (!params.initialized) {
			throw new libzkpException("Invalid parameters for accumulator");
		}
		
		this.value = this.params.accumulatorBase;
		
	}
    
    /**
     * copy an accumulator
     * but we leave comment blank
     */
    public Accumulator(Accumulator a) {
    	this.params = a.params;
    	this.value = a.value;
    	this.comment = "";
    }
    
    public void accumulate(CommentPublic commentpublic) {
	    	
	    	if(this.value == null) {
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
	    		this.value = this.value.modPow(commentpublic.getValue(), this.params.accumulatorModulus);
	    	}else {
	    		throw new libzkpException("PublicCoin is not valid");
	    	}
    	
    }
    
    
    /*public String getComment() {
    	return this.getComment();
    }*/
    
    
    public BigInteger getValue() {
    	return this.value;
    }
    
    public Accumulator AddCommentPublic(CommentPublic commentpublic) {
    	this.accumulate(commentpublic);
    	return this;
    }
    
    public boolean EqualAccumulator(Accumulator a) {
    	return this.value.compareTo(a.value)==0;
    }
} 
   
