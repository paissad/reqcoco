package net.paissad.tools.reqcoco.parser.simple.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.parser.simple.util.ReqGeneratorUtil;

public class ReqGeneratorUtilTest {

	private Collection<Requirement>	requirements;

	private Path					coverageFile;

	@Rule
	public ExpectedException		thrown	= ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.requirements = this.createRequirementsStub();
		this.coverageFile = Files.createTempFile(getClass().getSimpleName() + "-", "-report-coverage.xml");
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(this.coverageFile.toFile());
	}

	@Test
	public void testGenerateXmlCoverageReport() throws JAXBException, IOException {
		ReqGeneratorUtil.generateXmlCoverageReport(this.requirements, this.coverageFile);
	}

	@Test
	public void testGenerateXmlCoverageReportWhenOutputIsDirectory() throws JAXBException, IOException {

		FileUtils.deleteQuietly(this.coverageFile.toFile());
		this.coverageFile = Files.createTempDirectory(getClass().getSimpleName());

		thrown.expect(JAXBException.class);
		ReqGeneratorUtil.generateXmlCoverageReport(this.requirements, this.coverageFile);
	}

	private Collection<Requirement> createRequirementsStub() {
		final List<Requirement> reqs = new ArrayList<>();
		final Requirement req1 = new Requirement("myid", "my_version", "my_rev");
		reqs.add(req1);
		return reqs;
	}

}
