package com.zenika.dorm.core.guice.module.dao;

import com.google.inject.AbstractModule;
import com.zenika.dorm.core.dao.DormDao;
import com.zenika.dorm.core.dao.neo4j.DormDaoNeo4j;
import com.zenika.dorm.core.dao.neo4j.Neo4jIndex;
import com.zenika.dorm.core.dao.neo4j.provider.Neo4jIndexProvider;
import com.zenika.dorm.core.dao.neo4j.provider.Neo4jWebResourceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Antoine ROUAZE <antoine.rouaze AT zenika.com>
 */
public class DormCoreNeo4jModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(DormCoreNeo4jModule.class);

    @Override
    protected void configure() {

        if (LOG.isInfoEnabled()) {
            LOG.info("Configure dorm neo4j guice module");
        }

//        bind(Neo4jWebResourceWrapper.class);
//        bind(Neo4jIndexProvider.class);
        bind(DormDao.class).to(DormDaoNeo4j.class);
    }
}
