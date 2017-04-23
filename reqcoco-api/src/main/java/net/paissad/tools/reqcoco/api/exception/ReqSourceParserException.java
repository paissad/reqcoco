package net.paissad.tools.reqcoco.api.exception;

public class ReqSourceParserException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReqSourceParserException(String errMsg, Exception e) {
		super(errMsg, e);
	}

}
