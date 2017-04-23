package net.paissad.tools.reqcoco.core.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;

public class ReqReportBuilderConsole extends AbstractReqReportBuilder {

	private static final Logger		LOGGER	= LoggerFactory.getLogger(ReqReportBuilderConsole.class);

	private static final Charset	UTF8	= Charset.forName("UTF-8");

	public ReqReportBuilderConsole(final Collection<Requirement> requirements) {
		getRequirements().addAll(requirements);
		this.setReportConfig(getDefaultReportConfig());
	}

	@Override
	public void build() throws ReqReportBuilderException {

		if (getRequirements().isEmpty()) {
			LOGGER.warn("No requirements = no console report");

		} else {

			LOGGER.info("Starting to generate console report");

			final OutputStream out = getOutput();
			try (BufferedOutputStream bos = new BufferedOutputStream(out, 8192)) {

				out.write((getReportConfig().getTitle() + "\n").getBytes(UTF8));
				out.write("========================================== SUMMARY ===============================================\n".getBytes(UTF8));

				final String summaryFormat = "%-25s : %s\n";
				out.write(String.format(summaryFormat, "Number of requirements", getRequirements().size()).getBytes(UTF8));
				out.write(String.format(summaryFormat, "Code done ratio", getCodeDoneRatio() * 100 + " %").getBytes(UTF8));
				out.write(String.format(summaryFormat, "Tests done ratio", getTestDoneRatio() * 100 + " %").getBytes(UTF8));

				out.write("==================================================================================================\n".getBytes(UTF8));

				final String reqListFormat = "%s\n";
				getRequirements().forEach(req -> printRequirement(out, reqListFormat, req));
				out.write("==================================================================================================\n".getBytes(UTF8));

			} catch (Exception e) {
				String errMsg = "Error while building console report : " + e.getMessage();
				LOGGER.error(errMsg, e);
				throw new ReqReportBuilderException(errMsg, e);
			}

			LOGGER.info("Finished generating console report");
		}
	}

	private void printRequirement(final OutputStream out, final String reqListFormat, Requirement req) {
		try {
			out.write(String.format(reqListFormat, req.toString()).getBytes(UTF8));
		} catch (IOException e) {
			LOGGER.error("Unable to print requirement having id {} : {}", req.getId(), e);
		}
	}

	@Override
	protected OutputStream getOutput() {
		return System.out;
	}

}
