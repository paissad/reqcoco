package net.paissad.tools.reqcoco.core.report;

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

	/**
	 * The default name of the file containing the report, without the extension.
	 */
	public static final String		DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION	= "REPORT-requirements";

	@Getter(value = AccessLevel.PROTECTED)
	private Collection<Requirement>	requirements								= new LinkedList<>();

	@Getter
	@Setter
	private ReqReportConfig			reportConfig;

	private ReqReportConfig			defaultReportConfig;

	@Getter(value = AccessLevel.PROTECTED)
	@Setter(value = AccessLevel.PROTECTED)
	private OutputStream			output;

	@Override
	public void configure(Collection<Requirement> requirements, ReqReportConfig config) throws ReqReportBuilderException {
		getRequirements().addAll(requirements);
		final ReqReportConfig cfg = config == null ? getDefaultReportConfig() : config;
		this.setReportConfig(cfg);
	}

	/**
	 * @return The number of code marked as done for any version.
	 */
	protected long getCodeDoneCount() {
		return getRequirements().stream().filter(req -> !req.isIgnore() && req.isCodeDone()).count();
	}

	/**
	 * @return The number of requirements for which the code is not done yet.
	 */
	protected long getCodeUndoneCount() {
		return getRequirements().size() - getCodeDoneCount() - getIgnoredRequirementsCount();
	}

	/**
	 * @param version - The version to use for filtering.
	 * @return The number of code marked as done for the specified version.
	 */
	protected long getCodeDoneCount(final String version) {
		return getRequirementByVersion(version).stream().filter(req -> !req.isIgnore() && req.isCodeDone()).count();
	}

	/**
	 * @param version - The version to use for filtering.
	 * @return The number of requirements for which the code is not done yet.
	 */
	protected long getCodeUndoneCount(final String version) {
		return getRequirementByVersion(version).size() - getCodeDoneCount(version) - getIgnoredRequirementsCount(version);
	}

	/**
	 * @return The ratio of requirements for which the codes are marked as done !
	 */
	protected Float getCodeDoneRatio() {
		return getCodeDoneCount() / (float) getRequirements().size();
	}

	/**
	 * @param version : The version to use for filtering
	 * @return The ratio of requirements for which the codes are marked as done !
	 */
	protected Float getCodeDoneRatio(final String version) {
		return getCodeDoneCount(version) / (float) getRequirementByVersion(version).size();
	}

	/**
	 * @return The number of tests marked as done for any version
	 */
	protected long getTestsDoneCount() {
		return getRequirements().stream().filter(req -> !req.isIgnore() && req.isTestDone()).count();
	}

	/**
	 * @return The number of tests marked as done for any version
	 */
	protected long getTestsUndoneCount() {
		return getRequirements().size() - getTestsDoneCount() - getIgnoredRequirementsCount();
	}

	/**
	 * @param version - The version to use for filtering.
	 * @return The number of tests marked as done for the specified version.
	 */
	protected long getTestsDoneCount(final String version) {
		return getRequirementByVersion(version).stream().filter(req -> !req.isIgnore() && req.isTestDone()).count();
	}

	/**
	 * @param version - The version to use for filtering.
	 * @return The number of tests marked as done for the specified version.
	 */
	protected long getTestsUndoneCount(final String version) {
		return getRequirementByVersion(version).size() - getTestsDoneCount(version) - getIgnoredRequirementsCount(version);
	}

	/**
	 * @return The ratio of requirements for which the tests are marked as done !
	 */
	protected Float getTestDoneRatio() {
		return getTestsDoneCount() / (float) getRequirements().size();
	}

	/**
	 * @param version : The version to use for filtering
	 * @return The ratio of requirements for which the tests are marked as done !
	 */
	protected Float getTestDoneRatio(final String version) {
		return getTestsDoneCount(version) / (float) getRequirementByVersion(version).size();
	}

	/**
	 * @return The number of declared/input requirements which are marked as to be ignored for the coverage report.
	 */
	protected long getIgnoredRequirementsCount() {
		final Collection<Requirement> reqs = getRequirements();
		return reqs.stream().filter(Requirement::isIgnore).count();
	}

	/**
	 * @param version - The version to use for filtering.
	 * @return The number of declared/input requirements which are marked as to be ignored for the coverage report.
	 */
	protected long getIgnoredRequirementsCount(final String version) {
		final Collection<Requirement> reqs = getRequirementByVersion(version);
		return reqs.stream().filter(Requirement::isIgnore).count();
	}

	protected Collection<Requirement> getRequirementByVersion(final String version) {
		return Requirements.getByVersion(getRequirements(), version);
	}

	protected ReqReportConfig getDefaultReportConfig() {
		if (this.defaultReportConfig == null) {
			this.defaultReportConfig = new ReqReportConfig() {
			};
		}
		return this.defaultReportConfig;
	}

	/**
	 * @return The default name of the file containing the report WITH the extension.
	 * @see #DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION
	 * @see #getDefaultFileReportExtension()
	 */
	protected String getDefaultReportFilename() {
		return DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION + getDefaultFileReportExtension();
	}

	/**
	 * @return The default expected extension to use for the report filename if needed.
	 */
	protected abstract String getDefaultFileReportExtension();

}
