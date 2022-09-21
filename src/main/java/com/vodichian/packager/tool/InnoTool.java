package com.vodichian.packager.tool;

import java.io.File;

public class InnoTool extends AbstractTool {
    public InnoTool() {
    }

    @Override
    protected boolean validateConfiguration(File configurationPath) {
        return false;
    }

    @Override
    protected boolean validateTool(File tool) {
        return false;
    }

    @Override
    void execute() {

    }

    @Override
    public void load(ToolSettings settings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
