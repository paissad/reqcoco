package net.paissad.tools.reqcoco.api.report;

import java.util.Collection;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;

public interface ReqReportBuilder {

	/**
	 * Run the current report builder. {@link ReqReportBuilder#configure(Collection, ReqReportConfig)} must (should) be called first.
	 * 
	 * @throws ReqReportBuilderException If an error occurs while building the report.
	 * @see #configure(Collection, ReqReportConfig)
	 */
	void run() throws ReqReportBuilderException;

	/**
	 * @param requirements - The requirements from which the report is to be configured.
	 * @param config - The report builder configuration.
	 * @throws ReqReportBuilderException If an errors occurs while configuring the report builder.
	 */
	void configure(final Collection<Requirement> requirements, final ReqReportConfig config) throws ReqReportBuilderException;

}
