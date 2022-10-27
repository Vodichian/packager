package com.vodichian.packager.tool;

import javafx.beans.property.*;

import java.io.File;

/**
 * Simple data object containing the settings required to build an {@link AbstractTool}
 * <a href="joplin://x-callback-url/openNote?id=b73e27ffe7f347edaf05848aa109a6e9">Use Cases</a>
 */
public class ToolSettings {

    public final ObjectProperty<ToolName> nameProperty = new SimpleObjectProperty<>();
    public final ObjectProperty<File> toolLocationProperty = new SimpleObjectProperty<>();
    public final ObjectProperty<File> configurationProperty = new SimpleObjectProperty<>();
    public final IntegerProperty priorityProperty = new SimpleIntegerProperty(0);
    public final BooleanProperty enabledProperty = new SimpleBooleanProperty(true);

    public ToolSettings() {

    }

    public ToolSettings(
            final ToolName name,
            final File toolLocation,
            final File configuration,
            final int priority,
            final boolean enabled) {
        this.nameProperty.set(name);
        this.toolLocationProperty.set(toolLocation);
        this.configurationProperty.set(configuration);
        this.priorityProperty.set(priority);
        this.enabledProperty.set(enabled);
    }

    public ToolName getName() {
        return nameProperty.get();
    }

    public ToolSettings setName(final ToolName name) {
        nameProperty.set(name);
        return this;
    }

    public File getToolLocation() {
        return toolLocationProperty.get();
    }

    public ToolSettings setToolLocation(final File toolLocation) {
        toolLocationProperty.set(toolLocation);
        return this;
    }

    public File getConfiguration() {
        return configurationProperty.get();
    }

    public ToolSettings setConfiguration(final File configuration) {
        configurationProperty.set(configuration);
        return this;
    }

    public ToolSettings setPriority(int priority) {
        priorityProperty.set(priority);
        return this;
    }

    public int getPriority() {
        return priorityProperty.get();
    }

    public ToolSettings setEnabled(boolean enabled) {
        enabledProperty.set(enabled);
        return this;
    }

    public boolean getEnabled() {
        return enabledProperty.get();
    }
}
