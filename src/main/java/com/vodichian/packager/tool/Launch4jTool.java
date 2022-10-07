package com.vodichian.packager.tool;

import com.vodichian.packager.Utils;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
            EventBus.getDefault().post(new ToolMessage(NAME, "Tool path was null"));
            return false;
        }
        Optional<String> result = getExtension(tool.getName());
        boolean isValid = result.map(s -> s.equals("exe")).orElse(false);
        EventBus.getDefault().post(new ToolMessage(NAME, "Tool is valid: " + isValid));
        return isValid;
    }

    @Override
    public void execute() {
        if (!toolIsValid().get() || !configIsValid().get()) {
            post("Tool is not configured correctly, aborting...");
            return;
        }

        String command = getSettings().toolLocationProperty.get().getAbsolutePath();
        String config = getSettings().configurationProperty.get().getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(command, config);
        pb.redirectErrorStream(true);
        monitor(pb);
    }

    private void post(String message) {
        EventBus.getDefault().post(new ToolMessage(NAME, message));
    }

    private void monitor(ProcessBuilder pb) {
        Runnable r = () -> {
            try {
                toolStateWrapper.set(ToolState.RUNNING);
                Process p = pb.start();
                try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = input.readLine()) != null) {
                        post(line);
                        if (line.contains("Successfully created")) {
                            toolStateWrapper.set(ToolState.SUCCESS);
                        }
                    }
                }
            } catch (IOException e) {
                toolStateWrapper.set(ToolState.FAILURE);
                post(e.getMessage());
            }
            if (!toolStateWrapper.get().equals(ToolState.SUCCESS)) {
                post("Tool failed to build; reasons unknown");
                toolStateWrapper.set(ToolState.FAILURE);
            }
        };
        new Thread(r).start();
    }

}
