package com.vodichian.packager.tool;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.File;
import java.util.Optional;

public abstract class AbstractTool {
    private final ReadOnlyBooleanWrapper validPathToTool = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper validConfiguration = new ReadOnlyBooleanWrapper(false);
    private ReadOnlyObjectWrapper<File> toolWrapper = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<File> configWrapper = new ReadOnlyObjectWrapper<>();

    protected AbstractTool() {
    }

    public boolean setTool(File tool) {
        validPathToTool.set(validateTool(tool));
        toolWrapper.set(tool);
        return validPathToTool.get();
    }

    public boolean setConfiguration(File configuration) {
        configWrapper.set(configuration);
        validConfiguration.set(validateConfiguration(configuration));
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

    protected Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

}
