package com.vodichian.packager.tool;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

public class BuildTool extends AbstractTool {
    public BuildTool(ToolSettings settings) {
        super(settings);
    }

    @Override
    protected boolean validateConfiguration(File configuration) {
        return false;
    }

    @Override
    protected boolean validateTool(File file) {
        return false;
    }

    @Override
    void execute() {

    }

    @Override
    public Collection<FileChooser.ExtensionFilter> getFilters() {
        return Collections.emptyList();
    }
}
