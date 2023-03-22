package org.jobrunr.storage.sql.common;

import org.jobrunr.JobRunrException;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.StorageProviderUtils.DatabaseOptions;
import org.jobrunr.storage.sql.SqlStorageProvider;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;

import static org.jobrunr.utils.reflection.ReflectionUtils.cast;

public class SqlStorageProviderFactory {

    protected SqlStorageProviderFactory() {
    }

    public static StorageProvider using(DataSource dataSource) {
        return using(dataSource, null, DatabaseOptions.CREATE);
    }

    public static StorageProvider using(DataSource dataSource, String tablePrefix) {
        return using(dataSource, tablePrefix, DatabaseOptions.CREATE);
    }

    public static StorageProvider using(DataSource dataSource, String tablePrefix, DatabaseOptions databaseOptions) {
        final SqlStorageProviderFactory sqlStorageProviderFactory = new SqlStorageProviderFactory();
        return sqlStorageProviderFactory.getStorageProviderUsingDataSource(dataSource, tablePrefix, databaseOptions);
    }

    StorageProvider getStorageProviderUsingDataSource(DataSource dataSource, String tablePrefix, DatabaseOptions databaseOptions) {
        try (Connection connection = dataSource.getConnection()) {
            String jdbcUrl = connection.getMetaData().getURL();
            return getStorageProviderByJdbcUrl(jdbcUrl, dataSource, tablePrefix, databaseOptions);
        } catch (SQLException e) {
            throw JobRunrException.shouldNotHappenException(e);
        }
    }

    StorageProvider getStorageProviderByJdbcUrl(String jdbcUrl, DataSource dataSource, String tablePrefix, DatabaseOptions databaseOptions) {
        final Class<SqlStorageProvider> storageProviderClassByJdbcUrl = getStorageProviderClassByJdbcUrl(jdbcUrl);
        return getStorageProvider(storageProviderClassByJdbcUrl, dataSource, tablePrefix, databaseOptions);
    }

    Class<SqlStorageProvider> getStorageProviderClassByJdbcUrl(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:sqlite")) {
            return getStorageProviderClass(SqlStorageProvider.class.getPackage().getName() + ".sqlite.SqLiteStorageProvider");
        } else if (jdbcUrl.startsWith("jdbc:h2")) {
            return getStorageProviderClass(SqlStorageProvider.class.getPackage().getName() + ".h2.H2StorageProvider");
        } else if (jdbcUrl.startsWith("jdbc:postgres")) {
            return getStorageProviderClass(SqlStorageProvider.class.getPackage().getName() + ".postgres.PostgresStorageProvider");
        } else if (jdbcUrl.startsWith("jdbc:mysql")) {
            return getStorageProviderClass(SqlStorageProvider.class.getPackage().getName() + ".mysql.MySqlStorageProvider");
        } else if (jdbcUrl.startsWith("jdbc:mariadb")) {
            return getStorageProviderClass(SqlStorageProvider.class.getPackage().getName() + ".mariadb.MariaDbStorageProvider");
        } else if (jdbcUrl.startsWith("jdbc:oracle")) {
            return getStorageProviderClass(SqlStorageProvider.class.getPackage().getName() + ".oracle.OracleStorageProvider");
        } else if (jdbcUrl.startsWith("jdbc:sqlserver")) {
            return getStorageProviderClass(SqlStorageProvider.class.getPackage().getName() + ".sqlserver.SQLServerStorageProvider");
        } else if (jdbcUrl.startsWith("jdbc:db2")) {
            return getStorageProviderClass(SqlStorageProvider.class.getPackage().getName() + ".db2.DB2StorageProvider");
        }
        throw unsupportedDataSourceException(jdbcUrl);
    }

    StorageProvider getStorageProvider(Class<SqlStorageProvider> jobStorageProviderClass, DataSource dataSource, String tablePrefix, DatabaseOptions databaseOptions) {
        try {
            final Constructor<?> declaredConstructor = jobStorageProviderClass.getDeclaredConstructor(DataSource.class, String.class, DatabaseOptions.class);
            return (StorageProvider) declaredConstructor.newInstance(dataSource, tablePrefix, databaseOptions);
        } catch (ReflectiveOperationException e) {
            throw JobRunrException.shouldNotHappenException(e);
        }
    }

    Class<SqlStorageProvider> getStorageProviderClass(String className) {
        try {
            return cast(Class.forName(className));
        } catch (ReflectiveOperationException e) {
            throw JobRunrException.shouldNotHappenException(e);
        }
    }

    JobRunrException unsupportedDataSourceException(String jdbcUrl) {
        return new JobRunrException("Are you running an unsupported DataSource or Database? Please check the documentation. If you think this is wrong, please open an issue using the following url: https://github.com/jobrunr/jobrunr/issues/new?template=bug_report.md&title=%5BBUG%5D5%20-%20missing%20DataSource%20" + jdbcUrl);
    }
}
