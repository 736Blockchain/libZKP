package tutorial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import param.Params;
import param.Hash;
import comment.CommentPrivate;
import comment.CommentPublic;
import Accumulator.Accumulator;
import Accumulator.AccumulatorWitness;
import SpendMetaData.SpendMetaData;
import CoinSpend.CoinSpend;
import Exception.libzkpException;


/**
 * @file test.java
 * @brief Simple test for all libzerocoin functions
 * @date 
 * @version 1.0.0
 * @author jiaxyan
 *
 */
public class test {
	static String TUTORIAL_TEST_MODULUS = "a8852ebf7c49f01cd196e35394f3b74dd86283a07f57e0a262928e7493d4a3961d93d93c90ea3369719641d626d28b9cddc6d9307b9aabdbffc40b6d6da2e329d079b4187ff784b2893d9f53e9ab913a04ff02668114695b07d8ce877c4c8cac1b12b9beff3c51294ebe349eca41c24cd32a6d09dd1579d3947e5c4dcc30b2090b0454edb98c6336e7571db09e0fdafbd68d8f0470223836e90666a5b143b73b9cd71547c917bf24c0efc86af2eba046ed781d9acb05c80f007ef5a0a5dfca23236f37e698e8728def12554bc80f294f71c040a88eff144d130b24211016a97ce0f5fe520f477e555c9997683d762aff8bd1402ae6938dd5c994780b1bf6aa7239e9d8101630ecfeaa730d2bbc97d39beb057f016db2e28bf12fab4989c0170c2593383fd04660b5229adcd8486ba78f6cc1b558bcd92f344100dff239a8c00dbc4c2825277f24bdd04475bcc9a8c39fd895eff97c1967e434effcb9bd394e0577f4cf98c30d9e6b54cd47d6e447dcf34d67e48e4421691dbe4a7d9bd503abb9";
    static byte DUMMY_TRANSACTION_HASH = 0; 
    static byte DUMMY_ACCUMULATOR_ID = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * What is it:		Parameter loading
		 * Who does it:		P & V
		 * What it does:	load a trusted modulus "N" and generates all associated params from it
		 */
		BigInteger testModulus;//
		testModulus = new BigInteger(TUTORIAL_TEST_MODULUS,16);//把十六进制数转化成大数 不可< 1023
		
//		FileInputStream params_obj_serial = new FileInputStream(file)
		//通过序列化存储params对象 以便实验
		File params_obj = new File("params_obj_serialized.local");
		Params params = null;
		try {
			if(params_obj.exists()) {//尝试本地读取序列化对象 
				System.out.println("There is params Local, Read params Object from Local...\n");
				ObjectInputStream oIn = new ObjectInputStream(new FileInputStream(params_obj));
				params = (Params) oIn.readObject();
				oIn.close();
			}else {//没有本地对象则new Params
				//Set up the Params object 80为默认安全系数
				params_obj.createNewFile();
				params = new Params(testModulus, 80);
				ObjectOutputStream oOut = new ObjectOutputStream(new FileOutputStream(params_obj));
				oOut.writeObject(params);
				oOut.close();
			}
		} catch (IOException e) {
			params_obj.delete();
			System.out.println("[ERROR] IoException In Params Generation.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			params_obj.delete();
			System.out.println("[ERROR] ClassNotFound In Params Generation.");
			e.printStackTrace();
		}finally {
		
		}
		//Test for params
//		System.out.println("========params========");

		// TODO Comment generation method stub
		/*
		 *  What is it:		Comment generation
		 *  Who does it:    client
		 *  What it does:   Generates a new comment using the public parameters.
		 */
		CommentPrivate newComment = new CommentPrivate(params,"abhjghjcd");
		CommentPublic pubComment = newComment.getCommentPublic();
		
//		System.out.println("========pubComment========");
//		System.out.println(pubComment);
//		System.out.println("========pubComment.toString()========");
//		System.out.println(pubComment.toString());
//		System.out.println("========pubComment.toString().length()========");
//		System.out.println(pubComment.toString().length());
//		System.out.println("========pubComment.getComment()========");
//		System.out.println(pubComment.getComment());
//		System.out.println("========pubComment.getValue()========");
//		System.out.println(pubComment.getValue());
//		System.out.println("========pubComment.getValue().bitLength()========");
//		System.out.println(pubComment.getValue().bitLength());
		//System.out.println("========Hash.MD5(pubComment.getValue().toString())========");
		//System.out.println(Hash.MD5(pubComment.getValue().toString()));
		//System.out.println("========Hash.MD5(pubComment.getValue().toString()).length()========");
		//System.out.println(Hash.MD5(pubComment.getValue().toString()).length());
//		System.out.println("=Successfully newed a comment");

		//TODO Comment verification method stub
		/*
		 *  What is it:		Comment verification
		 *  Who does it:    transaction verifier
		 *  What it does:   Verifies the structure of a comment obtained from a COMMENT_MINT transaction.
		 */
		
		if(!pubComment.validate()) {
			System.out.println("Error: comment is not valid!");
		}else {
			System.out.println("=>Successfully generate and valid One testComment\n");
		}
		
		//TODO Accumulator computation method stub
				/*
				 *  What is it:		Accumulator computation
		    	 *  Who does it:    client & transaction verifier
				 *  What it does:   Collects a number of PublicCoin values drawn from the block chain and calculates an accumulator.
				 */
		// Create an empty accumulator object
		Accumulator accumulator = new Accumulator(params,"");
		accumulator.AddCommentPublic(newComment.getCommentPublic());
		
		System.out.println("=>Generate 5 testComments and Accumulate them...");
		// Add several comments to it
		for(int i = 0; i < 5; i++) {
			CommentPrivate testComment = new CommentPrivate(params,"abcd"+""+i);
			accumulator.AddCommentPublic(testComment.getCommentPublic());
			//System.out.println(testComment.getCommentPublic().getComment());
			//System.out.println(testComment.getCommentPublic().getValue().bitLength());
		}
	
		System.out.println("========accumulator.getValue()========");
		System.out.println(accumulator.getValue());
		System.out.println("=>Successfully accumulated testComments");

		//TODO Comment spend method stub
		/*
		 *  What is it:		Accumulator computation
    	 *  Who does it:    client 
		 *  What it does:   Create a new transaction that spends a Comment.
		 */
System.out.println("=>Generate a witness...");
		AccumulatorWitness witness = new AccumulatorWitness(params, accumulator, newComment.getCommentPublic());
		
		SpendMetaData metaData = new SpendMetaData(DUMMY_TRANSACTION_HASH, DUMMY_ACCUMULATOR_ID);
		
		// Construct the CoinSpend object. This acts like a signature on the transaction.
		CoinSpend spend = new CoinSpend(params, newComment, accumulator, witness, metaData);
		
		if (!spend.Verify(accumulator, metaData)) {
			throw new libzkpException("ERROR: Our new CoinSpend transaction did not verify!");
		}
		
		BigInteger serialNumber = spend.getCoinSerialNumber();
		
		System.out.println("Successfully verified a comment spend transaction, Comment serial number is:");
		System.out.println("========serialNumber========");
		System.out.println(serialNumber);
		
	}

}
