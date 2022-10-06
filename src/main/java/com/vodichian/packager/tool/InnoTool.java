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
            EventBus.getDefault().post(new ToolMessage(NAME, "Configuration path was null"));
            return false;
        }
        Optional<String> result = Utils.getExtension(configurationPath.getName());
        return result.map(s -> s.equals("iss")).orElse(false);
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
        // TODO: 10/3/2022 make this work
        String command = "C:\\Program Files (x86)\\Inno Setup 6\\ISCC.exe";
        String config = "C:\\Users\\Rick\\Vodichian Projects\\Guild\\inno-build2.iss";
        ProcessBuilder pb = new ProcessBuilder(command, config);
//        pb.directory(workingDir.toFile());
        pb.redirectErrorStream(true);
        monitor(pb);
    }

    private void monitor(ProcessBuilder pb) {
        Runnable r = () -> {
            try {
                toolStateWrapper.set(ToolState.RUNNING);
                Process p = pb.start();
                try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = input.readLine()) != null) {
                        EventBus.getDefault().post(new ToolMessage(NAME, line));
                        if (line.contains("Compile aborted.")) { // error message: "Compile aborted."
                            toolStateWrapper.set(ToolState.FAILURE);
                        } else if (line.contains("Successful compile")) { // success message: "Successful compile"
                            toolStateWrapper.set(ToolState.SUCCESS);
                        }
                    }
                }
            } catch (IOException e) {
                toolStateWrapper.set(ToolState.FAILURE);
                EventBus.getDefault().post(new ToolMessage(NAME, e.getMessage()));
            }
        };
        new Thread(r).start();
    }

}
