package com.zenika.dorm.maven.test.helper;

import com.zenika.dorm.core.graph.Dependency;
import com.zenika.dorm.core.graph.DependencyNode;
import com.zenika.dorm.core.graph.impl.DefaultDependency;
import com.zenika.dorm.core.graph.impl.DefaultDependencyNode;
import com.zenika.dorm.core.graph.impl.Usage;
import com.zenika.dorm.core.model.DormMetadata;
import com.zenika.dorm.core.model.DormMetadataExtension;
import com.zenika.dorm.core.model.DormRequest;
import com.zenika.dorm.core.model.impl.DefaultDormMetadata;
import com.zenika.dorm.core.test.helper.ExtensionFixtures;
import com.zenika.dorm.maven.model.impl.MavenMetadataExtension;
import com.zenika.dorm.maven.processor.extension.MavenProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukasz Piliszczuk <lukasz.piliszczuk AT zenika.com>
 */
public class MavenFixtures extends ExtensionFixtures {

    private static final Logger LOG = LoggerFactory.getLogger(MavenFixtures.class);

    /**
     * Maven metadata extension
     */
    private String groupId = "testgroup";
    private String artifactId = "testartifact";
    private String versionId = "testversion";
    private String type = "jar";
    private String origin = MavenMetadataExtension.NAME;

    @Override
    public DormMetadataExtension getMetadataExtension() {
        return new MavenMetadataExtension(groupId, artifactId, versionId, type);
    }

    @Override
    public Map<String, String> getRequestPropertiesForExtension() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(DormRequest.ORIGIN, origin);
        properties.put(MavenMetadataExtension.METADATA_GROUPID, groupId);
        properties.put(MavenMetadataExtension.METADATA_ARTIFACTID, artifactId);
        properties.put(MavenMetadataExtension.METADATA_VERSIONID, versionId);
        return properties;
    }

    /**
     * Create a maven dependency
     * All maven dependency must have the usage internal because of top generic "entity" dependency
     *
     * @return
     */
    @Override
    public Dependency getDependencyWithFile() {

        Usage usage = Usage.createInternal(MavenMetadataExtension.NAME);
        LOG.trace("Maven dependency fixture has the internal usage = " + usage);

        return DefaultDependency.create(getMetadata(), usage, getDormFile());
    }

    public MavenMetadataExtension getEntityExtension() {
        return new MavenMetadataExtension(groupId, artifactId, versionId,
                MavenProcessor.ENTITY_TYPE);
    }

    public DormMetadata getEntityMetadata() {
        return DefaultDormMetadata.create(getVersion(), getEntityExtension());
    }

    /**
     * Entity dependency has no file
     *
     * @return
     */
    public Dependency getEntityDependency() {
        return DefaultDependency.create(getEntityMetadata());
    }

    public DependencyNode getEntityNodeWithChild() {
        DependencyNode node = DefaultDependencyNode.create(getEntityDependency());
        node.addChild(getNodeWithFile());
        return node;
    }
}
