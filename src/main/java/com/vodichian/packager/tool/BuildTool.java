package com.vodichian.packager.tool;

import java.io.File;
import java.nio.file.Files;

public class BuildTool extends AbstractTool {
    public BuildTool(ToolSettings settings, Executor executor) {
        super(settings, executor);
    }

    /**
     * @param configuration the location of the pom.xml used to extract build information
     * @return <code>true</code> if a valid pom.xml file has been set.
     */
    @Override
    protected boolean validateConfiguration(File configuration) {
        if (configuration == null) {
            post("Path to pom.xml was null");
            return false;
        } else if (Files.notExists(configuration.toPath())) {
            post(configuration.getAbsolutePath() + " does not exist");
            return false;
        } else if (!configuration.getName().equals("pom.xml")) {
            post(configuration.getAbsolutePath() + " is not a pom.xml file");
            return false;
        } else {
            post("pom.xml is valid");
            return true;
        }
    }

    /**
     * {@link BuildTool} is custom code and does not use an external via and therefore always returns true.
     *
     * @param file ignored
     * @return always returns <code>true</code>
     */
    @Override
    protected boolean validateTool(File file) {
        return true;
    }

}
