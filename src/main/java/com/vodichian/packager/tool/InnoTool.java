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
    public static final String NAME = "InnoTool";

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
    void execute() {
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
                Process p = pb.start();
                try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = input.readLine()) != null) {
                        // error message: "Compile aborted."
                        // success message: "Successful compile"
                        System.out.println(line);
                    }
                }
            } catch (IOException e) {
                System.err.println("Something bad happened: " + e.getMessage());
            }
        };
        new Thread(r).start();
    }

}
