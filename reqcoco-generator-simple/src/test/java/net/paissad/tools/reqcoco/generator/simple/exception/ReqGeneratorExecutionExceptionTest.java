package net.paissad.tools.reqcoco.generator.simple.exception;

import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
