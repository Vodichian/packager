package com.vodichian.packager.tool;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class BuildExecutorTest {
    private final static Random RANDOM = new Random();

    @Test
    public void testExecute() throws IOException {
        BuildExecutor executor = new BuildExecutor();
        File pomXMl = new File("src/test/resources/pom.xml"); // in test/resources
        ToolSettings buildSettings = new ToolSettings()
                .setName(ToolName.BUILD_EXTRACTOR)
                .setConfiguration(pomXMl);

        File launch4jConfig = new File("src/test/resources/guildmaster-launch4j.xml"); // in test/resources
        ToolSettings launchSettings = new ToolSettings()
                .setName(ToolName.LAUNCH_4_J)
                .setConfiguration(launch4jConfig);

        File innoConfig = new File("src/test/resources/inno-build.iss"); // in test/resources
        ToolSettings innoSettings = new ToolSettings()
                .setName(ToolName.INNO_SETUP)
                .setConfiguration(innoConfig);

        ObjectProperty<ToolState> toolState = new SimpleObjectProperty<>();

        // Change pom.xml to have random version
        String randomVersion = makeRandomVersion();
        updatePom(randomVersion, pomXMl);

        executor.execute(buildSettings, toolState, launchSettings, innoSettings);

        boolean shouldBeTrue = verifyLaunch(randomVersion, launch4jConfig);
        assertTrue(shouldBeTrue);
        assertEquals(toolState.get(), ToolState.SUCCESS);
    }

    private boolean verifyLaunch(String randomVersion, File launchConfig) throws IOException {
        List<String> lines = Files.readAllLines(launchConfig.toPath());
        return lines.get(31).contains(randomVersion) && lines.get(35).contains(randomVersion);
    }

    private void updatePom(String randomVersion, File pomXML) throws IOException {
        List<String> lines = Files.readAllLines(pomXML.toPath());
        // pom.xml is a test version, so can rely on fixed location for version tag
        String versionLine = lines.get(16);
        String updatedLine = replaceContent(versionLine, randomVersion);
        lines.set(16, updatedLine);
        Files.write(pomXML.toPath(), lines);
    }

    private String replaceContent(String line, String content) {
        int startIndex = line.indexOf(">") + 1;
        int endIndex = line.indexOf("<", startIndex);
        return line.substring(0, startIndex) + content + line.substring(endIndex);
    }


    private String makeRandomVersion() {
        List<String> possibleValues = new ArrayList<>();
        possibleValues.add("1.1.1.1");
        possibleValues.add("1.2.3.4");
        possibleValues.add("2.1");
        possibleValues.add("3.1.4-SNAPSHOT");
        possibleValues.add("1-SNAPSHOT");
        return possibleValues.get(RANDOM.nextInt(possibleValues.size()));
    }

//    @Test
//    public void testConvertToFileVersion() {
//        BuildExecutor executor = new BuildExecutor();
//        String result = executor.convertToFileVersion("1.1.1.1");
//        assertEquals(result, "1.1.1.1");
//
//        result = executor.convertToFileVersion("1.1-SNAPSHOT");
//        assertEquals(result, "1.1.0.0");
//
//        result = executor.convertToFileVersion("1.1.0");
//        assertEquals(result, "1.1.0.0");
//
//        result = executor.convertToFileVersion("1.1.1");
//        assertEquals(result, "1.1.1.0");
//    }

//    @Test
//    public void testReplaceContent() {
//        BuildExecutor executor = new BuildExecutor();
//        String line = "    <fileVersion>2.9.5.1</fileVersion>\n";
//        String result = executor.replaceContent(line, "Something else");
//        System.out.println("Result was: " + result);
//        assertEquals(result, "    <fileVersion>Something else</fileVersion>\n");
//    }

//    @Test
//    public void testUpdateLaunch4j() throws PackagerException, IOException {
//        File launch4jConfig = new File("src/test/resources/guildmaster-launch4j.xml"); // in test/resources
//        File pomXMl = new File("src/test/resources/pom.xml"); // in test/resources
//
//        ToolSettings launchSettings = new ToolSettings()
//                .setName(ToolName.LAUNCH_4_J)
//                .setConfiguration(launch4jConfig);
//        AbstractTool launchTool = ToolFactory.make(launchSettings);
//        BuildExecutor executor = new BuildExecutor();
//        executor.updateLaunch4j("1.2.2-SNAPSHOT", launchTool);
//
//        // Load original file into lines list
//        List<String> lines = Files.readAllLines(launch4jConfig.toPath());
//        // Extract version from known indices
//        String fileVersion = lines.get(30); // <fileVersion>
//        String originalVersion = findVersion(fileVersion);
//        // run updateLaunch4j
//        // Load updated file into updated lines list
//        // compare lists are identical except for known updated indices
//        // assert version have been updated correctly
//    }

}