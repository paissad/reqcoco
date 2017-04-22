package net.paissad.tools.reqcoco.core.report;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;

public class ReqReportBuilderConsoleImpl extends AbstractReqReportBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReqReportBuilderConsoleImpl.class);

	public ReqReportBuilderConsoleImpl(final Collection<Requirement> requirements) {
		getRequirements().addAll(requirements);
	}

	@Override
	public void build() throws ReqReportBuilderException {

		if (getRequirements().isEmpty()) {
			LOGGER.warn("No requirements = no console report");

		} else {

			LOGGER.info("Starting to generate console report");

			final OutputStream out = getOutput();
			try (BufferedOutputStream bos = new BufferedOutputStream(out, 8192)) {

				final String outputFormat = "%-25s : %s\n";
				out.write(String.format(outputFormat, "Number of requirements", getRequirements().size()).getBytes());
				out.write(String.format(outputFormat, "Code done ratio", getCodeDoneRatio() * 100 + " %").getBytes());
				out.write(String.format(outputFormat, "Tests done ratio", getTestDoneRatio() * 100 + " %").getBytes());

			} catch (Exception e) {
				String errMsg = "Error while building console report : " + e.getMessage();
				LOGGER.error(errMsg, e);
				throw new ReqReportBuilderException(errMsg, e);
			}

			LOGGER.info("Finished generating console report");
		}
	}

	@Override
	protected OutputStream getOutput() {
		return System.out;
	}

}
