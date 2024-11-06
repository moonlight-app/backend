package ru.moonlightapp.backend.storage.dialect;

import org.hibernate.dialect.PostgreSQLDialect;

public final class CustomPostgreSQLDialect extends PostgreSQLDialect {

    @Override
    public int getDefaultTimestampPrecision() {
        return 0;
    }

}
