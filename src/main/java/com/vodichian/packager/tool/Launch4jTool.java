package com.vodichian.packager.tool;

import com.vodichian.packager.Utils;

import java.io.File;
import java.util.Optional;

import static com.vodichian.packager.Utils.getExtension;

public class Launch4jTool extends AbstractTool {

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

}
