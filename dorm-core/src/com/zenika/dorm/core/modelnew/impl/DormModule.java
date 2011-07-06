package com.zenika.dorm.core.modelnew.impl;

import com.zenika.dorm.core.exception.CoreException;
import com.zenika.dorm.core.modelnew.DormOrigin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lukasz Piliszczuk <lukasz.piliszczuk AT zenika.com>
 */
final public class DormModule {

    private String qualifier;
    private String version;
    private DormOrigin origin;

    private List<DormArtifact> artifacts = new ArrayList<DormArtifact>();
    private Map<DormScope, DormArtifact> artifactsByScope = new HashMap<DormScope, DormArtifact>();
    private Map<String, DormScope> scopes = new HashMap<String, DormScope>();

    public DormModule(String version, DormOrigin origin) {
        this.version = version;
        this.origin = origin;
    }

    public DormScope getScope(String identifier) {
        DormScope scope = null;

        try {
            scope = scopes.get(identifier);
        } catch (Exception e) {
            throw new CoreException("invalid scope");
        }

        if (null == scope) {
            scope = new DormScope(identifier);
            scopes.put(identifier, scope);
        }

        return scope;
    }

    public void addArtifact(DormArtifact artifact, String scope) {
        addArtifact(artifact, getScope(scope));
    }

    public void addArtifact(DormArtifact artifact, DormScope scope) {

        artifact.setModule(this);
        artifact.addScope(scope);

        if (!artifacts.contains(artifact)) {
            artifacts.add(artifact);
        }

        artifactsByScope.put(scope, artifact);
    }

    public void addArtifact(DormArtifact artifact) {
        addArtifact(artifact, DormScope.DEFAULT_SCOPE);
    }

    public String getQualifier() {

        if (null == qualifier) {
            qualifier = origin.getQualifier();
        }

        return qualifier;
    }

    public String getFullQualifier() {
        return getQualifier() + ":" + version + ":" + origin.getOrigin();
    }

    @Override
    public String toString() {
        return getFullQualifier();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public DormOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(DormOrigin origin) {
        this.origin = origin;
    }

    public List<DormArtifact> getArtifacts() {
        return artifacts;
    }

    public Map<DormScope, DormArtifact> getArtifactsByScope() {
        return artifactsByScope;
    }
}
