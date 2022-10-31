package com.vodichian.packager.projects;

import com.vodichian.packager.tool.AbstractTool;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Project {
    private String name;
    private LocalDateTime lastAccessed;
    private final Set<AbstractTool> toolSet;

    public Project() {
        toolSet = new HashSet<>();
    }

    public Project(String name) {
        this();
        this.name = name;
        this.lastAccessed = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(LocalDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Collection<AbstractTool> getTools() {
        return Collections.unmodifiableCollection(toolSet);
    }

    public void addAll(Collection<AbstractTool> tools) {

        toolSet.addAll(tools);
    }

    public void setTools(Collection<AbstractTool> tools) {
        toolSet.clear();
        toolSet.addAll(tools);
    }

    public Project add(AbstractTool tool) {
        toolSet.add(tool);
        return this;
    }
}
