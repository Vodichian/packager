package com.vodichian.packager.tool;

import com.vodichian.packager.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static com.vodichian.packager.Utils.getExtension;

public class InnoTool extends AbstractTool {

    public InnoTool(ToolSettings settings, Executor executor) {
        super(settings, executor);
    }

    @Override
    protected boolean validateConfiguration(File configuration) {
        if (configuration == null) {
            post("Configuration path was null");
            return false;
        } else if (Files.notExists(configuration.toPath())) {
            post(configuration.getAbsolutePath() + " does not exist");
            return false;
        }
        Optional<String> result = Utils.getExtension(configuration.getName());
        return result.map(s -> s.equals("iss")).orElse(false);
    }

    @Override
    protected boolean validateTool(File tool) {
        if (tool == null) {
            post("Tool path was null");
            return false;
        } else if (Files.notExists(tool.toPath())) {
            post(tool.getAbsolutePath() + " does not exist");
            return false;
        }
        Optional<String> result = getExtension(tool.getName());
        boolean isValid = result.map(s -> s.equals("exe")).orElse(false);
        post("Tool is valid: " + isValid);
        return isValid;
    }

    @Override
    public void openOutputDir() {
        post("Opening output directory...");
        File configFile = configuration().get();
        // parse file for output directory location
        getOutputLoc(configFile).ifPresent(file -> {
            // open this location
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(file);
            } catch (IOException e) {
                post("Failed to open output location: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    private Optional<File> getOutputLoc(File configFile) {
        List<String> lines;
        try {
            lines = Files.readAllLines(configFile.toPath());
        } catch (IOException e) {
            post("Failed to read Inno configuration: " + configFile.getAbsolutePath());
            return Optional.empty();
        }
        // identify line with OutputDir=
        for (String line : lines) {
            if (line.contains("OutputDir=")) {
                int index = line.indexOf("=") + 1;
                String pathString = line.substring(index);
                File file = new File(pathString);
                if (file.exists()) {
                    return Optional.of(file);
                } else {
                    post("The location was not found: " + pathString);
                    return Optional.empty();
                }
            }
        }
        // OutputDir was not found in the file
        return Optional.empty();
    }
}
