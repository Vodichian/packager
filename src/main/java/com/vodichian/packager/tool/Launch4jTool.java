package com.vodichian.packager.tool;

import com.vodichian.packager.Utils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Optional;

import static com.vodichian.packager.Utils.getExtension;

public class Launch4jTool extends AbstractTool {

    public Launch4jTool(ToolSettings settings) {
        super(settings);
    }

    @Override
    protected boolean validateConfiguration(File configurationPath) {
        if (configurationPath == null) {
            EventBus.getDefault().post(new ToolMessage("Configuration path was null"));
            return false;
        }
        Optional<String> result = Utils.getExtension(configurationPath.getName());
        return result.map(s -> s.equals("xml")).orElse(false);
    }

    @Override
    protected boolean validateTool(File tool) {
        if (tool == null) {
            EventBus.getDefault().post(new ToolMessage("Tool path was null"));
            return false;
        }
        Optional<String> result = getExtension(tool.getName());
        boolean isValid = result.map(s -> s.equals("exe")).orElse(false);
        EventBus.getDefault().post(new ToolMessage("Launch4j Tool> tool is valid: " + isValid));
        return isValid;
    }

    @Override
    void execute() {

    }

}
