package com.vodichian.packager.tool;

import javafx.scene.control.TextField;

import java.io.File;
import java.util.Optional;

public class Launch4jTool extends AbstractTool {
    public Launch4jTool() {
    }

    @Override
    protected boolean validateConfiguration(File configurationPath) {
        Optional<String> result = getExtension(configurationPath.getName());
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
}
