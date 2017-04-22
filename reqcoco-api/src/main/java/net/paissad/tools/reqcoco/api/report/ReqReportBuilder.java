package net.paissad.tools.reqcoco.api.report;

import java.io.OutputStream;
import java.util.Collection;

import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;

@FunctionalInterface
public interface ReqReportBuilder {

	void run(final Collection<Requirement> requirements, final OutputStream out) throws ReqReportBuilderException;

}
