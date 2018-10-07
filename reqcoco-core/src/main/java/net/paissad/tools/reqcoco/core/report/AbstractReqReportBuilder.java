package net.paissad.tools.reqcoco.core.report;

import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public static final String      DEFAULT_REPORT_FILENAME_WITHOUT_EXTENSION = "REPORT-requirements";

    @Getter(value = AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE)
    private Collection<Requirement> requirements;

    @Getter
    @Setter
    private ReqReportConfig         reportConfig;

    private ReqReportConfig         defaultReportConfig;

    @Getter(value = AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PROTECTED)
    private OutputStream            output;

    @Override
    public void configure(final Collection<Requirement> requirements, final ReqReportConfig config) throws ReqReportBuilderException {

        final ReqReportConfig cfg = config == null ? getDefaultReportConfig() : config;

        this.setReportConfig(cfg);

        if (cfg.getFilteredVersions() == null || cfg.getFilteredVersions().isEmpty()) {
            setRequirements(requirements);

        } else {
            final List<Requirement> filteredRequirements = requirements.stream().filter(req -> cfg.getFilteredVersions().contains(req.getVersion())).collect(Collectors.toList());
            setRequirements(filteredRequirements);
        }
    }

    /**
     * @return The number of code marked as done for any version.
     */
    protected long getCodeDoneCount() {
        return getRequirements().stream().filter(req -> !req.isCodeIgnore() && req.isCodeDone()).count();
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
        return getRequirementByVersion(version).stream().filter(req -> !req.isCodeIgnore() && req.isCodeDone()).count();
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
        return getRequirements().stream().filter(req -> !req.isCodeIgnore() && req.isTestDone()).count();
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
        return getRequirementByVersion(version).stream().filter(req -> !req.isCodeIgnore() && req.isTestDone()).count();
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
            this.defaultReportConfig = new ReqReportConfig();
        }
        return this.defaultReportConfig;
    }

    protected float getRequirementsIgnoredRatio(final String version) {
        return 1f * getIgnoredRequirementsCount(version) / getRequirementByVersion(version).size();
    }

    protected Collection<ReqReport> buildReqReports() {

        final Set<ReqReport> reqReports = new LinkedHashSet<>();

        // Retrieve available versions
        final Stream<String> versions = getRequirements().stream().map(Requirement::getVersion).distinct();

        versions.sorted().forEach(version -> {

            final ReqReport reqReport = new ReqReport();

            final Collection<Requirement> reqs = Requirements.getByVersion(getRequirements(), version);

            reqReport.setVersion(version);
            reqReport.setTotalRequirements(reqs.size());
            reqReport.setRequirements(reqs);

            reqReport.setCodeDoneCount(getCodeDoneCount(version));
            reqReport.setCodeUndoneCount(getCodeUndoneCount(version));
            reqReport.setCodeDoneRatio(getCodeDoneRatio(version));

            reqReport.setTestsDoneCount(getTestsDoneCount(version));
            reqReport.setTestsUndoneCount(getTestsUndoneCount(version));
            reqReport.setTestsDoneRatio(getTestDoneRatio(version));

            reqReport.setIgnoredRequirementsCount(getIgnoredRequirementsCount(version));
            reqReport.setIgnoredRequirementsRatio(getRequirementsIgnoredRatio(version));

            reqReports.add(reqReport);
        });

        return reqReports;
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
