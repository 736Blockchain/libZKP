package Accumulator;

import java.math.BigInteger;
import param.Params;
import param.AccumulatorAndProofParams;
import comment.CommentPrivate;
import comment.CommentPublic;

public class AccumulatorWitness {

	private Params params;
	private Accumulator witness;
	private CommentPublic element;
	
	public AccumulatorWitness(Params p, Accumulator checkpoint, CommentPublic commentpublic) {
		params = p;
		witness = new Accumulator(checkpoint);
		element = commentpublic;
	}
	
	public void AddElement(CommentPublic c) {
		if(element != c) {
			witness = witness.AddCommentPublic(c);
		}
	}
	
	public BigInteger getValue() {
		return this.witness.getValue();
	}
	
	public boolean VerifyWitness(Accumulator a, CommentPublic comment) {
		Accumulator temp = new Accumulator(witness);
		temp = temp.AddCommentPublic(element);
		//return (temp.equals(a) && this.element.equals(comment));
		return (temp.EqualAccumulator(a) && this.element == comment);
	}
	
	public AccumulatorWitness AddAccumulatorWitness(CommentPublic AddComment) {
		this.AddElement(AddComment);
		return this;
	}
	
	
}
