package com.vodichian.packager.tool;

import com.vodichian.packager.PackagerException;
import com.vodichian.packager.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creates {@link AbstractTool} instances using provided settings
 */
public class ToolFactory {
    public static final String TOOL_DIRECTORY = "tools";
    public static final String SETTING_EXTENSION = "tul";

    public static AbstractTool make(ToolSettings settings) throws PackagerException {
        AbstractTool tool;
        switch (settings.getName()) {
            case LAUNCH_4_J:
                tool = new Launch4jTool(settings);
                break;
            case INNO_SETUP:
                tool = new InnoTool(settings);
                break;
            case BUILD_EXTRACTOR:
                tool = new BuildTool(settings);
                break;
            default: {
                throw new PackagerException("Unsupported tool: " + settings.getName());
            }
        }
        return tool;
    }

    public static Collection<AbstractTool> tools() throws IOException, PackagerException {
        // Look in tool directory for ToolSettings files for settings
        Path toolDir = getToolDirectory();
        // Build tools from settings
        Collection<ToolSettings> allSettings = getToolSettings(toolDir);
        // Verify all tool settings exist, create defaults for any missing
        for (ToolName toolName : ToolName.values()) {
            boolean noneMatch = allSettings.stream()
                    .noneMatch(settings -> settings.getName().equals(toolName));
            if (noneMatch) {
                ToolSettings settings = new ToolSettings().setName(toolName);
                String filename = TOOL_DIRECTORY + "/" + toolName + "." + SETTING_EXTENSION;
                settings.save(new File(filename));
                allSettings.add(settings);
            }
        }

        ArrayList<AbstractTool> tools = new ArrayList<>(allSettings.size());
        for (ToolSettings toolSettings : allSettings) {
            tools.add(make(toolSettings));
        }
        return tools;
    }

    private static Collection<ToolSettings> getToolSettings(Path toolDir) throws IOException {
        try (Stream<Path> settings = Files.list(toolDir)) {
            return settings
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> Utils.getExtension(path.toFile().getName())
                            .orElse("none").equals(SETTING_EXTENSION))
                    .map(path -> new ToolSettings().setToolLocation(path.toFile())).collect(Collectors.toList());
        }
    }

    private static Path getToolDirectory() throws IOException {
        Path toolDir = Paths.get(TOOL_DIRECTORY);
        if (Files.notExists(toolDir)) {
            Files.createDirectory(toolDir);
        }
        return toolDir;
    }
}
