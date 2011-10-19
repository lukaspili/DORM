package com.zenika.dorm.maven.test.service;

import com.google.inject.Guice;
import com.zenika.dorm.maven.model.MavenUri;
import com.zenika.dorm.maven.model.builder.MavenMetadataBuilder;
import com.zenika.dorm.maven.service.MavenProxyService;
import com.zenika.dorm.maven.service.MavenProxyServiceHttp;
import com.zenika.dorm.maven.test.module.MavenTestModule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Antoine ROUAZE <antoine.rouaze AT zenika.com>
 */
public class MavenFederationServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(MavenFederationServiceTest.class);

    @Test
    public void test() {

        MavenProxyService service = Guice.createInjector(new MavenTestModule()).getInstance(MavenProxyServiceHttp.class);

        MavenBuildInfo info = new MavenBuildInfo("jar", null, null, null);
        MavenMetadata metadata = new MavenMetadataBuilder()
                .groupId("commons-io")
                .artifactId("commons-io")
                .version("1.4")
                .buildInfo(info)
                .build();

        DormResource resource = service.getArtifact(metadata);
        MavenUri uri = new MavenUri(metadata);
        assertThat(resource.getExtension()).isEqualTo(uri.getFilename().getExtension());
        assertThat(resource.getName()).isEqualTo(uri.getFilename().getFileNameWithoutExtension());
    }
}
