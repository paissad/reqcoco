package net.paissad.tools.reqcoco.api.report;

public interface ReqReportConfig {

	default String getTitle() {
		return "Requirements Coverage";
	}

	default String getCodeCoverageDiagramName() {
		return "Source Code Coverage";
	}

	default String getTestsCoverageDiagramName() {
		return "Tests Code Coverage";
	}

	default String getRequirementsTableLegend() {
		return "Table of requirements";
	}
}
