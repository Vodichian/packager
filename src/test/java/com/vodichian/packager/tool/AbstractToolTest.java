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
    AbstractTool abstractTool = new AbstractTool(new ToolSettings(ToolName.LAUNCH_4_J, null, null)) {
        @Override
        protected boolean validateConfiguration(File configuration) {
            return validConfig;
        }

        @Override
        protected boolean validateTool(File file) {
            return validTool;
        }

        @Override
        void execute() {

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

}