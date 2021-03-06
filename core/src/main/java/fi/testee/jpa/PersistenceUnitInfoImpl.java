/*
 * Copyright (C) 2017 Alex Stockinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.testee.jpa;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Implementation of {@link PersistenceUnitInfo}.
 *
 * @author Alex Stockinger, IT-Stockinger
 */
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {
    private final URL rootUrl;
    private final String provider;
    private final String name;
    private final String actualName;
    private final PersistenceUnitTransactionType transactionType;
    private final DataSource jtaDataSource;
    private final Properties properties;
    private final List<URL> jarFileUrls;
    private final List<String> mappingFileNames;
    private final List<String> managedClassNames;
    private final boolean excludeUnlistedClasses;
    private final ClassLoader classLoader;

    /**
     * Constructor.
     *
     * @param rootUrl                the URL for the jar file or directory that is the root of the persistence unit.
     * @param provider               provider class name.
     * @param name                   the persistence unit name.
     * @param transactionType        the transaction type.
     * @param jtaDataSource          the JTA data source.
     * @param properties             the properties.
     * @param jarFileUrls            <code>jar-file</code> elements from <code>persistence.xml</code>.
     * @param mappingFileNames       <code>mapping-file</code> elements from <code>persistence.xml</code>.
     * @param managedClassNames      <code>class</code> elements from <code>persistence.xml</code>.
     * @param excludeUnlistedClasses <code>exclude-unlisted-classes</code> element from <code>persistence.xml</code>.
     * @param classLoader            the class loader to use.
     */
    public PersistenceUnitInfoImpl(
            final URL rootUrl,
            final String provider,
            final String name,
            final String actualName,
            final PersistenceUnitTransactionType transactionType,
            final DataSource jtaDataSource,
            final Properties properties,
            final List<URL> jarFileUrls,
            final List<String> mappingFileNames,
            final List<String> managedClassNames,
            final boolean excludeUnlistedClasses,
            final ClassLoader classLoader
    ) {
        this.rootUrl = rootUrl;
        this.provider = provider;
        this.name = name;
        this.actualName = actualName;
        this.transactionType = transactionType;
        this.jtaDataSource = jtaDataSource;
        this.properties = properties;
        this.jarFileUrls = jarFileUrls;
        this.mappingFileNames = mappingFileNames;
        this.managedClassNames = managedClassNames;
        this.excludeUnlistedClasses = excludeUnlistedClasses;
        this.classLoader = classLoader;
    }

    @Override
    public String getPersistenceUnitName() {
        return name;
    }

    public String getActualPersistenceUnitName() {
        return actualName;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return provider;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return transactionType;
    }

    @Override
    public DataSource getJtaDataSource() {
        return jtaDataSource;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return null;
    }

    @Override
    public List<String> getMappingFileNames() {
        return mappingFileNames;
    }

    @Override
    public List<URL> getJarFileUrls() {
        return jarFileUrls;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return rootUrl;
    }

    @Override
    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return excludeUnlistedClasses;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return null;
    }

    @Override
    public ValidationMode getValidationMode() {
        return null;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public void addTransformer(final ClassTransformer transformer) {
        // This is called by eclipselink but doing nothing with it seems to be no problem right now
        // TODO investigate
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return null;
    }
}
