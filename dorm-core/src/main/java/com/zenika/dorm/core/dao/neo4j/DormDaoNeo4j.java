package com.zenika.dorm.core.dao.neo4j;


import com.google.inject.Inject;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.zenika.dorm.core.dao.DormDao;
import com.zenika.dorm.core.dao.neo4j.util.Neo4jRequestExecutor;
import com.zenika.dorm.core.dao.neo4j.util.RequestExecutor;
import com.zenika.dorm.core.exception.CoreException;
import com.zenika.dorm.core.model.Dependency;
import com.zenika.dorm.core.model.DependencyNode;
import com.zenika.dorm.core.model.impl.DefaultDependency;
import com.zenika.dorm.core.model.impl.DefaultDependencyNode;
import com.zenika.dorm.core.model.impl.DefaultDormMetadata;
import com.zenika.dorm.core.model.impl.Usage;
import com.zenika.dorm.core.graph.visitor.impl.DependenciesNodeCollector;
import com.zenika.dorm.core.model.DormMetadata;
import com.zenika.dorm.core.model.DormMetadataExtension;
import com.zenika.dorm.core.model.mapper.MetadataExtensionMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Lukasz Piliszczuk <lukasz.piliszczuk AT zenika.com>
 */
public class DormDaoNeo4j implements DormDao {

    private static final Logger LOG = LoggerFactory.getLogger(DormDaoNeo4j.class);

    private RequestExecutor executor;
    private Neo4jIndex index;

    @Inject
    public DormDaoNeo4j() {
        this(new Neo4jRequestExecutor());
    }

    public DormDaoNeo4j(RequestExecutor executor) {
        this.executor = executor;
        index = new Neo4jIndex();
        try {
            index = this.executor.post(index);
        } catch (ClientHandlerException e) {
            if (e.getCause().getClass().equals(ConnectException.class)) {
                LOG.error("The Neo4j dao can't connect with the Neo4j Database. Verify your configuration.", e);
            }
        }
    }

    public void init() {
    }


    @Override
    public Boolean push(Dependency dormDependency) {
        try {
            postDependency(dormDependency);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Neo4jDependency postDependency(Dependency dormDependency) {
        Usage usage = dormDependency.getUsage();
        Neo4jDependency dependency = null;
        try {
            dependency = new Neo4jDependency(dormDependency);
            Neo4jMetadata metadata = dependency.getMetadata();
            Neo4jMetadataExtension extension = dependency.getMetadata().getNeo4jExtension();
            if (executor.get(dependency.getIndexURI(index), List.class).isEmpty()) {
                extension.setResponse(executor.postExtension(MetadataExtensionMapper.fromExtension(extension.getExtension())));
                executor.post(metadata);
                executor.post(dependency);
                executor.post(new Neo4jRelationship(metadata, extension, Neo4jMetadataExtension.RELATIONSHIP_TYPE));
                executor.post(new Neo4jRelationship(dependency, metadata, Neo4jMetadata.RELATIONSHIP_TYPE));
                executor.post(dependency, dependency.getIndexURI(index));
            } else {
                dependency = searchNode(dependency.getIndexURI(index),
                        new TypeReference<List<Neo4jResponse<Neo4jDependency>>>() {
                        }.getType());
                fillNeo4jDependency(dependency, dormDependency.getMetadata().getExtension());
                dependency.setUsage(usage);
            }
        } catch (URISyntaxException e) {
            throw new CoreException("URI syntax error", e);
        }
        return dependency;
    }

    public <T extends Neo4jNode> T searchNode(URI uri, Type type) throws URISyntaxException {
        List<Neo4jResponse<T>> responses = executor.get(uri, type);
        Neo4jResponse<T> response = responses.get(0);
        T node = response.getData();
        node.setResponse(response);
        node.setProperties();
        return node;
    }

    public Neo4jDependency fillNeo4jDependency(Neo4jDependency dependency, DormMetadataExtension extensionPlug) throws URISyntaxException {
        Neo4jRelationship dependencyMetadata = getSingleRelationship(dependency.getResponse()
                .getOutgoing_typed_relationships(Neo4jMetadata.RELATIONSHIP_TYPE));
        Neo4jMetadata metadata = executor.getNode(dependencyMetadata.getEnd(), new TypeReference<Neo4jResponse<Neo4jMetadata>>() {
        }.getType());
        Neo4jRelationship metadataExtension = getSingleRelationship(metadata.getResponse()
                .getOutgoing_typed_relationships(Neo4jMetadataExtension.RELATIONSHIP_TYPE));
        Neo4jMetadataExtension extension = executor.getExtension(metadataExtension.getEnd(), extensionPlug);
        dependency.setMetadata(metadata);
        metadata.setExtension(extension);
        return dependency;
    }

    public Dependency getDependency(URI uri, Usage usage, DormMetadataExtension extension) throws URISyntaxException {
        Neo4jDependency dependency = executor.getNode(uri, new TypeReference<Neo4jResponse<Neo4jDependency>>() {
        }.getType());
        fillNeo4jDependency(dependency, extension);
        DormMetadata metadata = DefaultDormMetadata
                .create(dependency.getMetadata().getVersion(), dependency.getMetadata().getNeo4jExtension().getExtension());
        return DefaultDependency.create(metadata, usage);
    }

    private Neo4jRelationship getSingleRelationship(URI uri) {
        List<Neo4jRelationship> relationships = executor.get(uri, new TypeReference<List<Neo4jRelationship>>() {
        }.getType());
        return relationships.get(0);
    }

    @Override
    public DependencyNode getByMetadata(DormMetadata metadata, Usage usage) {
        DormMetadataExtension extension = metadata.getExtension();
        Map<String, DependencyNode> dependencyNodeMap = new HashMap<String, DependencyNode>();
        Neo4jDependency dependency = null;
        try {
            TypeReference<List<Neo4jResponse<Neo4jDependency>>> type = new TypeReference<List<Neo4jResponse<Neo4jDependency>>>() {
            };
            dependency = searchNode(Neo4jMetadata.generateIndexURI(metadata.getFullQualifier(), index), type.getType());
            Neo4jTraverse traverse = new Neo4jTraverse(new Neo4jRelationship(usage));
            List<Neo4jRelationship> relationships = executor.post(dependency.getTraverse(Neo4jTraverse.RELATIONSHIP_TYPE), traverse);
            DependencyNode root = DefaultDependencyNode.create(getDependency(new URI(dependency.getUri()), usage, extension));
            dependencyNodeMap.put(dependency.getUri(), root);
            putChild(usage, dependencyNodeMap, relationships, extension);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                LOG.debug("The dependency node with this " + metadata.getFullQualifier() + " full qualifier doesn't found");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change bo    dy of catch statement use File | Settings | File Templates.
        }
        return dependencyNodeMap.get(dependency.getResponse().getSelf());
    }

    public void putChild(Usage usage, Map<String, DependencyNode> dependencyNodeMap, List<Neo4jRelationship> relationships, DormMetadataExtension extension) throws URISyntaxException {

        for (Neo4jRelationship relationship : relationships) {
            DependencyNode dependencyParent = dependencyNodeMap.get(relationship.getStart());
            DependencyNode dependencyChild = dependencyNodeMap.get(relationship.getEnd());
            if (dependencyParent == null) {
                dependencyParent = DefaultDependencyNode.create(getDependency(relationship.getStart(), usage, extension));
                dependencyNodeMap.put(relationship.getStart().toString(), dependencyParent);
            }
            if (dependencyChild == null) {
                dependencyChild = DefaultDependencyNode.create(getDependency(relationship.getEnd(), usage, extension));
                dependencyNodeMap.put(relationship.getEnd().toString(), dependencyChild);
            }
            dependencyParent.addChild(dependencyChild);
        }
    }

    @Override
    public Boolean push(DependencyNode node) {

        // todo: fix this when dao is correct
        if (node.getChildren().isEmpty()) {
            return push(node.getDependency());
        }

        try {
            DependenciesNodeCollector visitor = new DependenciesNodeCollector(node.getDependency().getUsage());
            node.accept(visitor);
            Set<DependencyNode> nodes = visitor.getDependencies();
            for (DependencyNode currentNode : nodes) {
                postNodeWithChild(currentNode);
            }
            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return false;
    }

    private void postNodeWithChild(DependencyNode currentNode) throws URISyntaxException {
        Neo4jDependency dependency = postDependency(currentNode.getDependency());
        for (DependencyNode child : currentNode.getChildren()) {
            Neo4jDependency dependencyChild = postDependency(child.getDependency());
            executor.post(new Neo4jRelationship(dependency, dependencyChild, dependency.getUsage()));
        }
    }

}
