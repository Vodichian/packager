package com.vodichian.packager;

import com.vodichian.packager.tool.AbstractTool;
import com.vodichian.packager.tool.ToolMessage;
import com.vodichian.packager.tool.ToolSettings;
import com.vodichian.packager.tool.ToolState;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final BooleanProperty runningProperty = new SimpleBooleanProperty(false);
    private AbstractTool finalToolExecuted;

    public void setTools(Collection<AbstractTool> toolCollection) {
        // check and monitor ToolState of each tool and set readyPropertyWrapper accordingly
        tools.clear();
        tools.addAll(toolCollection);
        List<AbstractTool> filteredTools = tools.stream().filter(tool -> tool.getSettings().enabledProperty.get()).collect(Collectors.toList());
        if (!filteredTools.isEmpty()) {
            BooleanBinding readyBinding = filteredTools.get(0).readyBinding.and(runningProperty.not());
            for (int i = 1; i < filteredTools.size(); i++) {
                readyBinding = readyBinding.and(filteredTools.get(i).readyBinding);
            }
            readyPropertyWrapper.bind(readyBinding);
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
        runningProperty.set(true);
        finalToolExecuted = null;
        // order according to priority
        List<AbstractTool> validAndSorted = tools.stream()
                .filter(tool -> tool.getSettings().enabledProperty.get())
                .sorted((o1, o2) -> Integer.compare(o2.getSettings().getPriority(), o1.getSettings().getPriority()))
                .collect(Collectors.toList());
        tools.clear();
        tools.addAll(validAndSorted);
        for (AbstractTool tool : tools) {
            post("executing " + tool.getSettings().getName());
            currentlyExecutingPropertyWrapper.set(tool);
            tool.execute();
            do {
                try {
                    Thread.sleep(10); // TODO: 10/14/2022 need to rethink how tool.execute is implemented
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (tool.state().get() == ToolState.RUNNING);

        }
        runningProperty.set(false);
        finalToolExecuted = currentlyExecutingProperty.get();
        currentlyExecutingPropertyWrapper.set(null);
    }

    /**
     * @return the number of tools loaded into this instance
     */
    public int toolCount() {
        return tools.size();
    }

    private void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }

    public Optional<AbstractTool> getFinalExecuted() {
        return Optional.ofNullable(finalToolExecuted);
    }
}
