package com.vodichian.packager.tool;

import com.vodichian.packager.PackagerException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class ToolFactoryTest {

    @Test
    public void testMake() {
        ToolSettings settings = new ToolSettings();
        try {
            settings.setName(ToolName.LAUNCH_4_J);
            AbstractTool tool = ToolFactory.make(settings);
            assertEquals(tool.getClass(), Launch4jTool.class);
        } catch (PackagerException e) {
            fail("Exception was thrown", e);
        }

        settings = new ToolSettings(ToolName.INNO_SETUP, null, null);
        try {
            AbstractTool tool = ToolFactory.make(settings);
            assertEquals(tool.getClass(), InnoTool.class);
        } catch (PackagerException e) {
            fail("Exception was thrown, e");
        }
    }
}