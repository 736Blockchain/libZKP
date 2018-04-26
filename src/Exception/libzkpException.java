package Exception;

public class libzkpException extends RuntimeException{
//	public libzkpException() {
//		// TODO Auto-generated constructor stub
//	}
	
	public libzkpException(String message) {
		super(message);
		System.out.println(message);
	}
}
