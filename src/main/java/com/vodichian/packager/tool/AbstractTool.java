package com.vodichian.packager.tool;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public abstract class AbstractTool {
    private final ReadOnlyBooleanWrapper validPathToTool = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper validConfiguration = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyObjectWrapper<File> toolWrapper = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<File> configWrapper = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<ToolName> toolNameWrapper = new ReadOnlyObjectWrapper<>();

    private final ToolSettings settings;

    protected AbstractTool(ToolSettings settings) {
        this.settings = settings;
        toolNameWrapper.bind(settings.nameProperty);
        configWrapper.bind((settings.configurationProperty));
        toolWrapper.bind(settings.toolLocationProperty);
    }

    public boolean setTool(File tool) throws IOException {
        validPathToTool.set(validateTool(tool));
        settings.setToolLocation(tool);
        ToolFactory.save(settings);
        return validPathToTool.get();
    }

    public boolean setConfiguration(File configuration) throws IOException {
        settings.setConfiguration(configuration);
        validConfiguration.set(validateConfiguration(configuration));
        ToolFactory.save(settings);
        return validConfiguration.get();
    }

    public ReadOnlyObjectProperty<File> tool() {
        return toolWrapper.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<File> configuration() {
        return configWrapper.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty toolIsValid() {
        return validPathToTool.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty configIsValid() {
        return validConfiguration.getReadOnlyProperty();
    }

    protected abstract boolean validateConfiguration(File configuration);

    protected abstract boolean validateTool(File file);

    abstract void execute();

    public ToolSettings getSettings() {
        return settings;
    }

    /**
     * @return the file extension supported by this tool, returns empty list if none
     */
    public abstract Collection<FileChooser.ExtensionFilter> getFilters();
}
