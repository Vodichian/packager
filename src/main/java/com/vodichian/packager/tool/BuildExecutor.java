package com.vodichian.packager.tool;

import com.vodichian.packager.PackagerException;
import javafx.beans.property.ObjectProperty;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

public class BuildExecutor implements Executor {
    @Override
    public void execute(ToolSettings settings, ObjectProperty<ToolState> toolState) {
        // Update version in Launch4j configuration
        Optional<AbstractTool> optional = ToolFactory.getTool(ToolName.LAUNCH_4_J);
        if (optional.isPresent()) {
            Launch4jTool launchTool = (Launch4jTool) optional.get();
            execute(settings, toolState, launchTool.getSettings());
        } else {
            post("Launch4j tool not found");
            toolState.set(ToolState.FAILURE);
        }

    }

    protected void execute(ToolSettings buildSettings, ObjectProperty<ToolState> toolState, ToolSettings launchSettings) {
        toolState.set(ToolState.RUNNING);
        try {
            // Get Version
            String version = getVersion(buildSettings);
            post("Found version: " + version);

            // Update version in Launch4j configuration

            if (launchSettings == null) {
                post("Launch4j settings not found");
                toolState.set(ToolState.FAILURE);
                return;
            }

            boolean success = updateLaunch4j(version, launchSettings);
            post("Updating Launch4j... " + success);
            if (!success) {
                post("Failed to update Launch4j's config file");
                toolState.set(ToolState.FAILURE);
                return;
            }

            // Update version in Inno configuration
            success = updateInno(version);
            post("Updated Inno Setup... " + success);
            if (!success) {
                toolState.set(ToolState.FAILURE);
            } else {
                toolState.set(ToolState.SUCCESS);
            }
        } catch (PackagerException e) {
            post("Failed to update version: " + e.getMessage());
            toolState.set(ToolState.FAILURE);
        }
    }

    private void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }

    private boolean updateInno(String version) {
        return false; // TODO: 10/7/2022 implement this method
    }

    /**
     * Updates version info in the Launch4j configuration file. This is a bit tricky as Launch4j has two version
     * locations:
     * <pre>
     * {@code <fileVersion>x.x.x.x</fileVersion> version string needs to be converted to 4 numbers}
     * {@code <txtFileVersion>some string</txtFileVersion> version string can be pasted directly}
     * {@code <productVersion>x.x.x.x</fileVersion> version string needs to be converted to 4 numbers}
     * {@code <txtProductVersion>some string</txtFileVersion> version string can be pasted directly}
     * </pre>
     *
     * @param version the version string
     */
    private boolean updateLaunch4j(String version, ToolSettings launchSettings) {
        String fileVersion = convertToFileVersion(version); // converts to "x.x.x.x"

        if (launchSettings == null) {
            post("launchSettings were null but are required");
            return false;
        }

        File launch4jConfig = launchSettings.getConfiguration();

        // I have the file, now modify version line
        int fileVersionIndex = 0;
        int txtFileVersionIndex = 0;
        int productVersionIndex = 0;
        int txtProductVersionIndex = 0;
        List<String> lines;
        try {
            lines = Files.readAllLines(launch4jConfig.toPath());
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (fileVersionIndex == 0 && line.contains("<fileVersion>")) {
                    fileVersionIndex = i;
                } else if (txtFileVersionIndex == 0 && line.contains("<txtFileVersion>")) {
                    txtFileVersionIndex = i;
                } else if (productVersionIndex == 0 && line.contains("<productVersion>")) {
                    productVersionIndex = i;
                } else if (txtProductVersionIndex == 0 && line.contains("<txtProductVersion>")) {
                    txtProductVersionIndex = i;
                } else {
                    if (fileVersionIndex > 0 && txtFileVersionIndex > 0 && productVersionIndex > 0 && txtProductVersionIndex > 0) {
                        break; // all indices have been found
                    }
                }
            } // for

        } catch (IOException e) {
            post("Failure parsing pom.xml: " + e.getMessage());
            return false;
        }

        lines.set(fileVersionIndex, replaceContent(lines.get(fileVersionIndex), fileVersion));
        lines.set(txtFileVersionIndex, replaceContent(lines.get(txtFileVersionIndex), version));
        lines.set(productVersionIndex, replaceContent(lines.get(productVersionIndex), fileVersion));
        lines.set(txtProductVersionIndex, replaceContent(lines.get(txtProductVersionIndex), version));

        // make a backup
        try {
            Files.copy(launch4jConfig.toPath(),
                    new File(launch4jConfig.getAbsolutePath() + ".bak").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            post("Failed to create backup: " + e.getMessage());
            return false;
        }
        // write to file
        try {
            Files.write(launch4jConfig.toPath(), lines);
        } catch (IOException e) {
            post("Failed to save configuration: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Replaces the existing content between the tag with <code>content</code> and returns the resulting String.
     *
     * @param line    the line of text to modify
     * @param content the content to write
     * @return the resulting String
     */
    private String replaceContent(String line, String content) {
        int startIndex = line.indexOf(">") + 1;
        int endIndex = line.indexOf("<", startIndex);
        return line.substring(0, startIndex) + content + line.substring(endIndex);
    }

    /**
     * This method is limited in scope, working <bold>only</bold> on version strings supported the common Maven format
     * of "1.2.3" or "1.2.3-SNAPSHOT"
     *
     * @param version the original version String
     * @return a {@link String} adjusted to conform to Launch4j's versioning format
     */
    private String convertToFileVersion(String version) {
        if (version == null || version.isBlank()) {
            return "0.0.0.0";
        } else if (version.contains("-SNAPSHOT")) {
            int index = version.indexOf("-SNAPSHOT");
            version = version.substring(0, index);
        }
        String[] versionArray = version.split("\\.");
        int missing = 4 - versionArray.length;
        return version + ".0".repeat(Math.max(0, missing));
    }

    /**
     * Extract the version information from the pom.xml file provided in {@link ToolSettings#getConfiguration()}
     *
     * @param buildSettings the {@link ToolSettings} to extact the version info from
     * @return the extracted version information
     * @throws PackagerException if file can't be opened or an XML error occurs during parse
     */
    private String getVersion(ToolSettings buildSettings) throws PackagerException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            Model model = reader.read(new FileReader(buildSettings.getConfiguration()));
            return model.getVersion();
        } catch (IOException | XmlPullParserException e) {
            throw new PackagerException(e);
        }
    }

}