package com.vodichian.packager.tool;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.File;

public abstract class AbstractTool {
    private final ReadOnlyBooleanWrapper validPathToTool = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper validConfiguration = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyObjectWrapper<File> toolWrapper = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<File> configWrapper = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<ToolName> toolNameWrapper = new ReadOnlyObjectWrapper<>();
    protected final ReadOnlyObjectWrapper<ToolState> toolStateWrapper = new ReadOnlyObjectWrapper<>(ToolState.CONFIG_ERROR);

    private final ToolSettings settings;

    protected AbstractTool(ToolSettings settings) {
        this.settings = settings;
        toolNameWrapper.bind(settings.nameProperty);
        configWrapper.bind((settings.configurationProperty));
        toolWrapper.bind(settings.toolLocationProperty);

        toolWrapper.addListener(observable -> validPathToTool.set(validateTool(toolWrapper.get())));
        validPathToTool.set(validateTool(toolWrapper.get()));
        configWrapper.addListener((observable -> validConfiguration.set(validateConfiguration(configWrapper.get()))));
        validConfiguration.set(validateConfiguration(configWrapper.get()));

    }

    public ReadOnlyObjectProperty<File> tool() {
        return toolWrapper.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<ToolName> name() {
        return toolNameWrapper.getReadOnlyProperty();
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

    /**
     * Used to monitor the current state of this tool.
     *
     * @return a {@link ToolState} property
     */
    public ReadOnlyObjectProperty<ToolState> state() {
        return toolStateWrapper.getReadOnlyProperty();
    }

    protected abstract boolean validateConfiguration(File configuration);

    protected abstract boolean validateTool(File file);

    public abstract void execute();

    public ToolSettings getSettings() {
        return settings;
    }

}
