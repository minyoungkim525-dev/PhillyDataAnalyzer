package common;

public enum PropertyColumn {
    ZIP_CODE("zip_code"),
    MARKET_VALUE("market_value"),
    TOTAL_LIVABLE_AREA("total_livable_area");

    private final String columnName;

    PropertyColumn(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
