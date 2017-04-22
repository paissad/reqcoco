package net.paissad.tools.reqcoco.api.exception;

public class ReParserException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReParserException(String errMsg, Exception e) {
		super(errMsg, e);
	}

}
