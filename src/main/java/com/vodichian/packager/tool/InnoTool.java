package com.vodichian.packager.tool;

import com.vodichian.packager.Utils;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import static com.vodichian.packager.Utils.getExtension;

public class InnoTool extends AbstractTool {
    private static final String NAME = "InnoTool";

    public InnoTool(ToolSettings settings) {
        super(settings);
    }

    @Override
    protected boolean validateConfiguration(File configurationPath) {
        if (configurationPath == null) {
            post("Configuration path was null");
            return false;
        }
        Optional<String> result = Utils.getExtension(configurationPath.getName());
        return result.map(s -> s.equals("iss")).orElse(false);
    }

    @Override
    protected boolean validateTool(File tool) {
        if (tool == null) {
            post("Tool path was null");
            return false;
        }
        Optional<String> result = getExtension(tool.getName());
        boolean isValid = result.map(s -> s.equals("exe")).orElse(false);
        post("Tool is valid: " + isValid);
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
                        if (line.contains("Compile aborted.")) { // error message: "Compile aborted."
                            toolStateWrapper.set(ToolState.FAILURE);
                        } else if (line.contains("Successful compile")) { // success message: "Successful compile"
                            toolStateWrapper.set(ToolState.SUCCESS);
                        }
                    }
                }
            } catch (IOException e) {
                toolStateWrapper.set(ToolState.FAILURE);
                post(e.getMessage());
            }
        };
        new Thread(r).start();
    }

}
