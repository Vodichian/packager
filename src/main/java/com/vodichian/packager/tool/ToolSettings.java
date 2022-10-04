package com.vodichian.packager.tool;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

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

    public ObjectProperty<ToolName> nameProperty = new SimpleObjectProperty<>();
    public ObjectProperty<File> toolLocationProperty = new SimpleObjectProperty<>();
    public ObjectProperty<File> configurationProperty = new SimpleObjectProperty<>();

    public ToolSettings() {

    }

    public ToolSettings(final ToolName name, final File toolLocation, final File configuration) {
        this.nameProperty = new SimpleObjectProperty<>(name);
        this.toolLocationProperty = new SimpleObjectProperty<>(toolLocation);
        this.configurationProperty = new SimpleObjectProperty<>(configuration);
    }

    public void save(final File file) throws IOException {
        final PrintWriter writer = new PrintWriter(new FileWriter(file));
        writer.println("# ToolSettings");
        writer.printf(NAME_TAG + "%s\n", nameProperty.get());
        writer.printf(TOOL_TAG + "%s\n", (toolLocationProperty.get() == null) ? "" : toolLocationProperty.get().getAbsolutePath());
        writer.printf(CONFIG_TAG + "%s", (configurationProperty.get() == null) ? "" : configurationProperty.get().getAbsolutePath());
        writer.close();
    }

    public void load(final File file) throws IOException {
        final Path path = file.toPath();
        final Stream<String> lines = Files.lines(path);
        lines.forEach(line -> {
            if (line.contains(TOOL_TAG)) {
                final String pathToTool = line.substring(TOOL_TAG.length());
                toolLocationProperty.set(new File(pathToTool));
            } else if (line.contains(CONFIG_TAG)) {
                final String pathToConfig = line.substring(CONFIG_TAG.length());
                configurationProperty.set(new File(pathToConfig));
            } else if (line.contains(NAME_TAG)) {
                ToolName extracted = ToolName.valueOf(line.substring(NAME_TAG.length()));
                nameProperty.set(extracted);
            }
        });
        lines.close();
    }

    public ToolName getName() {
        return nameProperty.get();
    }

    public ToolSettings setName(final ToolName name) {
        this.nameProperty.set(name);
        return this;
    }

    public File getToolLocation() {
        return toolLocationProperty.get();
    }

    public ToolSettings setToolLocation(final File toolLocation) {
        this.toolLocationProperty.set(toolLocation);
        return this;
    }

    public File getConfiguration() {
        return configurationProperty.get();
    }

    public ToolSettings setConfiguration(final File configuration) {
        this.configurationProperty.set(configuration);
        return this;
    }
}
