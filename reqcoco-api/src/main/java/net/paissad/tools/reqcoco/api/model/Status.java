package net.paissad.tools.reqcoco.api.model;

import lombok.Getter;

public enum Status {

    TODO("TODO"), IN_PROGRESS("IN_PROGRESS"), DONE("DONE"), FAILED("FAILED"), IGNORE("IGNORED");

    @Getter
    private final String displayName;

    private Status(final String displayName) {
        this.displayName = displayName;
    }
}
