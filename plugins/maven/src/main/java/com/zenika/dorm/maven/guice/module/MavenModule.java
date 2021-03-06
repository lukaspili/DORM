package com.zenika.dorm.maven.guice.module;

import com.zenika.dorm.core.guice.module.DormExtensionModule;
import com.zenika.dorm.maven.processor.extension.MavenProcessor;
import com.zenika.dorm.maven.processor.extension.MavenRequestAnalyser;
import com.zenika.dorm.maven.provider.ProxyWebResourceWrapper;
import com.zenika.dorm.maven.service.MavenProxyService;
import com.zenika.dorm.maven.service.MavenProxyServiceHttp;
import com.zenika.dorm.maven.service.MavenService;
import com.zenika.dorm.maven.ws.resource.MavenResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lukasz Piliszczuk <lukasz.piliszczuk AT zenika.com>
 */
public class MavenModule extends DormExtensionModule {

    private static final Logger LOG = LoggerFactory.getLogger(MavenModule.class);

    @Override
    protected void configure() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Configure maven guice module");
        }
        standardConfigure();
        bind(MavenResource.class);
        bind(MavenProcessor.class);
        bind(MavenService.class);
        bind(ProxyWebResourceWrapper.class);

    }

    protected void standardConfigure() {
        super.configure();
        bind(MavenProxyService.class).to(MavenProxyServiceHttp.class);
        requestAnalyserBinder.addBinding().to(MavenRequestAnalyser.class);
    }
}
