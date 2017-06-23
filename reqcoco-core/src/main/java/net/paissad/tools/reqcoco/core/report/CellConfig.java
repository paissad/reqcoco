package net.paissad.tools.reqcoco.core.report;

enum CellConfig {

                 NAME("Name", 0),
                 VERSION("Version", 1),
                 REVISION("Revision", 2),
                 DESCRIPTION("Short Description", 3),
                 CODE("Code", 4),
                 CODE_AUTHOR("Code Author", 5),
                 TEST("Test", 6),
                 TEST_AUTHOR("Test Author", 7),
                 LINK("Link", 8);

    public final String header;
    public final int    position;

    private CellConfig(String columnHeaderName, int columnPosition) {
        this.header = columnHeaderName;
        this.position = columnPosition;
    }
}
