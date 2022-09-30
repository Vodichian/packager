package com.vodichian.packager.tool;

import com.vodichian.packager.Utils;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static com.vodichian.packager.Utils.getExtension;

public class Launch4jTool extends AbstractTool {
    private static final FileChooser.ExtensionFilter XML_EXT = new FileChooser.ExtensionFilter("XML Files", "*.xml");
    private static final FileChooser.ExtensionFilter ALL_EXT = new FileChooser.ExtensionFilter("All Files", "*.*");
    private static final Collection<FileChooser.ExtensionFilter> LAUNCH4J_FILTERS = Arrays.asList(XML_EXT, ALL_EXT);

    public Launch4jTool(ToolSettings settings) {
        super(settings);
    }

    @Override
    protected boolean validateConfiguration(File configurationPath) {
        Optional<String> result = Utils.getExtension(configurationPath.getName());
        if (result.isPresent()) {
            System.out.println("result = " + result.get());
            return result.get().equals("xml");
        } else {
            return false;
        }
    }

    @Override
    protected boolean validateTool(File tool) {
        Optional<String> result = getExtension(tool.getName());
        if (result.isPresent()) {
            System.out.println("result = " + result.get());
            return result.get().equals("exe");
        } else {
            return false;
        }
    }

    @Override
    void execute() {

    }

    @Override
    public Collection<FileChooser.ExtensionFilter> getFilters() {
        return LAUNCH4J_FILTERS;
    }

}
