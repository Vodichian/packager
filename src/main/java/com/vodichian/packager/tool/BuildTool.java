package com.vodichian.packager.tool;


import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class BuildTool extends AbstractTool {
    public BuildTool(ToolSettings settings) {
        super(settings);
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

    @Override
    public void execute() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {
            model = reader.read(new FileReader(getSettings().getConfiguration()));
            post(model.getId());
            post(model.getGroupId());
            post(model.getArtifactId());
            post(model.getVersion());
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }
}
