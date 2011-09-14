package com.zenika.dorm.core.service.impl;

import com.google.inject.Inject;
import com.zenika.dorm.core.dao.DormDao;
import com.zenika.dorm.core.graph.visitor.filter.WithResourceDependencyVisitorFilter;
import com.zenika.dorm.core.graph.visitor.impl.DependenciesCollector;
import com.zenika.dorm.core.model.Dependency;
import com.zenika.dorm.core.model.DependencyNode;
import com.zenika.dorm.core.model.DormMetadata;
import com.zenika.dorm.core.model.DormResource;
import com.zenika.dorm.core.model.impl.DefaultDependency;
import com.zenika.dorm.core.model.impl.Usage;
import com.zenika.dorm.core.repository.DormRepository;
import com.zenika.dorm.core.service.DormService;
import com.zenika.dorm.core.service.config.DormServiceGetResourceConfig;
import com.zenika.dorm.core.service.config.DormServiceStoreResourceConfig;
import com.zenika.dorm.core.service.get.DormServiceGetMetadataResult;
import com.zenika.dorm.core.service.get.DormServiceGetMetadataValues;
import com.zenika.dorm.core.service.get.DormServiceGetRequest;
import com.zenika.dorm.core.service.get.DormServiceGetResult;
import com.zenika.dorm.core.service.put.DormServicePutRequest;
import com.zenika.dorm.core.service.put.DormServiceStoreResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Lukasz Piliszczuk <lukasz.piliszczuk AT zenika.com>
 */
public class DefaultDormService implements DormService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDormService.class);

    @Inject
    private DormDao dao;

    @Inject
    private DormRepository repository;

    @Override
    public DormServiceStoreResult store(DormServicePutRequest request) {
        return null;
    }

    @Override
    public void storeMetadata(DormMetadata metadata) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Store metadata : " + metadata);
        }

        dao.saveMetadata(metadata);
    }

    @Override
    public void storeResource(DormResource resource, DormServiceStoreResourceConfig config) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Store resource : " + resource + " with config : " + config);
        }

        repository.store(resource, config);
    }

    @Override
    public DormServiceGetMetadataResult getMetadata(DormServiceGetMetadataValues values) {

        DormServiceGetMetadataResult result = new DormServiceGetMetadataResult();

        // if null then get by all usages
        Usage usage = values.getUsage();

        List<DormMetadata> metadatas = null;

        if (values.isGetByQualifier()) {

            DormMetadata metadata = dao.getMetadataByQualifier(values.getMetadata().getQualifier(), usage);

            if (null != metadata) {
                metadatas = new ArrayList<DormMetadata>();
                metadatas.add(metadata);
            }

        } else {
            metadatas = dao.getMetadataByExtension(values.getMetadata().getExtension().getExtensionName(),
                    values.getMetadataExtensionClauses(), usage);
        }

        result.setMetadatas(metadatas);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Result from database : " + result);
        }

        return result;
    }

    @Override
    public DormResource getResource(DormMetadata metadata, DormServiceGetResourceConfig config) {

//        repository.get(metadata)
        return null;
    }

    @Override
    public DormResource getResource(String extension, String path) {
        return null;
    }

    public DormServiceGetResult get(DormServiceGetRequest request) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Get request : " + request);
        }

        DormServiceGetResult result = new DormServiceGetResult();

        if (request.isUniqueResultRequest()) {

            DependencyNode node = dao.getOne(request.getValues(), request.getTransitiveDependencies());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Get unique node : " + dao);
            }

            result.addNode(node);
        } else {
            List<DependencyNode> nodes = dao.get(request.getValues(), request.getTransitiveDependencies());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Get nodes : " + nodes);
            }
            result.setNodes(nodes);
        }

        if (!result.hasResult()) {
            LOG.debug("No result found for the get request : " + request);
            return result;
        }

        if (request.isRepositoryRequest()) {
            for (DependencyNode node : result.getNodes()) {
                Dependency dependency = getDependencyWithResource(node);
                node.setDependency(dependency);
            }
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Get result : " + result);
        }

        return result;
    }

    private Dependency getDependencyWithResource(DependencyNode node) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Get resource for node : " + node);
        }

        DormMetadata metadata = node.getDependency().getMetadata();
        DormResource resource = repository.get(metadata);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Resource from the repository : " + resource);
        }

        if (null == resource) {
            return node.getDependency();
        }

        return DefaultDependency.create(metadata, resource);
    }
}