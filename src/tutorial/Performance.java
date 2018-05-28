package tutorial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.google.common.base.Stopwatch;

import Accumulator.Accumulator;
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

	public static void main(String[] args) {
		//获取modulus,这个modulus应该是q和p的乘积吧？ 其次这个modulus是RSA模数吧？
//		System.out.println( getRandomRSAModlus(2048).bitLength() );
		BigInteger testRSAModeModulus = getRandomRSAModlus(1024);
		Params params = getParams(testRSAModeModulus, 80);
		JSONObject first = new JSONObject();
		JSONObject second = new JSONObject();
		JSONObject third = new JSONObject();
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		Stopwatch stopwatch_1024 = Stopwatch.createStarted();
		
		stopwatch_1024.stop();
		first.append("1024", stopwatch_1024.elapsed(TimeUnit.SECONDS));
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		
		Stopwatch stopwatch_2048 = Stopwatch.createStarted();
		
		stopwatch_2048.stop();
		second.append("2048", stopwatch_2048.elapsed(TimeUnit.SECONDS));
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		
		
		Stopwatch stopwatch_3072 = Stopwatch.createStarted();
		
		stopwatch_3072.stop();
		third.append("3072", stopwatch_3072.elapsed(TimeUnit.SECONDS));
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	}

	
	/**
	 * 产生评论(承诺值)的测试
	 * @param params 参数
	 * @param commentNum 评论(承诺)个数
	 */
	public static void generateCommentTask(Params params,int commentNum) {
//		Accumulator accumulator = new Accumulator(params,"");
		System.out.println("---- 产生"+commentNum+"个评论，(#并Accumulate、验证)");
		for(int i = 0; i < commentNum; i++) {
			CommentPrivate testComment = new CommentPrivate(params,"abcd"+""+i);
//			accumulator.AddCommentPublic(testComment.getCommentPublic());
		}
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
