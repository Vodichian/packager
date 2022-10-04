package com.vodichian.packager.tool;

import com.vodichian.packager.App;
import com.vodichian.packager.PackagerException;
import com.vodichian.packager.ToolController;
import com.vodichian.packager.Utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public static void save(ToolSettings settings) throws IOException {
        String filename = TOOL_DIRECTORY + "/" + settings.getName() + "." + SETTING_EXTENSION;
        File file = new File(filename);
        settings.save(file);
        System.out.println("Settings saved to " + file.getAbsolutePath());
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
                save(settings);
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
                    .filter(path -> !Files.isDirectory(path)) // is not a directory
                    .filter(path -> Utils.getExtension(path.toFile().getName()) // is a settings file
                            .orElse("none").equals(SETTING_EXTENSION))
                    .map(path -> {
                        try {
                            ToolSettings toolSettings = new ToolSettings();
                            toolSettings.load(path.toFile());
                            return toolSettings;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());
        }
    }

    private static Path getToolDirectory() throws IOException {
        Path toolDir = Paths.get(TOOL_DIRECTORY);
        if (Files.notExists(toolDir)) {
            Files.createDirectory(toolDir);
        }
        return toolDir;
    }

    private static Parent loadFXML(AbstractTool tool) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("tool.fxml"));
        Parent parent = fxmlLoader.load();
        ToolController controller = fxmlLoader.getController();
        controller.setTool(tool);
        return parent;
    }

    public static List<Parent> toolViews() throws PackagerException, IOException {
        return tools().stream().map(tool -> {
            try {
                return loadFXML(tool);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
