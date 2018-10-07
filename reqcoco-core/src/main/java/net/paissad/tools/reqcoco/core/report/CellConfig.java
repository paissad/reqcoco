package net.paissad.tools.reqcoco.core.report;

enum CellConfig {

                 NAME("Name", 0),
                 GROUP("Group", 1),
                 VERSION("Version", 2),
                 REVISION("Revision", 3),
                 DESCRIPTION("Short Description", 4),
                 CODE_STATUS("Code", 5),
                 CODE_AUTHOR("Code Author", 6),
                 CODE_COMMENT("Code Comment", 7),
                 TEST_STATUS("Test", 8),
                 TEST_AUTHOR("Test Author", 9),
                 TEST_COMMENT("Test Comment", 10),
                 LINK("Link", 11);

    public final String header;
    public final int    position;

    private CellConfig(String columnHeaderName, int columnPosition) {
        this.header = columnHeaderName;
        this.position = columnPosition;
    }
}
