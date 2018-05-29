package Accumulator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.lang.String;
import param.Params;
import param.AccumulatorAndProofParams;
import comment.CommentPrivate;
import comment.CommentPublic;
import Exception.libzkpException;

public class Accumulator {

	private AccumulatorAndProofParams params;
	private BigInteger accumulateValue;
//	private String comment;
	
	
//	public Accumulator(AccumulatorAndProofParams p) {
//		this.params = p;
//		if (!params.initialized) {
//			throw new libzkpException("Invalid parameters for accumulator");
//		}
//		this.accumulateValue = this.params.accumulatorBase;
//	}
	
	/**
	 * 生成一个Accumulator  参数为Params
	 * @param p Params
	 */
    public Accumulator(Params p) {
    		this.params = p.accumulatorAndproofParams;
		if (!params.initialized) {
			throw new libzkpException("Invalid parameters for accumulator");
		}
		this.accumulateValue = this.params.accumulatorBase;
	}
    
    /**
     * 拷贝一个Accumulator(生成一个跟参数一样的Accumulator对象)
     * @param 一个Accumulator
     */
    public Accumulator(Accumulator a) {
	    	this.params = a.params;
	    	this.accumulateValue = a.accumulateValue;
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
    
    /**
     * 返回一个CommentPrivate 的 witness
     * @param commentprivatelist 包含一段时间内生成的所有commentPrivate
     * @param commentprivate 需要生成witness的comment
     * @return 该commentprivate的witness
     */
    public static AccumulatorWitness getAccumulatorWitness(ArrayList<CommentPrivate> commentprivatelist, Params params,  CommentPrivate comment_private) {
    		Accumulator accumulatorValueWithoutOneCommentPrivate =  new Accumulator(params);
    		for(CommentPrivate commentprivate:commentprivatelist) {
    			if(commentprivate != comment_private) 
    				accumulatorValueWithoutOneCommentPrivate.AddCommentPublic(commentprivate.getCommentPublic());
    		}
    		
    		return new AccumulatorWitness(params, accumulatorValueWithoutOneCommentPrivate, comment_private.getCommentPublic());
    }
} 
   
