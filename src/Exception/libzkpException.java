package Exception;

public class libzkpException extends RuntimeException{
	public libzkpException(String message) {
		super(message);
		System.out.println("[libzkpException]:"+message);
	}
}
