package com.vodichian.packager.projects;

import com.vodichian.packager.tool.AbstractTool;

import java.time.LocalDateTime;
import java.util.Collection;

public interface Project {
    String getName();

    void setName(String name);

    LocalDateTime getLastAccessed();

    void setLastAccessed(LocalDateTime lastAccessed);

    Collection<AbstractTool> getTools();

    void addAll(Collection<AbstractTool> tools);

    void setTools(Collection<AbstractTool> tools);

    Project add(AbstractTool tool);
}
