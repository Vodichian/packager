package com.vodichian.packager;

import com.vodichian.packager.tool.*;
import javafx.beans.property.ObjectProperty;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class SequencerTest {

    @Test
    public void testSetTools() {
        Sequencer sequencer = new Sequencer();
        assertNull(sequencer.currentlyExecutingProperty.get());
        assertFalse(sequencer.readyProperty.get());

        sequencer.setTools(new ArrayList<>());
        assertNull(sequencer.currentlyExecutingProperty.get());
        assertFalse(sequencer.readyProperty.get());

        // test all configured correctly
        List<AbstractTool> tools = new ArrayList<>();
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        sequencer.setTools(tools);
        assertEquals(sequencer.toolCount(), tools.size());
        assertNull(sequencer.currentlyExecutingProperty.get());
        assertTrue(sequencer.readyProperty.get());

        // test tool fail
        sequencer = new Sequencer();
        tools = new ArrayList<>();
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        tools.add(new TestToolFailTool(new ToolSettings(), null));
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        sequencer.setTools(tools);
        assertEquals(sequencer.toolCount(), tools.size());
        assertNull(sequencer.currentlyExecutingProperty.get());
        assertFalse(sequencer.readyProperty.get());

        // test config fail
        sequencer = new Sequencer();
        tools = new ArrayList<>();
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        tools.add(new TestToolFailConfig(new ToolSettings(), null));
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        sequencer.setTools(tools);
        assertEquals(sequencer.toolCount(), tools.size());
        assertNull(sequencer.currentlyExecutingProperty.get());
        assertFalse(sequencer.readyProperty.get());

        // test both fail
        sequencer = new Sequencer();
        tools = new ArrayList<>();
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        tools.add(new TestToolFailBoth(new ToolSettings(), null));
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        sequencer.setTools(tools);
        assertEquals(sequencer.toolCount(), tools.size());
        assertNull(sequencer.currentlyExecutingProperty.get());
        assertFalse(sequencer.readyProperty.get());

        // test combination fail
        sequencer = new Sequencer();
        tools = new ArrayList<>();
        tools.add(new TestToolSuccess(new ToolSettings(), null));
        tools.add(new TestToolFailBoth(new ToolSettings(), null));
        tools.add(new TestToolFailTool(new ToolSettings(), null));
        tools.add(new TestToolFailConfig(new ToolSettings(), null));
        sequencer.setTools(tools);
        assertEquals(sequencer.toolCount(), tools.size());
        assertNull(sequencer.currentlyExecutingProperty.get());
        assertFalse(sequencer.readyProperty.get());
    }

    /**
     * Test Plan:
     * <p>
     * // - need to verify currentlyExecutingProperty updates
     * //          - external monitor collects updates, then verifies updates were in order
     * // - need to verify readyProperty
     * //          - verify readyProperty toggles off after runSequence is called and toggles back on after it finishes
     * // - need to verify every tool in Sequencer executes in order of priority
     * //          - custom class implements execute, adds self to a List when called, verify order of list after execution
     */
    @Test
    public void testRunSequence() {
        Sequencer sequencer = new Sequencer();
        try {
            sequencer.runSequence();
            fail("No tools loaded, expected exception to be thrown");
        } catch (PackagerException e) {
            // should throw exception because no tools
        }

        // monitor for changes to currentlyExecutingProperty
        List<AbstractTool> currentMonitor = new ArrayList<>();
        sequencer.currentlyExecutingProperty.addListener(observable ->
                currentMonitor.add(sequencer.currentlyExecutingProperty.get()));

        // monitor for state changes
        List<Boolean> stateChanges = new ArrayList<>();
        sequencer.readyProperty.addListener(observable -> stateChanges.add(sequencer.readyProperty.get()));

        List<AbstractTool> tools = new ArrayList<>();
        tools.add(new TestToolSuccess(new ToolSettings().setName(ToolName.LAUNCH_4_J).setPriority(2), new TestExecutor()));
        tools.add(new TestToolSuccess(new ToolSettings().setName(ToolName.INNO_SETUP).setPriority(1), new TestExecutor()));
        tools.add(new TestToolSuccess(new ToolSettings().setName(ToolName.BUILD_EXTRACTOR).setPriority(3), new TestExecutor()));

        sequencer.setTools(tools);
        assertTrue(sequencer.readyProperty.get());

        try {
            sequencer.runSequence();
            // verify state changes
            assertEquals(stateChanges.size(), 3, stateChanges.toString());
            assertTrue(stateChanges.get(0));    // changes to true when tools are added
            assertFalse(stateChanges.get(1));   // changes to false while running
            assertTrue(stateChanges.get(2));    // changes to true after finished

            // verify execution and priority
            assertEquals(TestExecutor.settingsList.size(), 3);
            assertEquals(TestExecutor.settingsList.get(0).getName(), ToolName.BUILD_EXTRACTOR);
            assertEquals(TestExecutor.settingsList.get(1).getName(), ToolName.LAUNCH_4_J);
            assertEquals(TestExecutor.settingsList.get(2).getName(), ToolName.INNO_SETUP);

            // verify currentExecuting changes
            assertEquals(currentMonitor.size(), 4, currentMonitor.toString());
            assertEquals(currentMonitor.get(0).getSettings().getName(), ToolName.BUILD_EXTRACTOR);
            assertEquals(currentMonitor.get(1).getSettings().getName(), ToolName.LAUNCH_4_J);
            assertEquals(currentMonitor.get(2).getSettings().getName(), ToolName.INNO_SETUP);
            assertNull(currentMonitor.get(3));
        } catch (PackagerException e) {
            fail("Sequencer failed", e);
        }

    }
}

class TestExecutor implements Executor {
    /**
     * Shared among all instances
     */
    public final static List<ToolSettings> settingsList = new ArrayList<>();

    @Override
    public void execute(ToolSettings settings, ObjectProperty<ToolState> toolState) {
        toolState.set(ToolState.RUNNING);
        settingsList.add(settings);
        toolState.set(ToolState.SUCCESS);
    }
}

class TestToolSuccess extends AbstractTool {

    protected TestToolSuccess(ToolSettings settings, Executor executor) {
        super(settings, executor);
    }

    @Override
    protected boolean validateConfiguration(File configuration) {
        return true;
    }

    @Override
    protected boolean validateTool(File file) {
        return true;
    }
}

class TestToolFailTool extends AbstractTool {

    protected TestToolFailTool(ToolSettings settings, Executor executor) {
        super(settings, executor);
    }

    @Override
    protected boolean validateConfiguration(File configuration) {
        return true;
    }

    @Override
    protected boolean validateTool(File file) {
        return false;
    }
}

class TestToolFailConfig extends AbstractTool {

    protected TestToolFailConfig(ToolSettings settings, Executor executor) {
        super(settings, executor);
    }

    @Override
    protected boolean validateConfiguration(File configuration) {
        return false;
    }

    @Override
    protected boolean validateTool(File file) {
        return true;
    }
}

class TestToolFailBoth extends AbstractTool {

    protected TestToolFailBoth(ToolSettings settings, Executor executor) {
        super(settings, executor);
    }

    @Override
    protected boolean validateConfiguration(File configuration) {
        return false;
    }

    @Override
    protected boolean validateTool(File file) {
        return false;
    }
}
