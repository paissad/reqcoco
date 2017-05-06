package net.paissad.tools.reqcoco.generator.simple.util;

import java.nio.file.Path;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;

public class ReqGeneratorUtil {

	private ReqGeneratorUtil() {
	}

	/**
	 * @param requirements - The requirements (computed between declaration source and code (source/test)) for which we need to generate the report.
	 * @param outputFile - The file where to generate the coverage report.
	 * @throws JAXBException If an error occurs while marshalling
	 */
	public static void generateXmlCoverageReport(final Collection<Requirement> requirements, final Path outputFile) throws JAXBException {

		final Requirements rootReqs = new Requirements();
		rootReqs.setRequirements(requirements);

		final JAXBContext jaxbContext = JAXBContext.newInstance(Requirements.class);
		final Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.marshal(rootReqs, outputFile.toFile());
	}

}
