package net.paissad.tools.reqcoco.runner;

import lombok.Getter;

public enum ExitStatus {

	OK(0), OPTIONS_PARSING_ERROR(1), REQUIREMENTS_INPUT_PARSE_ERROR(2), BUILD_REPORT_ERROR(3), I_O_ERROR(4), URI_BUILD_ERROR(5);

	@Getter
	private int code;

	private ExitStatus(final int code) {
		this.code = code;
	}
}
