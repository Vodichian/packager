package com.vodichian.packager.tool;

import com.vodichian.packager.Utils;

import java.io.File;
import java.nio.file.Files;
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

}
