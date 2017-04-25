package net.paissad.tools.reqcoco.api.exception;

public class ReqReportParserException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReqReportParserException(String errMsg, Exception e) {
		super(errMsg, e);
	}

}
