package com.zenika.dorm.maven.processor.extension;

import com.zenika.dorm.core.graph.Dependency;
import com.zenika.dorm.core.graph.DependencyNode;
import com.zenika.dorm.core.graph.impl.DefaultDependencyNode;
import com.zenika.dorm.core.graph.impl.Usage;
import com.zenika.dorm.core.model.DormMetadata;
import com.zenika.dorm.core.model.DormRequest;
import com.zenika.dorm.core.model.builder.DependencyBuilderFromRequest;
import com.zenika.dorm.core.model.builder.DormRequestBuilder;
import com.zenika.dorm.core.model.builder.MetadataBuilderFromRequest;
import com.zenika.dorm.core.processor.impl.AbstractProcessorExtension;
import com.zenika.dorm.maven.exception.MavenException;
import com.zenika.dorm.maven.model.impl.MavenMetadataExtension;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The maven processor needs to create an abstract dependency node which will be the parent of the
 * following maven nodes : maven pom node, maven jar node, maven sha1 node, etc...
 * The only difference between theses nodes is the file and his type : pom.xml, jar, etc...
 *
 * See : https://docs.google.com/drawings/d/1N1epmWY3dUy7th-VwrSNk1HXf6srEi0RoUoETQbe8qM/edit?hl=fr
 *
 * @author Lukasz Piliszczuk <lukasz.piliszczuk AT zenika.com>
 */
public class MavenProcessor extends AbstractProcessorExtension {

    private static final Logger LOG = LoggerFactory.getLogger(MavenProcessor.class);

    public static final String ENTITY_TYPE = "entity";

    @Override
    public DependencyNode push(DormRequest request) {

        LOG.debug("Maven push with request : " + request);

        if (!request.hasFile()) {
            throw new MavenException("File is required.");
        }

        String type = getRequestType(request);

        // get the maven metadatas from the request
        String groupId = request.getProperty(MavenMetadataExtension.METADATA_GROUPID);
        String artifactId = request.getProperty(MavenMetadataExtension.METADATA_ARTIFACTID);
        String versionId = request.getProperty(MavenMetadataExtension.METADATA_VERSIONID);

        // create the entity extension which is the same as the child with a different type
        MavenMetadataExtension entityExtension = new MavenMetadataExtension(groupId, artifactId, versionId,
                MavenProcessor.ENTITY_TYPE);

        // entity dependencuy has no file
        DormRequest entityRequest = new DormRequestBuilder(request)
                .file(null)
                .filename(null)
                .build();

        Dependency entityDependency = new DependencyBuilderFromRequest(entityRequest, entityExtension).build();
        LOG.debug("Maven entity dependency = " + entityDependency);

        // create the real maven dependency to push
        MavenMetadataExtension childExtension = new MavenMetadataExtension(groupId, artifactId, versionId, type);

        // replace the default usage by the maven internal for the child dependency
        Usage childUsage = Usage.createInternal(MavenMetadataExtension.NAME);

        Dependency dependency = new DependencyBuilderFromRequest(request, childExtension).usage(childUsage).build();
        LOG.debug("Maven real dependency = " + dependency);

        DependencyNode root = DefaultDependencyNode.create(entityDependency);
        DependencyNode node = DefaultDependencyNode.create(dependency);
        root.addChild(node);

        return root;
    }

    @Override
    public DormMetadata getMetadata(DormRequest request) {

        LOG.debug("Maven get with request : " + request);

        String type = request.getProperty(MavenMetadataExtension.METADATA_TYPE);
        checkMavenType(type);

        String groupId = request.getProperty(MavenMetadataExtension.METADATA_GROUPID);
        String artifactId = request.getProperty(MavenMetadataExtension.METADATA_ARTIFACTID);
        String versionId = request.getProperty(MavenMetadataExtension.METADATA_VERSIONID);


        MavenMetadataExtension extension = new MavenMetadataExtension(groupId, artifactId, versionId,
                type);
        LOG.debug("Maven metadata extension from request : " + extension);

        DormMetadata metadata = new MetadataBuilderFromRequest(request, extension).build();
        LOG.debug("Maven metadata from request : " + metadata);

        return metadata;
    }

    private String getRequestType(DormRequest request) {

        // get the maven type from the filename
        String type = FilenameUtils.getExtension(request.getFilename());
        checkMavenType(type);
        return type;
    }

    private void checkMavenType(String type) {
        LOG.debug("Type of the maven file = " + type);
        if (!type.equalsIgnoreCase("jar") && type.equalsIgnoreCase("pom") && type.equalsIgnoreCase("sha1")) {
            throw new MavenException("Invalid maven type : " + type);
        }
    }
}
