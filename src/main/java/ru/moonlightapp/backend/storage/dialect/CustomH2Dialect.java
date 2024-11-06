package ru.moonlightapp.backend.storage.dialect;

import org.hibernate.dialect.H2Dialect;

public final class CustomH2Dialect extends H2Dialect {

    @Override
    public int getDefaultTimestampPrecision() {
        return 0;
    }

}
