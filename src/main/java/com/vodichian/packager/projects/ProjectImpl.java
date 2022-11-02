package com.vodichian.packager.projects;

import com.vodichian.packager.tool.AbstractTool;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class ProjectImpl implements Project {
    private String name;
    private LocalDateTime lastAccessed;
    private final Set<AbstractTool> toolSet;

    public ProjectImpl() {
        toolSet = new HashSet<>();
        this.lastAccessed = LocalDateTime.now();
    }

    public ProjectImpl(String name) {
        this();
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    @Override
    public void setLastAccessed(LocalDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    @Override
    public Collection<AbstractTool> getTools() {
        return Collections.unmodifiableCollection(toolSet);
    }

    @Override
    public void addAll(Collection<AbstractTool> tools) {

        toolSet.addAll(tools);
    }

    @Override
    public void setTools(Collection<AbstractTool> tools) {
        toolSet.clear();
        toolSet.addAll(tools);
    }

    @Override
    public Project add(AbstractTool tool) {
        toolSet.add(tool);
        return this;
    }
}
