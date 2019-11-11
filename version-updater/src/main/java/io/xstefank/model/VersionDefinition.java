package io.xstefank.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class VersionDefinition {
    
    private String property;
    private String version;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
