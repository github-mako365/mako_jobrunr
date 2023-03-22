package org.jobrunr.storage.sql.sqlserver;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.jupiter.api.AfterAll;

import javax.sql.DataSource;

class C3P0SQLServerStorageProviderTest extends AbstractSQLServerStorageProviderTest {

    private static ComboPooledDataSource dataSource;

    @Override
    protected DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new ComboPooledDataSource();
            dataSource.setJdbcUrl(sqlContainer.getJdbcUrl());
            dataSource.setUser(sqlContainer.getUsername());
            dataSource.setPassword(sqlContainer.getPassword());
        }

        return dataSource;
    }

    @AfterAll
    public static void destroyDatasource() {
        dataSource.close();
    }
}