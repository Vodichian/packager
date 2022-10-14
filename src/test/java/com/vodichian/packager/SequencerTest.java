package com.vodichian.packager;

import com.vodichian.packager.tool.AbstractTool;
import com.vodichian.packager.tool.Executor;
import com.vodichian.packager.tool.ToolSettings;
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
        assertEquals(sequencer.toolCount(),tools.size());
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
        assertEquals(sequencer.toolCount(),tools.size());
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
        assertEquals(sequencer.toolCount(),tools.size());
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
        assertEquals(sequencer.toolCount(),tools.size());
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
        assertEquals(sequencer.toolCount(),tools.size());
        assertNull(sequencer.currentlyExecutingProperty.get());
        assertFalse(sequencer.readyProperty.get());
    }

    @Test
    public void testRunSequence() {
        fail("Need to implement");
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
