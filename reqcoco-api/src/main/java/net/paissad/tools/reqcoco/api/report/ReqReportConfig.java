package net.paissad.tools.reqcoco.api.report;

public interface ReqReportConfig {

	default String getTitle() {
		return "Requirements Coverage";
	}

	default String getCodeCoverageDiagramName() {
		return "Code coverage";
	}

	default String getTestsCoverageDiagramName() {
		return "Tests coverage";
	}

	default String getRequirementsTableLegend() {
		return "Table of requirements";
	}
}
