package com.vodichian.packager.tool;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class BuildToolSettings extends ToolSettings {
    private final ObjectProperty<InnoTool> innoToolProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Launch4jTool> launchToolProperty = new SimpleObjectProperty<>();

    public ToolSettings setInnoTool(InnoTool innoTool) {
        this.innoToolProperty.set(innoTool);
        return this;
    }

    public InnoTool getInnoTool() {
        return innoToolProperty.get();
    }

    public ToolSettings setLaunchTool(Launch4jTool launchTool) {
        this.launchToolProperty.set(launchTool);
        return this;
    }

    public Launch4jTool getLaunchTool() {
        return launchToolProperty.get();
    }
}
