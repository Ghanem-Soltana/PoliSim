
package util;

public class ProcessingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProcessingException(Throwable e) {
		super(e);
	}

	public ProcessingException(String msg, Throwable th) {
		super(msg, th);
	}

	public ProcessingException(String string) {
		super(string);
	}

}
