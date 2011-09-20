package com.zenika.dorm.core.test.dao.neo4j.util;

import com.google.guiceberry.GuiceBerryModule;
import com.sun.jersey.api.client.WebResource;
import com.zenika.dorm.core.dao.neo4j.Neo4jIndex;
import com.zenika.dorm.core.dao.neo4j.provider.IndexProvider;
import com.zenika.dorm.core.dao.neo4j.provider.WebResourceWrapper;
import com.zenika.dorm.core.service.spi.ExtensionFactoryServiceLoader;

/**
 * @author Antoine ROUAZE <antoine.rouaze AT zenika.com>
 */
public class Neo4jTestModule extends GuiceBerryModule {

    @Override
    protected void configure() {
        super.configure();

        bind(WebResourceWrapper.class);

        requireBinding(WebResource.class);

        bind(Neo4jIndex.class).toProvider(IndexProvider.class);
        bind(ExtensionFactoryServiceLoader.class);
    }
}
