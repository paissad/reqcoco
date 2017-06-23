package net.paissad.tools.reqcoco.core.report;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.report.ReqReportConfig;

public class ReqReportBuilderConsole extends AbstractReqReportBuilder {

	private static final Logger		LOGGER				= LoggerFactory.getLogger(ReqReportBuilderConsole.class);

	private static final String		LOGGER_PREFIX_TAG	= String.format("%-15s -", "[ConsoleReport]");

	private static final Charset	UTF8				= Charset.forName("UTF-8");

	@Override
	public void configure(final Collection<Requirement> requirements, final ReqReportConfig config) throws ReqReportBuilderException {
		super.configure(requirements, config);
		this.setOutput(System.out);
	}

	@Override
	public void run() throws ReqReportBuilderException {

		if (getRequirements().isEmpty()) {
			LOGGER.warn("{} No requirements = no console report", LOGGER_PREFIX_TAG);

		} else {

			LOGGER.info("{} Starting to generate console report", LOGGER_PREFIX_TAG);

			try {

				final OutputStream out = getOutput(); // Important note : never close the standard output stream :D
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
				LOGGER.error(LOGGER_PREFIX_TAG + errMsg, e);
				throw new ReqReportBuilderException(errMsg, e);
			}

			LOGGER.info("Finished generating console report", LOGGER_PREFIX_TAG);
		}
	}

	private void printRequirement(final OutputStream out, final String reqListFormat, Requirement req) {
		try {
			out.write(String.format(reqListFormat, req.toString()).getBytes(UTF8));
		} catch (IOException e) {
			LOGGER.error(LOGGER_PREFIX_TAG + "Unable to print requirement having name {} : {}", req.getName(), e);
		}
	}

	@Override
	protected String getDefaultFileReportExtension() {
		return "";
	}

}
