package net.paissad.tools.reqcoco.api.exception;

import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ReqReportParserExceptionTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testReqSourceParserException() throws ReqReportParserException {
		thrown.expect(ReqReportParserException.class);
		thrown.expectCause(Is.isA(NullPointerException.class));
		thrown.expectMessage("bbb");
		throw new ReqReportParserException("bbb", new NullPointerException());
	}
}
