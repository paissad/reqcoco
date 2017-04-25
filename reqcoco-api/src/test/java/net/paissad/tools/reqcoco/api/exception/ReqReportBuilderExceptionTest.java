package net.paissad.tools.reqcoco.api.exception;

import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ReqReportBuilderExceptionTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testReqReportBuilderException() throws ReqReportBuilderException {
		thrown.expect(ReqReportBuilderException.class);
		thrown.expectCause(Is.isA(NullPointerException.class));
		thrown.expectMessage("aaaaa");
		throw new ReqReportBuilderException("aaaaa", new NullPointerException());
	}

}
