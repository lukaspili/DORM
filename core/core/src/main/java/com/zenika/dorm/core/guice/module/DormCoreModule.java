package com.zenika.dorm.core.guice.module;

import com.google.inject.AbstractModule;
import com.zenika.dorm.core.dao.DormDao;
import com.zenika.dorm.core.factory.MetadataExtensionFactory;
import com.zenika.dorm.core.processor.Processor;
import com.zenika.dorm.core.processor.impl.DefaultProcessor;
import com.zenika.dorm.core.repository.DormRepository;
import com.zenika.dorm.core.repository.impl.DefaultDormRepository;
import com.zenika.dorm.core.service.DormService;
import com.zenika.dorm.core.service.impl.DefaultDormService;
import com.zenika.dorm.core.ws.provider.CoreExceptionMapper;
import com.zenika.dorm.core.ws.provider.DormProcessExceptionMapper;
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

        // exception mapper
        bind(CoreExceptionMapper.class);
        bind(DormProcessExceptionMapper.class);

        bind(Processor.class).to(DefaultProcessor.class);
        bind(DormService.class).to(DefaultDormService.class);
        bind(DormRepository.class).to(DefaultDormRepository.class);

        // factories
        bind(MetadataExtensionFactory.class);

        requireBinding(DormDao.class);
    }
}
