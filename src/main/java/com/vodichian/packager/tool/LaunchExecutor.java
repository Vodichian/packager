package com.vodichian.packager.tool;

import javafx.beans.property.ObjectProperty;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LaunchExecutor implements Executor {

    @Override
    public void execute(ToolSettings settings, ObjectProperty<ToolState> toolState) {
        String command = settings.toolLocationProperty.get().getAbsolutePath();
        String config = settings.configurationProperty.get().getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(command, config);
        pb.redirectErrorStream(true);
        monitor(pb, toolState);

    }

    private void monitor(ProcessBuilder pb, ObjectProperty<ToolState> toolState) {
        Runnable r = () -> {
            try {
                toolState.set(ToolState.RUNNING);
                Process p = pb.start();
                try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = input.readLine()) != null) {
                        post(line);
                        if (line.contains("Successfully created")) {
                            toolState.set(ToolState.SUCCESS);
                        }
                    }
                }
            } catch (IOException e) {
                toolState.set(ToolState.FAILURE);
                post(e.getMessage());
            }
            if (!toolState.get().equals(ToolState.SUCCESS)) {
                post("Tool failed to build; reasons unknown");
                toolState.set(ToolState.FAILURE);
            }
        };
        new Thread(r).start();
    }

    private void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }

}
