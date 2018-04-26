package BigNum;

import java.math.BigInteger;
import java.util.Random;

public class BigNumber {
	/**
	 * 产生一个小于upperboundBigNum的大数
	 * @param upperboundBigNum
	 * @return a BigInteger less than upperboundBigNum
	 */
	public static BigInteger randomBigInteger(BigInteger upperboundBigNum) {
		Random rand = new Random();
	    BigInteger result = new BigInteger(upperboundBigNum.bitLength(), rand);
	    while( result.compareTo(upperboundBigNum) >= 0 ) {
	        result = new BigInteger(upperboundBigNum.bitLength(), rand);
	    }
	    return result;
	}
}
