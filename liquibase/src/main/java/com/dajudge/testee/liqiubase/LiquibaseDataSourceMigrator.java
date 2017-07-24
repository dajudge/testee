package com.dajudge.testee.liqiubase;

import com.dajudge.testee.exceptions.TesteeException;
import com.dajudge.testee.liqiubase.annotations.Liquibase;
import com.dajudge.testee.spi.DataSourceMigrator;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

import static com.dajudge.testee.utils.AnnotationUtils.collectAnnotations;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * {@link DataSourceMigrator} based on {@link liquibase.Liquibase}.
 *
 * @author Alex Stockinger, IT-Stockinger
 */
public class LiquibaseDataSourceMigrator implements DataSourceMigrator {
    @Override
    public void migrate(final Class<?> testSetupClass, final Function<String, DataSource> dataSourceProvider) {
        collectAnnotations(testSetupClass, Liquibase.class).stream().forEach(it -> liquibase(it, dataSourceProvider));
    }

    private void liquibase(
            final Liquibase annotation,
            final Function<String, DataSource> dataSourceProvider
    ) {
        try (final Connection c = dataSourceProvider.apply(annotation.dataSource()).getConnection()) {
            final String contexts = isBlank(annotation.contexts()) ? null : annotation.contexts();
            final ResourceAccessor resourceAccessor = annotation.resourceAccessorFactory().newInstance().create();
            applyChangelog(c, contexts, resourceAccessor);
        } catch (final LiquibaseException | SQLException e) {
            throw new TesteeException("Failed to apply Liquibase changelog", e);
        } catch (final IllegalAccessException | InstantiationException e) {
            throw new TesteeException("Failed to instantiate ResourceAccessorFactory", e);
        }
    }

    private void applyChangelog(
            final Connection c,
            final String contexts,
            final ResourceAccessor resourceAccessor
    ) throws LiquibaseException {
        liquibase.Liquibase liquibase = null;
        final Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(c));
        liquibase = new liquibase.Liquibase("liquibase/h2.xml", resourceAccessor, database);
        liquibase.update(contexts);
    }
}
