package tutorial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.google.common.base.Stopwatch;

import Accumulator.Accumulator;
import Accumulator.AccumulatorWitness;
import comment.CommentPrivate;
import param.Params;

/**
 * 用来统计一些计算的时间  生成计算数据并绘图
 * @author jiaxyan
 * ① Modulus模数的比特长度对时间的影响
 * ② Modulus模数的比特长度对证明Proof的比特长度影响
 * ③ 安全参数的影响？
 * ④ Accumulator聚合时间随聚合元素的关系
 */
public class Performance {
	public static final int nums = 5;
	public static void main(String[] args) throws IOException {
		//获取modulus,这个modulus应该是q和p的乘积吧？ 其次这个modulus是RSA模数吧？
//		System.out.println( getRandomRSAModlus(2048).bitLength() );
//		BigInteger testModulus = new BigInteger("a8852ebf7c49f01cd196e35394f3b74dd86283a07f57e0a262928e7493d4a3961d93d93c90ea3369719641d626d28b9cddc6d9307b9aabdbffc40b6d6da2e329d079b4187ff784b2893d9f53e9ab913a04ff02668114695b07d8ce877c4c8cac1b12b9beff3c51294ebe349eca41c24cd32a6d09dd1579d3947e5c4dcc30b2090b0454edb98c6336e7571db09e0fdafbd68d8f0470223836e90666a5b143b73b9cd71547c917bf24c0efc86af2eba046ed781d9acb05c80f007ef5a0a5dfca23236f37e698e8728def12554bc80f294f71c040a88eff144d130b24211016a97ce0f5fe520f477e555c9997683d762aff8bd1402ae6938dd5c994780b1bf6aa7239e9d8101630ecfeaa730d2bbc97d39beb057f016db2e28bf12fab4989c0170c2593383fd04660b5229adcd8486ba78f6cc1b558bcd92f344100dff239a8c00dbc4c2825277f24bdd04475bcc9a8c39fd895eff97c1967e434effcb9bd394e0577f4cf98c30d9e6b54cd47d6e447dcf34d67e48e4421691dbe4a7d9bd503abb9",16);
		Params param1024 = getParams(getRandomRSAModlus(2048), 80);
		Params param2048 = getParams(getRandomRSAModlus(2048), 112);
		Params param3072 = getParams(getRandomRSAModlus(2048), 120);
		
		//先分别生成n个评论
		ArrayList<CommentPrivate> commentlist1024 =  new ArrayList<>();
		Accumulator accu1024 = new Accumulator(param1024);
		for(int num = 0; num < nums+10; num++) {
			CommentPrivate testComment = new CommentPrivate(param1024,"abcd");
			accu1024.AddCommentPublic(testComment.getCommentPublic());
//			commentlist1024.add(testComment);
		}
		
System.out.println("---------------------------");
		ArrayList<CommentPrivate> commentlist2048 =  new ArrayList<>();
		Accumulator accu2048 = new Accumulator(param2048);
		for(int num = 0; num < nums; num++) {
			CommentPrivate testComment = new CommentPrivate(param2048,"abcd");
			accu2048.AddCommentPublic(testComment.getCommentPublic());
//			commentlist2048.add(testComment);
		}

System.out.println("---------------------------");
		ArrayList<CommentPrivate> commentlist3072 =  new ArrayList<>(); 
		Accumulator accu3072 = new Accumulator(param3072);
		for(int num = 0; num < nums; num++) {
			CommentPrivate testComment = new CommentPrivate(param3072,"abcd");
			accu3072.accumulate(testComment.getCommentPublic());
//			commentlist3072.add(testComment);
		}
		
		//分别对这n个评论生成对应的witness
System.exit(0);


		//开始计算时间
		long start1 = System.currentTimeMillis();
		ArrayList<AccumulatorWitness> witnesslist1024 = new ArrayList<>();
		for(CommentPrivate commentprivate :commentlist1024) {
			AccumulatorWitness witness = Accumulator.getAccumulatorWitness(commentlist1024, param1024, commentprivate);
			witnesslist1024.add(witness);
		}
		int index = 0;
			for(CommentPrivate commentprivate:commentlist1024) {
				VerifyComment.verifyWithAccumu(commentprivate, param1024, accu1024, witnesslist1024.get(index)); index++;}
		long end1 = System.currentTimeMillis();//毫秒数?
		long time1024 = (end1 - start1)/nums;
		
		long start2 = System.currentTimeMillis();
		ArrayList<AccumulatorWitness> witnesslist2048 = new ArrayList<>();		
		for(CommentPrivate commentprivate :commentlist2048) {
			AccumulatorWitness witness = Accumulator.getAccumulatorWitness(commentlist2048, param2048, commentprivate);
			witnesslist2048.add(witness);
		}
		index = 0;
			for(CommentPrivate commentprivate:commentlist2048) {
				VerifyComment.verifyWithAccumu(commentprivate, param2048, accu2048, witnesslist2048.get(index));index++;}
		long end2 = System.currentTimeMillis();//毫秒数?
		long time2048 = (end2 - start2)/nums;
		System.out.println(time2048/(double)time1024);
		
		long start3 = System.currentTimeMillis();
		ArrayList<AccumulatorWitness> witnesslist3072 = new ArrayList<>();
		for(CommentPrivate commentprivate :commentlist3072) {
			AccumulatorWitness witness = Accumulator.getAccumulatorWitness(commentlist3072, param3072, commentprivate);
			witnesslist3072.add(witness);
		}
		index = 0;
			for(CommentPrivate commentprivate:commentlist3072) {
				VerifyComment.verifyWithAccumu(commentprivate, param3072, accu3072, witnesslist3072.get(index));index++;}
		long end3 = System.currentTimeMillis();//毫秒数?
		
		long time3072 = (end3 - start3)/nums;
		System.out.println(time3072/(double)time2048);
	}
	
	/**
	 * 产生评论(承诺值)的测试
	 * @param params 参数
	 * @param commentNum 评论(承诺)个数
	 * @throws IOException 
	 */
	public static void generateCommentAndVerifyTask() throws IOException {
		Params[] params = new Params[3];
		params[0] = getParams(getRandomRSAModlus(3074), 80); //params1024
		params[1] = getParams(getRandomRSAModlus(3074), 112);//params2048
		params[2] = getParams(getRandomRSAModlus(3074), 112);//params3072
		JSONObject zkpProofTest = new JSONObject();
		Stopwatch timeMakeComment, timeVerifyComment;

System.out.println("---- 生成5个交易评论 并验证  计算时间");
		for(int i=1024; i<=3072; i+=1024) {
			ArrayList<CommentPrivate> commentlist = new ArrayList<>();
			/////////////////////////////////////////////
			timeMakeComment = Stopwatch.createStarted();
				for(int num = 0; num < 5; num++) {
					CommentPrivate testComment = new CommentPrivate(params[i/1024-1],"abcd"+i);
					commentlist.add(testComment);
				}
			timeMakeComment.stop();
			zkpProofTest.put(i+"makecomment", timeMakeComment.elapsed(TimeUnit.SECONDS)/5);

System.out.println("---- 生成完毕");
			//////////////////////////////////////////////
			timeVerifyComment = Stopwatch.createStarted();
				for(CommentPrivate commentprivate:commentlist)
					VerifyComment.verifyWithoutAccumu(commentprivate, params[i/1024-1]);
			timeVerifyComment.stop();
			zkpProofTest.put(i+"verifycomment", timeVerifyComment.elapsed(TimeUnit.SECONDS)/5);
		}
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
System.out.println("---- 验证结束");
		FileWriter fw = new FileWriter("/Users/jiaxyan/Desktop/data.json");
		PrintWriter out = new PrintWriter(fw);
		out.write(zkpProofTest.toString());
		out.println();
		fw.close();
		out.close();
	}

	/**
	 * 获取一个指定bits数目的RSA Modulus(RSA模数，p*q)
	 * @param bitsNum 如1024、2048、3072等
	 * @return 模数Modulus
	 */
	public static BigInteger getRandomRSAModlus(int bitsNum) {
		BigInteger p = getRandomBigIntegerOfbits(bitsNum/2);
		BigInteger q = getRandomBigIntegerOfbits(bitsNum/2);
		return p.multiply(q);
	}
	
	
	/**
	 * 获取一个指定bits数目的随机素数
	 * @param bitsNum 如1024、2048、3072等
	 * @return 随机素数
	 */
	public static BigInteger getRandomBigIntegerOfbits(int bitsNum) {
		int i=0,maxTry = 1000;
		BigInteger result = null;
		Random random = new Random();
		while(i<maxTry) {
			result = new BigInteger(bitsNum, random);
			if(result.bitLength()==bitsNum)
				return result;
			i++;
		}
		return null;
	}

	
	/**
	 * 获取参数对象(或生成或从本地读取)
	 * @param Modulus 模数
	 * @param securityLevel 安全参数 默认是80
	 * @return 参数对象
	 */
	public static Params getParams(BigInteger Modulus, int securityLevel) {
		//通过序列化存储params对象 以便实验
		int bits = 0;
		if(securityLevel==80)bits = 1024;
		if(securityLevel<=112 && securityLevel>80)bits = 2048;
		if(securityLevel<=128 && securityLevel>112)bits = 3072;
		
		File params_obj = new File("params_obj_serialized - "+bits+".local");
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
				params = new Params(Modulus, securityLevel);
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
		return params;
	}
}
