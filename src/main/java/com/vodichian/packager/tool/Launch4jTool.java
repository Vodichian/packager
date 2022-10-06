package com.vodichian.packager.tool;

import com.vodichian.packager.Utils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Optional;

import static com.vodichian.packager.Utils.getExtension;

public class Launch4jTool extends AbstractTool {

    private static final String NAME = "Launch4j";

    public Launch4jTool(ToolSettings settings) {
        super(settings);
    }

    @Override
    protected boolean validateConfiguration(File configurationPath) {
        if (configurationPath == null) {
            EventBus.getDefault().post(new ToolMessage(NAME, "Configuration path was null"));
            return false;
        }
        Optional<String> result = Utils.getExtension(configurationPath.getName());
        return result.map(s -> s.equals("xml")).orElse(false);
    }

    @Override
    protected boolean validateTool(File tool) {
        if (tool == null) {
            EventBus.getDefault().post(new ToolMessage(NAME,"Tool path was null"));
            return false;
        }
        Optional<String> result = getExtension(tool.getName());
        boolean isValid = result.map(s -> s.equals("exe")).orElse(false);
        EventBus.getDefault().post(new ToolMessage(NAME, "Tool is valid: " + isValid));
        return isValid;
    }

    @Override
    void execute() {

    }

}
