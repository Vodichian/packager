package com.vodichian.packager.tool;

import javafx.stage.FileChooser;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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

        @Override
        public Collection<FileChooser.ExtensionFilter> getFilters() {
            return null;
        }
    };

    @Test
    public void testSetTool() throws IOException {
        assertNull(abstractTool.tool().get());
        File tool = getRandomFile();
        boolean result = abstractTool.setTool(tool);
        assertTrue(result);
        assertTrue(abstractTool.toolIsValid().get());
        assertEquals(abstractTool.tool().get(), tool);
        assertEquals(abstractTool.getSettings().getToolLocation(), tool);

        validTool = false;
        tool = getRandomFile();
        result = abstractTool.setTool(tool);
        assertFalse(result);
        assertFalse(abstractTool.toolIsValid().get());
        assertEquals(abstractTool.tool().get(), tool);
        assertEquals(abstractTool.getSettings().getToolLocation(), tool);
    }

    @Test
    public void testSetConfiguration() throws IOException {
        assertNull(abstractTool.configuration().get());
        File config = getRandomFile();
        boolean result = abstractTool.setConfiguration(config);
        assertTrue(result);
        assertTrue(abstractTool.configIsValid().get());
        assertEquals(abstractTool.configuration().get(), config);
        assertEquals(abstractTool.getSettings().getConfiguration(), config);

        validConfig = false;
        config = getRandomFile();
        result = abstractTool.setConfiguration(config);
        assertFalse(result);
        assertFalse(abstractTool.configIsValid().get());
        assertEquals(abstractTool.configuration().get(), config);
        assertEquals(abstractTool.getSettings().getConfiguration(), config);
    }

}