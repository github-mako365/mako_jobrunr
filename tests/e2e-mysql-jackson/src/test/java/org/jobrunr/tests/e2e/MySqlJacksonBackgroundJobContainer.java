package org.jobrunr.tests.e2e;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class MySqlJacksonBackgroundJobContainer extends AbstractBackgroundJobSqlContainer {

    public MySqlJacksonBackgroundJobContainer(JdbcDatabaseContainer sqlContainer) {
        super("jobrunr-e2e-mysql-jackson:1.0", sqlContainer);
    }

    @Override
    protected StorageProvider initStorageProvider(JdbcDatabaseContainer sqlContainer) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(sqlContainer.getJdbcUrl() + "?rewriteBatchedStatements=true&pool=true");
        dataSource.setUser(sqlContainer.getUsername());
        dataSource.setPassword(sqlContainer.getPassword());
        return SqlStorageProviderFactory.using(dataSource);
    }

}
