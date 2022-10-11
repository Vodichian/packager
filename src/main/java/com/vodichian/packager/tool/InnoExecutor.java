package com.vodichian.packager.tool;

import javafx.beans.property.ObjectProperty;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InnoExecutor implements Executor {
    ObjectProperty<ToolState> toolStateWrapper;

    @Override
    public void execute(ToolSettings settings, ObjectProperty<ToolState> toolStateWrapper) {
        this.toolStateWrapper = toolStateWrapper;
        String command = settings.toolLocationProperty.get().getAbsolutePath();
        String config = settings.configurationProperty.get().getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(command, config);
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

    private void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }

}
