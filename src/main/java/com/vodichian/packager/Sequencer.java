package com.vodichian.packager;

import com.vodichian.packager.tool.AbstractTool;
import com.vodichian.packager.tool.ToolSettings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sequentially calls the execute methods on a list of {@link com.vodichian.packager.tool.AbstractTool} instances.
 */
public class Sequencer {

    private final List<AbstractTool> tools = new ArrayList<>();

    private final ReadOnlyBooleanWrapper readyPropertyWrapper = new ReadOnlyBooleanWrapper(false);
    /**
     * Indicates the Sequencer is ready to execute. Sequencer functionality is blocked while this is <code>false</code>.
     */
    public final ReadOnlyBooleanProperty readyProperty = readyPropertyWrapper.getReadOnlyProperty();

    private final ReadOnlyObjectWrapper<AbstractTool> currentlyExecutingPropertyWrapper = new ReadOnlyObjectWrapper<>(null);
    /**
     * Holds a reference to the {@link AbstractTool} currently executing. Value is <code>null</code> if idle.
     */
    public final ReadOnlyObjectProperty<AbstractTool> currentlyExecutingProperty = currentlyExecutingPropertyWrapper.getReadOnlyProperty();

    public void setTools(Collection<AbstractTool> toolCollection) {
        // check and monitor ToolState of each tool and set readyPropertyWrapper accordingly
        tools.clear();
        tools.addAll(toolCollection);
        if (!tools.isEmpty()) {
            if (tools.size() == 1) {
                readyPropertyWrapper.bind(tools.get(0).readyBinding);
            } else {
                BooleanBinding readyBinding = tools.get(0).readyBinding.and(tools.get(1).readyBinding);
                if (tools.size() > 2) {
                    for (int i = 2; i < tools.size(); i++) {
                        readyBinding = readyBinding.and(tools.get(i).readyBinding);
                    }
                }
                boolean result = readyBinding.get();
                System.out.println("readyBinding: "+result);
                System.out.println("readyBinding.isValid: "+readyBinding.isValid());
//                readyBinding.invalidate();
                readyPropertyWrapper.bind(readyBinding);
            }
        }
    }

    /**
     * Sequentially execute each enabled {@link AbstractTool#execute()} according to {@link ToolSettings#priorityProperty}.
     * The {@link #readyProperty} becomes false while active, and {@link #currentlyExecutingProperty} is updated with each
     * {@link AbstractTool} as it is being processed.
     *
     * @throws PackagerException if Sequencer is not ready, or if an {@link AbstractTool#execute()} fails.
     */
    public void runSequence() throws PackagerException {
        if (!readyProperty.get()) {
            throw new PackagerException("The sequencer is not ready");
        }
        // order according to priority
    }

    /**
     * @return the number of tools loaded into this instance
     */
    public int toolCount() {
        return tools.size();
    }

}
