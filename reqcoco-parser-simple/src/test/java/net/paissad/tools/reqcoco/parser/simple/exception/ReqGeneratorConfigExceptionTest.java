package net.paissad.tools.reqcoco.parser.simple.exception;

import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.parser.simple.exception.ReqGeneratorConfigException;

public class ReqGeneratorConfigExceptionTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testReqGeneratorConfigException() throws ReqGeneratorConfigException {
		thrown.expect(ReqGeneratorConfigException.class);
		thrown.expectCause(Is.isA(NullPointerException.class));
		thrown.expectMessage("cccc");
		throw new ReqGeneratorConfigException("cccc", new NullPointerException());
	}

}
