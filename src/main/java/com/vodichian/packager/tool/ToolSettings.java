package com.vodichian.packager.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Simple data object containing the settings required to build an {@link AbstractTool}
 * <a href="joplin://x-callback-url/openNote?id=b73e27ffe7f347edaf05848aa109a6e9">Use Cases</a>
 */
public class ToolSettings {

    private static final String NAME_TAG = "name : ";
    private static final String TOOL_TAG = "tool : ";
    private static final String CONFIG_TAG = "config : ";
    private ToolName name;
    private File toolLocation;
    private File configuration;

    public ToolSettings() {

    }

    public ToolSettings(final ToolName name, final File toolLocation, final File configuration) {
        this.name = name;
        this.toolLocation = toolLocation;
        this.configuration = configuration;
    }

    public void save(final File file) throws IOException {
        final PrintWriter writer = new PrintWriter(new FileWriter(file));
        writer.println("# ToolSettings");
        writer.printf(NAME_TAG + "%s\n", name);
        writer.printf(TOOL_TAG + "%s\n", toolLocation.getAbsolutePath());
        writer.printf(CONFIG_TAG + "%s", configuration.getAbsolutePath());
        writer.close();
    }

    public void load(final File file) throws IOException {
        final Path path = file.toPath();
        final Stream<String> lines = Files.lines(path);
        lines.forEach(line -> {
            if (line.contains(TOOL_TAG)) {
                final String pathToTool = line.substring(TOOL_TAG.length());
                toolLocation = new File(pathToTool);
            } else if (line.contains(CONFIG_TAG)) {
                final String pathToConfig = line.substring(CONFIG_TAG.length());
                configuration = new File(pathToConfig);
            } else if (line.contains(NAME_TAG)) {
                final String nameValue = line.substring(NAME_TAG.length());
                this.name = ToolName.valueOf(nameValue);
            }
        });
        lines.close();
    }

    public ToolName getName() {
        return name;
    }

    public ToolSettings setName(final ToolName name) {
        this.name = name;
        return this;
    }

    public File getToolLocation() {
        return toolLocation;
    }

    public ToolSettings setToolLocation(final File toolLocation) {
        this.toolLocation = toolLocation;
        return this;
    }

    public File getConfiguration() {
        return configuration;
    }

    public ToolSettings setConfiguration(final File configuration) {
        this.configuration = configuration;
        return this;
    }
}
