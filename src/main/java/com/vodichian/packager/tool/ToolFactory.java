package com.vodichian.packager.tool;

import com.vodichian.packager.App;
import com.vodichian.packager.PackagerException;
import com.vodichian.packager.ToolController;
import com.vodichian.packager.projects.Project;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates {@link AbstractTool} instances using provided settings
 */
public class ToolFactory {
    private static final FileChooser.ExtensionFilter ISS_EXT = new FileChooser.ExtensionFilter("Inno Config Files", "*.iss");
    private static final FileChooser.ExtensionFilter ALL_EXT = new FileChooser.ExtensionFilter("All Files", "*.*");
    private static final Collection<FileChooser.ExtensionFilter> INNO_FILTERS = Arrays.asList(ISS_EXT, ALL_EXT);
    private static final FileChooser.ExtensionFilter XML_EXT = new FileChooser.ExtensionFilter("XML Files", "*.xml");
    private static final Collection<FileChooser.ExtensionFilter> XML_ALL_FILTERS = Arrays.asList(XML_EXT, ALL_EXT);

    private static Parent loadFXML(AbstractTool tool) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("tool.fxml"));
        Parent parent = fxmlLoader.load();
        ToolController controller = fxmlLoader.getController();
        controller.setTool(tool);
        return parent;
    }

    public static List<Parent> toolViews(Project project) {
        return project.getTools().stream()
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
