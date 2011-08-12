package com.zenika.dorm.core.model.impl;

import com.zenika.dorm.core.exception.CoreException;
import com.zenika.dorm.core.model.Dependency;
import com.zenika.dorm.core.model.DormResource;
import com.zenika.dorm.core.model.DormMetadata;

/**
 * Immutable dorm dependency
 *
 * @author Lukasz Piliszczuk <lukasz.piliszczuk AT zenika.com>
 */
public final class DefaultDependency implements Dependency {

    /**
     * Always required
     */
    private final Usage usage;
    private final DormMetadata metadata;

    /**
     * Optionnal, may be null
     */
    private final DormResource resource;

    public static DefaultDependency create(DormMetadata metadata) {
        return new DefaultDependency(metadata, Usage.create(), null);
    }

    public static DefaultDependency create(DormMetadata metadata, DormResource resource) {
        return new DefaultDependency(metadata, Usage.create(), resource);
    }

    public static DefaultDependency create(DormMetadata metadata, Usage usage) {
        return new DefaultDependency(metadata, usage, null);
    }

    public static DefaultDependency create(DormMetadata metadata, Usage usage, DormResource resource) {
        return new DefaultDependency(metadata, usage, resource);
    }

    private DefaultDependency(DormMetadata metadata, Usage usage, DormResource resource) {

        if (null == metadata || null == usage) {
            throw new CoreException("Metadata and usage are required.");
        }

        this.metadata = metadata;
        this.usage = usage;
        this.resource = resource;
    }

    @Override
    public Usage getUsage() {
        return usage;
    }

    @Override
    public DormMetadata getMetadata() {
        return metadata;
    }

    @Override
    public DormResource getResource() {
        return resource;
    }

    @Override
    public Boolean hasFile() {
        return null != resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultDependency)) return false;

        DefaultDependency that = (DefaultDependency) o;

        if (resource != null ? !resource.equals(that.resource) : that.resource != null) return false;
        if (metadata != null ? !metadata.equals(that.metadata) : that.metadata != null) return false;
        if (usage != null ? !usage.equals(that.usage) : that.usage != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = usage != null ? usage.hashCode() : 0;
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String fileAsString = (resource == null) ? "null" : resource.toString();
        return "Dependency { " +
                "Usage = " + usage + "; " +
                "Metadata = " + metadata + "; " +
                "File = " + fileAsString + " }";
    }
}