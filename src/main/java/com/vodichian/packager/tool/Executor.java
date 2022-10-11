package com.vodichian.packager.tool;

import javafx.beans.property.ObjectProperty;

public interface Executor {
    void execute(ToolSettings settings, ObjectProperty<ToolState> toolState);
}
