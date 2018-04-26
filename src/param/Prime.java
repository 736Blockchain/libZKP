package param;

import java.math.BigInteger;
import java.util.Random;

public class Prime {

	public static BigInteger generatePrime(int bitsNum){
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
}
