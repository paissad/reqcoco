package net.paissad.tools.reqcoco.runner;

import lombok.Getter;

public enum ExitStatus {

	OK(0), OPTIONS_PARSING_ERROR(1);

	@Getter
	private int code;

	private ExitStatus(final int code) {
		this.code = code;
	}
}
