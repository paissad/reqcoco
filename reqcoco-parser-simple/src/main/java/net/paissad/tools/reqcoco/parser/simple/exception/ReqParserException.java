package net.paissad.tools.reqcoco.parser.simple.exception;

/**
 * This exception is thrown when an error occurs while parsing the source containing the declarations of the requirements.
 * 
 * @author paissad
 */
public class ReqParserException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReqParserException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
