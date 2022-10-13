package com.vodichian.packager.tool;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class AbstractToolTest {

    private File getRandomFile() {
        return new File(RandomStringUtils.randomAlphabetic(10));
    }

    private boolean validConfig = true;
    private boolean validTool = true;

    private final Executor testExecutor = (settings, toolState) -> {

    };
    AbstractTool abstractTool = new AbstractTool(new ToolSettings(
            ToolName.LAUNCH_4_J,
            null,
            null,
            0,
            true), testExecutor) {
        @Override
        protected boolean validateConfiguration(File configuration) {
            return validConfig;
        }

        @Override
        protected boolean validateTool(File file) {
            return validTool;
        }

        @Override
        public void execute() {

        }
    };

    @Test
    public void testSetTool() {
        assertNull(abstractTool.tool().get());
        File tool = getRandomFile();
        abstractTool.getSettings().setToolLocation(tool);
        assertTrue(abstractTool.toolIsValid().get());
        assertEquals(abstractTool.tool().get(), tool);
        assertEquals(abstractTool.getSettings().getToolLocation(), tool);

        validTool = false;
        tool = getRandomFile();
        abstractTool.getSettings().setToolLocation(tool);
        assertFalse(abstractTool.toolIsValid().get());
        assertEquals(abstractTool.tool().get(), tool);
        assertEquals(abstractTool.getSettings().getToolLocation(), tool);
    }

    @Test
    public void testSetConfiguration() {
        assertNull(abstractTool.configuration().get());
        File config = getRandomFile();
        abstractTool.getSettings().setConfiguration(config);
        assertTrue(abstractTool.configIsValid().get());
        assertEquals(abstractTool.configuration().get(), config);
        assertEquals(abstractTool.getSettings().getConfiguration(), config);

        validConfig = false;
        config = getRandomFile();
        abstractTool.getSettings().setConfiguration(config);
        assertFalse(abstractTool.configIsValid().get());
        assertEquals(abstractTool.configuration().get(), config);
        assertEquals(abstractTool.getSettings().getConfiguration(), config);
    }

    @Test
    public void testName() {
        assertEquals(abstractTool.name().get(), ToolName.LAUNCH_4_J);
    }

    /**
     * Hard to test here, as state is mostly controlled and modified by the implementing classes. Just verify initial
     * state is {@link ToolState#CONFIG_ERROR}.
     */
    @Test
    public void testState() {
        assertEquals(abstractTool.state().get(), ToolState.CONFIG_ERROR);
    }
}