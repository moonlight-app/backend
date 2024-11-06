package ru.moonlightapp.backend.storage.dialect;

import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.type.SqlTypes;

public final class CustomPostgreSQLDialect extends PostgreSQLDialect {

    @Override
    public int getDefaultTimestampPrecision() {
        return 0;
    }

    @Override
    protected String columnType(int sqlTypeCode) {
        if (sqlTypeCode == SqlTypes.TIMESTAMP_UTC)
            return columnType(SqlTypes.TIMESTAMP);

        return super.columnType(sqlTypeCode);
    }

}
