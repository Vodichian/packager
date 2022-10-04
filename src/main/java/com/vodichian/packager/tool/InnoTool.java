package com.vodichian.packager.tool;

import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

public class InnoTool extends AbstractTool {
    private static final FileChooser.ExtensionFilter ISS_EXT = new FileChooser.ExtensionFilter("Inno Config Files", "*.iss");
    private static final FileChooser.ExtensionFilter ALL_EXT = new FileChooser.ExtensionFilter("All Files", "*.*");
    private static final Collection<FileChooser.ExtensionFilter> INNO_FILTERS = Arrays.asList(ISS_EXT, ALL_EXT);

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

    @Override
    public Collection<FileChooser.ExtensionFilter> getFilters() {
        return INNO_FILTERS;
    }
}
