package com.vodichian.packager.tool;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

public class InnoTool extends AbstractTool {
    private static final FileChooser.ExtensionFilter ISS_EXT = new FileChooser.ExtensionFilter("Inno Config Files", "*.iss");
    private static final FileChooser.ExtensionFilter ALL_EXT = new FileChooser.ExtensionFilter("All Files", "*.*");
    private static final Collection<FileChooser.ExtensionFilter> INNO_FILTERS = Arrays.asList(ISS_EXT, ALL_EXT);

    public InnoTool(ToolSettings settings) {
        super(settings);
    }

    @Override
    protected boolean validateConfiguration(File configurationPath) {
        return false;
    }

    @Override
    protected boolean validateTool(File tool) {
        return false;
    }

    @Override
    void execute() {

    }

    @Override
    public Collection<FileChooser.ExtensionFilter> getFilters() {
        return INNO_FILTERS;
    }
}
