package net.paissad.tools.reqcoco.runner;

import org.junit.Assert;
import org.junit.Test;

import net.paissad.tools.reqcoco.parser.redmine.RedmineReqDeclParser;
import net.paissad.tools.reqcoco.parser.simple.impl.FileReqDeclParser;

public class ReqSourceTypeTest {

	@Test
	public void testGetParserFile() {
		Assert.assertEquals(FileReqDeclParser.class, ReqSourceType.FILE.getParser().getClass());
	}

	@Test
	public void testGetParserRedmine() {
		Assert.assertEquals(RedmineReqDeclParser.class, ReqSourceType.REDMINE.getParser().getClass());
	}

}
