package net.paissad.tools.reqcoco.core.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.paissad.tools.reqcoco.api.exception.ReqReportBuilderException;
import net.paissad.tools.reqcoco.api.model.Requirement;
import net.paissad.tools.reqcoco.api.model.Requirements;
import net.paissad.tools.reqcoco.api.report.ReqReportBuilder;
import net.paissad.tools.reqcoco.api.report.ReqReportConfig;

public abstract class AbstractReqReportBuilder implements ReqReportBuilder {

	@Getter(value = AccessLevel.PROTECTED)
	private Collection<Requirement>	requirements	= new LinkedList<>();

	@Getter
	@Setter
	private ReqReportConfig			reportConfig;

	private ReqReportConfig			defaultReportConfig;

	@Override
	public void run(Collection<Requirement> requirements, OutputStream out, final ReqReportConfig config) throws ReqReportBuilderException {
		this.setReportConfig(config);
		build();
	}

	/**
	 * Builds the report.
	 * 
	 * @throws ReqReportBuilderException
	 */
	protected abstract void build() throws ReqReportBuilderException;

	protected abstract OutputStream getOutput() throws IOException;

	/**
	 * @return The number of code marked as done for any version.
	 */
	protected long getCodeDoneCount() {
		return getRequirements().stream().filter(Requirement::isCodeDone).count();
	}

	/**
	 * @param versionValue - The version to use for filtering.
	 * @return The number of code marked as done for the specified version.
	 */
	protected long getCodeDoneCount(final String versionValue) {
		return getRequirementByVersionValue(versionValue).stream().filter(Requirement::isCodeDone).count();
	}

	/**
	 * @return The ratio of requirements for which the codes are marked as done !
	 */
	protected Float getCodeDoneRatio() {
		final Collection<Requirement> reqs = getRequirements();
		return reqs.stream().filter(Requirement::isCodeDone).count() / (float) reqs.size();
	}

	/**
	 * @param versionValue : The version to use for filtering
	 * @return The ratio of requirements for which the codes are marked as done !
	 */
	protected Float getCodeDoneRatio(final String versionValue) {
		final Collection<Requirement> reqs = getRequirementByVersionValue(versionValue);
		return reqs.stream().filter(Requirement::isCodeDone).count() / (float) reqs.size();
	}

	/**
	 * @return The number of tests marked as done for any version
	 */
	protected long getTestsDoneCount() {
		return getRequirements().stream().filter(Requirement::isTestDone).count();
	}

	/**
	 * @param versionValue - The version to use for filtering.
	 * @return The number of tests marked as done for the specified version.
	 */
	protected long getTestsDoneCount(final String versionValue) {
		return getRequirementByVersionValue(versionValue).stream().filter(Requirement::isTestDone).count();
	}

	/**
	 * @return The ratio of requirements for which the tests are marked as done !
	 */
	protected Float getTestDoneRatio() {
		final Collection<Requirement> reqs = getRequirements();
		return reqs.stream().filter(Requirement::isTestDone).count() / (float) reqs.size();
	}

	/**
	 * @param versionValue : The version to use for filtering
	 * @return The ratio of requirements for which the tests are marked as done !
	 */
	protected Float getTestDoneRatio(final String versionValue) {
		final Collection<Requirement> reqs = getRequirementByVersionValue(versionValue);
		return reqs.stream().filter(Requirement::isTestDone).count() / (float) reqs.size();
	}

	protected Collection<Requirement> getRequirementByVersionValue(final String versionValue) {
		return Requirements.getByVersion(getRequirements(), versionValue);
	}

	protected ReqReportConfig getDefaultReportConfig() {
		if (this.defaultReportConfig == null) {
			this.defaultReportConfig = new ReqReportConfig() {
			};
		}
		return this.defaultReportConfig;
	}
}
