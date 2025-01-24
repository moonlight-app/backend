package ru.moonlightapp.backend.core.storage.dialect;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.type.SqlTypes;

public final class CustomH2Dialect extends H2Dialect {

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
