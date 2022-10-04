package com.vodichian.packager.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class InnoTool extends AbstractTool {

    public InnoTool(ToolSettings settings) {
        super(settings);
    }

    @Override
    protected boolean validateConfiguration(File configurationPath) {
        return false;
    }

    @Override
    protected boolean validateTool(File tool) {
        return false;
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
