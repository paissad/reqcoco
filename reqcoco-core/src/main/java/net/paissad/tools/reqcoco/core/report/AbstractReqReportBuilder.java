package net.paissad.tools.reqcoco.core.report;

import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;

import lombok.AccessLevel;
import lombok.Getter;
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.report.ReqReportBuilder;

public abstract class AbstractReqReportBuilder implements ReqReportBuilder {

	@Getter(value = AccessLevel.PROTECTED)
	private Collection<Requirement> requirements = new LinkedList<>();

	@Override
	public void run(Collection<Requirement> requirements, OutputStream out) throws ReqReportBuilderException {
		build();
	}

	/**
	 * Builds the report.
	 * 
	 * @throws ReqReportBuilderException
	 */
	abstract protected void build() throws ReqReportBuilderException;

	abstract protected OutputStream getOutput();

	/**
	 * @return The ratio of requirements for which the codes are marked as done !
	 */
	protected Float getCodeDoneRatio() {
		return getRequirements().stream().filter(Requirement::isCodeDone).count() / (float) getRequirements().size();
	}

	/**
	 * @return The ratio of requirements for which the tests are marked as done !
	 */
	protected Float getTestDoneRatio() {
		return getRequirements().stream().filter(Requirement::isTestDone).count() / (float) getRequirements().size();
	}

}
