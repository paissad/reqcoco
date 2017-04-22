package net.paissad.tools.reqcoco.api.exception;

public class ReqParserException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReqParserException(String errMsg, Exception e) {
		super(errMsg, e);
	}

}
