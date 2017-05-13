package net.paissad.tools.reqcoco.parser.simple.exception;

/**
 * This exception is thrown when an error occurs during the configuration of the generator.
 * 
 * @author paissad
 */
public class ReqGeneratorConfigException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReqGeneratorConfigException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
