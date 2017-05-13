package net.paissad.tools.reqcoco.parser.simple.exception;

import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.parser.simple.exception.ReqGeneratorExecutionException;

public class ReqGeneratorExecutionExceptionTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testReqGeneratorConfigException() throws ReqGeneratorExecutionException {
		thrown.expect(ReqGeneratorExecutionException.class);
		thrown.expectCause(Is.isA(NullPointerException.class));
		thrown.expectMessage("ddd");
		throw new ReqGeneratorExecutionException("ddd", new NullPointerException());
	}

}
