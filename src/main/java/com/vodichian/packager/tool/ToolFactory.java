package com.vodichian.packager.tool;

import com.vodichian.packager.App;
import com.vodichian.packager.PackagerException;
import com.vodichian.packager.ToolController;
import com.vodichian.packager.Utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creates {@link AbstractTool} instances using provided settings
 */
public class ToolFactory {
    private static final FileChooser.ExtensionFilter ISS_EXT = new FileChooser.ExtensionFilter("Inno Config Files", "*.iss");
    private static final FileChooser.ExtensionFilter ALL_EXT = new FileChooser.ExtensionFilter("All Files", "*.*");
    private static final Collection<FileChooser.ExtensionFilter> INNO_FILTERS = Arrays.asList(ISS_EXT, ALL_EXT);
    private static final FileChooser.ExtensionFilter XML_EXT = new FileChooser.ExtensionFilter("XML Files", "*.xml");
    private static final Collection<FileChooser.ExtensionFilter> XML_ALL_FILTERS = Arrays.asList(XML_EXT, ALL_EXT);
    public static final String NAME = "ToolFactory";

    private final static List<AbstractTool> toolList = new ArrayList<>();


    public static final String TOOL_DIRECTORY = "tools";
    public static final String SETTING_EXTENSION = "tul";

    /**
     * Resets this {@link ToolFactory} to its initial state.
     *
     * @apiNote intended for testing purposes.
     */
    public static void reset() {
        toolList.clear();
    }

    public static Optional<AbstractTool> getTool(ToolName name) {
        return toolList.stream()
                .filter(tool -> tool.getSettings().getName().equals(name)).findAny();
    }

    public static AbstractTool make(ToolSettings settings) throws PackagerException {
        AbstractTool tool;
        switch (settings.getName()) {
            case LAUNCH_4_J:
                tool = new Launch4jTool(settings, new LaunchExecutor());
                break;
            case INNO_SETUP:
                tool = new InnoTool(settings, new InnoExecutor());
                break;
            case BUILD_EXTRACTOR:
                tool = new BuildTool(settings, new BuildExecutor());
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
        EventBus.getDefault().post(new ToolMessage(NAME, "Settings saved to " + file.getAbsolutePath()));
    }

    public static Collection<AbstractTool> tools() throws IOException, PackagerException {
        if (toolList.isEmpty()) {
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

            for (ToolSettings toolSettings : allSettings) {
                toolList.add(make(toolSettings));
            }
        }
        return Collections.unmodifiableCollection(toolList);
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
        return tools().stream()
                // reverse order sorting by flipping o2 and o1
                .sorted((o1, o2) -> Integer.compare(o2.getSettings().getPriority(), o1.getSettings().getPriority()))
                .map(tool -> {
                    try {
                        return loadFXML(tool);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public static Collection<FileChooser.ExtensionFilter> getFilters(ToolSettings settings) throws PackagerException {
        if (settings.getName() == null) {
            throw new PackagerException("ToolSettings name has not been set");
        }
        switch (settings.getName()) {
            case LAUNCH_4_J:
            case BUILD_EXTRACTOR:
                return XML_ALL_FILTERS;
            case INNO_SETUP:
                return INNO_FILTERS;
            default: {
                throw new PackagerException("File filters not supported for " + settings.getName());
            }
        }
    }
}
