package com.vodichian.packager.tool;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Random;

import static org.testng.Assert.*;

public class ToolSettingsTest {
    private final Random RANDOM = new Random();

    private ToolName getRandomName() {
        return ToolName.values()[RANDOM.nextInt(ToolName.values().length)];
    }

    private File getRandomFile() {
        return new File(RandomStringUtils.randomAlphabetic(10));
    }


    @Test
    public void testName() {
        ToolSettings settings = new ToolSettings();
        assertNull(settings.getName());
        ToolName name = getRandomName();
        System.out.println("Name: " + name);
        settings.setName(name);
        assertEquals(settings.getName(), name);

        name = getRandomName();
        settings = new ToolSettings(name, null, null, 0, true);
        assertEquals(settings.getName(), name);
    }

    @Test
    public void testToolLocation() {
        ToolSettings settings = new ToolSettings();
        assertNull(settings.getToolLocation());
        File file = getRandomFile();
        settings.setToolLocation(file);
        assertEquals(settings.getToolLocation(), file);

        file = getRandomFile();
        settings = new ToolSettings(null, file, null, 0, true);
        assertEquals(settings.getToolLocation(), file);
    }

    @Test
    public void testConfiguration() {
        ToolSettings settings = new ToolSettings();
        assertNull(settings.getConfiguration());
        File file = getRandomFile();
        settings.setConfiguration(file);
        assertEquals(settings.getConfiguration(), file);

        file = getRandomFile();
        settings = new ToolSettings(null, null, file, 0, true);
        assertEquals(settings.getConfiguration(), file);
    }

    @Test
    public void testPriority() {
        ToolSettings settings = new ToolSettings();
        assertEquals(settings.getPriority(), 0);
        int expected = RANDOM.nextInt();
        settings.setPriority(expected);
        assertEquals(settings.getPriority(), expected);

        expected = RANDOM.nextInt();
        settings = new ToolSettings(ToolName.BUILD_EXTRACTOR, null, null, expected, true);
        assertEquals(settings.getPriority(), expected);
    }

    @Test
    public void testEnabled() {
        ToolSettings settings = new ToolSettings();
        assertTrue(settings.getEnabled());
        for (int i = 0; i < 5; i++) {
            boolean expected = RANDOM.nextBoolean();
            ToolSettings result = settings.setEnabled(expected);
            assertEquals(result, settings);
            assertEquals(settings.getEnabled(), expected);
            assertEquals(settings.enabledProperty.get(), expected);
        }
    }

}