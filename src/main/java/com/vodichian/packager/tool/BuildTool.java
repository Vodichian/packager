package com.vodichian.packager.tool;

import java.io.File;

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
}
