package param;

import java.math.BigInteger;

import Exception.libzkpException;

public class ParamGeneration {
	
//	public String STRING_COMMIT_GROUP = "COIN_COMMITMENT_GROUP";
//	public String STRING_AVC_GROUP = "ACCUMULATED_VALUE_COMMITMENT_GROUP";
//	public String STRING_AVC_ORDER = "ACCUMULATED_VALUE_COMMITMENT_ORDER";
//	public String STRING_AIC_GROUP = "ACCUMULATOR_INTERNAL_COMMITMENT_GROUP";
//	public String STRING_QRNCOMMIT_GROUPG = "ACCUMULATOR_QRN_COMMITMENT_GROUPG";
//	public String STRING_QRNCOMMIT_GROUPH = "ACCUMULATOR_QRN_COMMITMENT_GROUPH";
	public int ACCUMULATOR_BASE_CONSTANT= 31;
//	public int MAX_PRIMEGEN_ATTEMPTS = 10000;
	public int MAX_ACCUMGEN_ATTEMPTS = 10000;
//	public int MAX_GENERATOR_ATTEMPTS = 10000;
	public int NUM_SCHNORRGEN_ATTEMPTS = 10000;
	
	public void CalculateParams(Params params, BigInteger accumulatorModulus,  int securityLevel) {
System.out.println("	======进入CalculateParams方法...");		
		params.initialized = false;
		params.accumulatorAndproofParams.initialized = false;
		
		int Nlen = accumulatorModulus.bitLength();
		if(accumulatorModulus.bitLength() < 1023) {//模数一定要大于1023 bits 
			throw new libzkpException("N < 1023 bits----in ParamGeneration.java");
		}
		
		if(securityLevel < 80) {//验证Verify securityLevel大于等于 80 bits
			throw new libzkpException("SecurityLevel must be at least 80 bits----in ParamGeneration.java");
		}
		
		//设置累加器的模数为accumulatorModulus  Set the accumulator modulus to "accumulatorModulus"
		params.accumulatorAndproofParams.accumulatorModulus = accumulatorModulus;
		
		//计算“F_p”需要的大小
		//[0]-p   默认qpLen[0] = 1024;
		//[1]-q   默认qpLen[1] = 256;
		int [] pqLen = new int[2];
		pqLen = calculateGroupParamLengths(Nlen-2, securityLevel);//Nlen-2
		
		params.coinCommitmentGroup = deriveIntegerGroupParams( pqLen[0], pqLen[1]);//1024, 256
		System.out.println("---- params.coinCommitmentGroup (modulus bits len:)"+params.coinCommitmentGroup.modulus.bitLength());
System.out.println("	======生成params.coinCommitmentGroup完毕...");
		params.serialNumberSoKCommitmentGroup = deriveIntegerGroupFromOrder(params.coinCommitmentGroup.modulus);
		System.out.println("---- params.serialNumberSoKCommitmentGroup(Modulus bit len:)"+params.serialNumberSoKCommitmentGroup.modulus.bitLength());
System.out.println("	======生成params.serialNumberSoKCommitmentGroup完毕...");
		params.accumulatorAndproofParams.accumulatorPoKCommitmentGroup = deriveIntegerGroupParams(pqLen[0] + 300, pqLen[1] + 1);
		System.out.println("---- params.accumulatorAndproofParams.accumulatorPoKCommitmentGroup (Modulus bits size:)"+params.accumulatorAndproofParams.accumulatorPoKCommitmentGroup.modulus.bitLength());
System.out.println("	======生成params.accumulatorParams.accumulatorPoKCommitmentGroup完毕...");
		params.accumulatorAndproofParams.accumulatorQRNCommitmentGroup.g = ( Prime.generateRandomPrime(Nlen - 1) ).modPow(BigInteger.valueOf(2),accumulatorModulus); 
		params.accumulatorAndproofParams.accumulatorQRNCommitmentGroup.h = ( Prime.generateRandomPrime(Nlen - 1) ).modPow(BigInteger.valueOf(2),accumulatorModulus); 
		

		final BigInteger constant = BigInteger.valueOf(ACCUMULATOR_BASE_CONSTANT);
		params.accumulatorAndproofParams.accumulatorBase = BigInteger.ONE;
		for(int count = 0; count < MAX_ACCUMGEN_ATTEMPTS && params.accumulatorAndproofParams.accumulatorBase.compareTo(BigInteger.ONE)==0; count++) {	
			params.accumulatorAndproofParams.accumulatorBase = constant.modPow(BigInteger.valueOf(2),params.accumulatorAndproofParams.accumulatorModulus);
		}
		
		
		params.accumulatorAndproofParams.maxCommitmentValue = params.coinCommitmentGroup.modulus;
		params.accumulatorAndproofParams.minCommitmentValue = BigInteger.valueOf(2).pow((params.coinCommitmentGroup.modulus.bitLength()/2)+3);
		
		params.accumulatorAndproofParams.initialized = true;
		params.initialized = true;
		
	}
	

	/* qpLen[0] = 1024;plen
	 * qpLen[1] = 256; qlen
	 */
	public int[] calculateGroupParamLengths(int maxPlen, int securitylevel) {
		int [] qpLen = new int[2];
//		qpLen[0] = 1024;
//		qpLen[1] = 256;
		qpLen[0] = 0;
		qpLen[1] = 0;
			
		if (securitylevel <80) {
			throw new libzkpException("Security level must be at least 80 bits.");
		}else if (securitylevel == 80) {
			qpLen[1] = 256;
			qpLen[0] = 1024;
		}else if (securitylevel <= 112) {
			qpLen[1] = 256;
			qpLen[0] = 2048;
		}else if (securitylevel <= 128) {
			qpLen[1] = 320;
			qpLen[0] = 3072;
		}else {
			throw new libzkpException("Security level not supported.");
		}
		
		if (qpLen[0] > maxPlen) {
			throw new libzkpException("Modulus size is too small for this security level.");
		}
		return qpLen;
	}
	
	//生成p和q，p为1024位，q为256位  都是素数  且满足p = 2^w * q + 1
	public void calculateGroupModulusAndOrder(int pLen, int qLen, IntegerGroupParams result /*BigInteger modulus, BigInteger order*/) {
		
		result.groupOrder = Prime.generateRandomPrime(qLen);
		
		int p0len = (int)Math.ceil((pLen/2.0)+1);
		BigInteger p0 = Prime.generateRandomPrime(p0len);
		
		BigInteger x = Prime.generateRandomPrime(pLen);
		
		BigInteger powerOfTwo = (BigInteger.valueOf(2)).pow(pLen-1);
		x = powerOfTwo.add( (x.mod(powerOfTwo)) );
		BigInteger t = x.divide(
				(BigInteger.valueOf(2)).multiply(result.groupOrder).multiply(p0)
				);
		int pgen_counter = 1000;
		int old_counter = 1000;
		for(;pgen_counter <= (4*pLen+old_counter); pgen_counter++) {
			//if(pgen_counter%10==0)
				//System.out.println(pgen_counter);
			powerOfTwo = ( BigInteger.valueOf(2) ).pow(pLen);
			BigInteger prod = ( (BigInteger.valueOf(2)).multiply(t).multiply(result.groupOrder).multiply(p0) ).add(BigInteger.ONE);
			if(prod.compareTo(powerOfTwo)==1) {
				t = ((BigInteger.valueOf(2)).pow(pLen-1)).divide( (BigInteger.valueOf(2)).multiply(result.groupOrder).multiply(p0) );
			}
			result.modulus = ((BigInteger.valueOf(2)).multiply(t).multiply(result.groupOrder).multiply(p0)).add(BigInteger.ONE);
			
			BigInteger a = Prime.generateRandomPrime(pLen);
			a = (BigInteger.valueOf(2)).add( a.mod(result.modulus.subtract((BigInteger.valueOf(3)))) );
			BigInteger z = a.modPow((BigInteger.valueOf(2)).multiply(t).multiply(result.groupOrder), result.modulus);
			if( result.modulus.gcd(z.subtract((BigInteger.ONE))).compareTo( (BigInteger.ONE) ) == 0
					&& (z.modPow(p0, result.modulus)).compareTo( (BigInteger.ONE) ) == 0) {
				//System.out.println("ok");
				break;
			}
			
			t = t.add(BigInteger.ONE);
		}
	}
		
	public BigInteger calculateGroupGenerator(BigInteger modulus, BigInteger order, String gOrh) {
		BigInteger result = null;
		BigInteger modulus_local = modulus;
		BigInteger order_local = order;
		BigInteger e = ( modulus_local.subtract(BigInteger.ONE) ).divide(order_local);
//		System.out.println("e::"+e);
//		byte[] hashString = Hash.calculateHash("haha".getBytes());
		for(int count = 0; count < 1000; count++) {
//			if(count%100==0)System.out.println("count:"+count);
			String s = String.valueOf(count*256)+gOrh;
			BigInteger W = new BigInteger(Hash.calculateHash(s.getBytes()));
			
			//if(W.compareTo(BigInteger.ONE)<1 || W.compareTo(modulus.subtract(BigInteger.ONE))>-1)
				//System.out.println("Wrong");
			result = W.modPow(e, modulus_local);
			
			//result > 1?
		    //g^order mod m =1?
			if(result.compareTo(BigInteger.valueOf(1))==1 && (result.modPow(order_local, modulus_local)).compareTo(BigInteger.ONE)==0){
					return result;
			}
		}
		return null;
	}
	

	public IntegerGroupParams deriveIntegerGroupParams(int pLen, int qLen) {
		 
		IntegerGroupParams result = new IntegerGroupParams();
		
		calculateGroupModulusAndOrder(pLen, qLen, result/*result.modulus, result.groupOrder*/);

		result.g = calculateGroupGenerator(result.modulus, result.groupOrder, "g");
		result.h = calculateGroupGenerator(result.modulus, result.groupOrder, "h");
		//System.out.println(result.g);
		//System.out.println(result.h);
		if(result.g == null || result.h == null)
			throw new libzkpException("g or h is null(in deriveIntegerGrouparams)");
		
		if(
			result.modulus.bitLength()<pLen	||
			result.groupOrder.bitLength()<qLen ||
			!(result.modulus.isProbablePrime(10)) ||
			!(result.groupOrder.isProbablePrime(10)) ||
			!( (result.g.modPow(result.groupOrder, result.modulus)).compareTo(BigInteger.ONE)==0 ) ||
			!( (result.h.modPow(result.groupOrder, result.modulus)).compareTo(BigInteger.ONE)==0  ) ||
			( (result.g.modPow(BigInteger.valueOf(100), result.modulus)).compareTo(BigInteger.ONE)==0 ) ||
			( (result.h.modPow(BigInteger.valueOf(100), result.modulus)).compareTo(BigInteger.ONE)==0 ) ||
			(result.g.compareTo(result.h)==0) ||
			(result.g.compareTo(BigInteger.ONE)==0)
				)
		{
			System.out.println("=====Group parameters check=====");
			System.out.println(result.modulus.bitLength()<pLen);
			System.out.println(result.groupOrder.bitLength()<qLen);
			System.out.println(!(result.modulus.isProbablePrime(10)));
			System.out.println(!(result.groupOrder.isProbablePrime(10)));
			System.out.println(!( (result.g.modPow(result.groupOrder, result.modulus)).compareTo(BigInteger.ONE)==0 ));
			System.out.println(!( (result.h.modPow(result.groupOrder, result.modulus)).compareTo(BigInteger.ONE)==0  ));
			System.out.println(( (result.g.modPow(BigInteger.valueOf(100), result.modulus)).compareTo(BigInteger.ONE)==0 ));
			System.out.println(( (result.h.modPow(BigInteger.valueOf(100), result.modulus)).compareTo(BigInteger.ONE)==0 ));
			System.out.println((result.g.compareTo(result.h)==0));
			System.out.println((result.g.compareTo(BigInteger.ONE)==0));
			System.out.println("=====Group parameters check=====");
			throw new libzkpException("Group parameters are not valid (in deriveIntegerGroupParams)");
		}
		return result;
	}
	
	public IntegerGroupParams deriveIntegerGroupFromOrder(BigInteger groupOrder) {
		
		IntegerGroupParams result = new IntegerGroupParams();
		result.groupOrder = groupOrder;
		
		for(int i =1; i < NUM_SCHNORRGEN_ATTEMPTS; i++) {
			
			// Set modulus equal to "groupOrder * 2 * i"
			result.modulus = (result.groupOrder.multiply(BigInteger.valueOf(i*2))).add(BigInteger.ONE);
			 
			if (result.modulus.isProbablePrime(10)) {
				
				result.g = calculateGroupGenerator(result.modulus, result.groupOrder, "g");
				result.h = calculateGroupGenerator(result.modulus, result.groupOrder, "h");
				//System.out.println(result.g);
				//System.out.println(result.h);

				// Perform some basic tests to make sure we have good parameters
				if(     !(result.modulus.isProbablePrime(10)) ||
						!(result.groupOrder.isProbablePrime(10)) ||
						!( (result.g.modPow(result.groupOrder, result.modulus)).compareTo(BigInteger.ONE)==0 ) ||
						!( (result.h.modPow(result.groupOrder, result.modulus)).compareTo(BigInteger.ONE)==0  ) ||
						( (result.g.modPow(BigInteger.valueOf(100), result.modulus)).compareTo(BigInteger.ONE)==0 ) ||
						( (result.h.modPow(BigInteger.valueOf(100), result.modulus)).compareTo(BigInteger.ONE)==0 ) ||
						(result.g.compareTo(result.h)==0) ||
						(result.g.compareTo(BigInteger.ONE)==0)
				 ) {
					throw new libzkpException("Group parameters are not valid (in deriveIntegerGroupFromOrder)");
				}
				
				return result;
			}
		}
		
		throw new libzkpException("Too many attempts to generate Schnorr group.");
	}
	

	/*
	 * 查看参数是否为素数
	 */
	public Boolean primalityTestByTrialDivision(int candidate) {
		BigInteger canBigNum = BigInteger.valueOf(candidate);
		//当前数字为素数的可能性超过 (1 - (1/2)^certainty)
		return canBigNum.isProbablePrime(9);
	}
}
