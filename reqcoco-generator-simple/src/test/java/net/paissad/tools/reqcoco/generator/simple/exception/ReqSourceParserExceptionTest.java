package net.paissad.tools.reqcoco.generator.simple.exception;

import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ReqSourceParserExceptionTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testReqSourceParserException() throws ReqSourceParserException {
		thrown.expect(ReqSourceParserException.class);
		thrown.expectCause(Is.isA(NullPointerException.class));
		thrown.expectMessage("eee");
		throw new ReqSourceParserException("eee", new NullPointerException());
	}

}
