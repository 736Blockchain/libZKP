package param;

import java.io.Serializable;
import java.math.BigInteger;

import BigNum.BigNumber;

public class IntegerGroupParams  implements Serializable{
	// 群的生成元 a generator for the group
	public BigInteger g;
	//群的第二个生成元 a second generator for the group
	//Note that log_g(h) and log_h(g) must be unknown
	public BigInteger h;
	//群的模数 the modulus for the group
	public BigInteger modulus;
	//群的阶 the order of the group
	public BigInteger groupOrder;
	public Boolean initialized;
	//序列化上述
	
	/**
	 * Allocate an empty set of parameters
	 */
	public IntegerGroupParams() {
		this.initialized = false;
	};
	
	/**
	 * Generates a random group of parameters
	 * @return a random element in the group
	 */
	public BigInteger randomElement() {
		/*
		 * 群的生成元被raised to 到一个小于群的阶的随机数，提供了均匀分布的随机数
		 * modPow(exponent, m)
		 * Parameters:	exponent the exponent.
		 * 				m the modulus.
		 * Returns:
		 * 			this^exponent mod m
		 */
		return g.modPow(BigNumber.randomBigInteger(groupOrder), modulus);
	}
	
/*	public String toString() {
		String returnString = null;
		returnString += "\t\tinitialized="+initialized;
		returnString += "\t\tg="+g.toString();
		returnString += "\t\th="+h.toString();
		returnString += "\t\tmodulus="+modulus.toString();
		returnString += "\t\tgroupOrder="+groupOrder.toString();
		return returnString;
	}*/
}
