package param;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

 public class Hash {
//	public static void main(String[] args) {
//		byte[] a = "sdsd".getBytes();
//
//		byte[] b = "sdsd".getBytes();
//		
//		byte[] ahash = calculateHash(a);
//		byte[] bhash = calculateHash(b);
//		
//		System.out.println(ahash);
//		System.out.println(bhash);
//		System.out.println(equals(ahash,bhash));
//	}

	 public static String MD5(String s) {
		    try {
		        MessageDigest md = MessageDigest.getInstance("MD5");
		        byte[] bytes = md.digest(s.getBytes("utf-8"));
		        return toHex(bytes);
		    }
		    catch (Exception e) {
		        throw new RuntimeException(e);
		    }
	}

	public static String toHex(byte[] bytes) {

		   final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
		   StringBuilder ret = new StringBuilder(bytes.length * 2);
		   for (int i=0; i<bytes.length; i++) {
		       ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
		       ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
		   }
		    return ret.toString();
	}
	
	public static byte[] calculateHash(byte[] input) {
		MessageDigest digest;
		byte[] hash = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			//参数本来是text.getBytes(StandardCharsets.UTF_8)
			hash = digest.digest(input);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}
	
	public static Boolean equals(byte[] a, byte[] b) {
		if(a.length != b.length)
			return false;
//		Boolean isEqual = true;
		for(int i = 0; i < a.length; i++) 
			if(a[i]!=b[i])
				return false;
		return true;
	}
	
}
