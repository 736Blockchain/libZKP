package param;

import java.math.BigInteger;
import java.util.Random;

public class Prime {

	/**
	 * 返回一个指定bit书目大小的 随机 素大数
	 * @param bitsNum
	 * @return prime BigInteger
	 */
	public static BigInteger generateRandomPrime(int bitsNum){
		int certainty = 0;
		int verify_certainty = 0;
		if(certainty == 0) {
			certainty = 10;
		}
		if(verify_certainty == 0) {
			verify_certainty = 50;
		}
		BigInteger possible;
		while(true) {
			possible = new BigInteger(bitsNum, certainty, new Random());
			if(possible.isProbablePrime(verify_certainty)) {
				return possible;
			}
		}
		
	}
	
	/**
	 * 快速生成一个素大数
	 * @param bitsNum
	 * @return prime BigInteger
	 */
	public static BigInteger generateRandomPrimeFast(int bitsNum){
		return new BigInteger(bitsNum, 10, new Random());
	}
	
}
