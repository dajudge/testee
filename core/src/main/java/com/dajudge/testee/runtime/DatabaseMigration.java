package com.dajudge.testee.runtime;

import com.dajudge.testee.spi.DataSourceMigrator;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.injection.spi.ResourceInjectionServices;

import javax.sql.DataSource;
import java.util.Set;
import java.util.function.Function;

/**
 * Control of database migration.
 *
 * @author Alex Stockinger, IT-Stockinger
 */
public class DatabaseMigration {
    static void migrateDataSources(
            final Class<?> setupClass,
            final Set<DataSourceMigrator> migrators,
            final ServiceRegistry serviceRegistry
    ) {
        final ResourceInjectionServices injectionServices = serviceRegistry.get(ResourceInjectionServices.class);
        final Function<String, DataSource> dataSourceAccessor = mappedName -> (DataSource) injectionServices
                .registerResourceInjectionPoint(null, mappedName)
                .createResource()
                .getInstance();
        migrators.forEach(it -> it.migrate(setupClass, dataSourceAccessor));
    }
}