package com.zenika.dorm.core.guice.module;

import com.google.inject.AbstractModule;
import com.zenika.dorm.core.dao.DormDao;
import com.zenika.dorm.core.processor.DormProcessor;
import com.zenika.dorm.core.repository.DormRepository;
import com.zenika.dorm.core.security.DormSecurity;
import com.zenika.dorm.core.service.DefaultDormService;
import com.zenika.dorm.core.service.DormService;
import com.zenika.dorm.core.service.spi.ExtensionFactoryServiceLoader;
import com.zenika.dorm.core.ws.handler.CoreExceptionMapper;
import com.zenika.dorm.core.ws.resource.DormGenericResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lukasz Piliszczuk <lukasz.piliszczuk AT zenika.com>
 */
public class DormCoreModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(DormCoreModule.class);

    @Override
    protected void configure() {

        if (LOG.isInfoEnabled()) {
            LOG.info("Configure dorm core guice module");
        }

        bind(DormService.class).to(DefaultDormService.class);
        bind(DormGenericResource.class);
        bind(CoreExceptionMapper.class);
        bind(ExtensionFactoryServiceLoader.class);
        bind(DormProcessor.class);
        bind(DormSecurity.class);

        requireBinding(DormDao.class);
        requireBinding(DormRepository.class);
    }
}
