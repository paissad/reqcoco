package net.paissad.tools.reqcoco.parser.simple.exception;

/**
 * This exception is thrown when an error occurs during the execution of the generator.
 * 
 * @author paissad
 */
public class ReqGeneratorExecutionException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReqGeneratorExecutionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
