package net.paissad.tools.reqcoco.core.parser.simple;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PathReqReportParserTest {

	private PathReqReportParser	pathReqSourceParser;

	private Path				path;

	private Map<String, Object>	options;

	@Before
	public void setUp() throws Exception {
		path = Paths.get(".");
		this.options = new HashMap<>();
		this.pathReqSourceParser = new PathReqReportParser(this.path, this.options);
	}

	@Test
	public void testGetURI() throws URISyntaxException {
		Assert.assertEquals(this.path.toFile().toURI(), this.pathReqSourceParser.getURI());
	}

	@Test
	public void testGetOptions() {
		Assert.assertTrue(this.options == this.pathReqSourceParser.getOptions());
	}

	@Test
	public void testGetPath() {
		Assert.assertTrue(this.path == this.pathReqSourceParser.getPath());
	}

}
