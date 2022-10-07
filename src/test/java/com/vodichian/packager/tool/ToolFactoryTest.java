package com.vodichian.packager.tool;

import com.vodichian.packager.PackagerException;
import com.vodichian.packager.Utils;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.testng.Assert.*;

public class ToolFactoryTest {

    @BeforeTest
    static void initJfxRuntime() {
        Platform.startup(() -> {
        });
    }


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

    @Test
    public void testTools() throws PackagerException, IOException {
        // remove existing settings, if any
        Path toolDir = Paths.get("tools");
        try (Stream<Path> settings = Files.list(toolDir)) {
            settings
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> Utils.getExtension(path.toFile().getName())
                            .orElse("none").equals(ToolFactory.SETTING_EXTENSION))
                    .forEach(path1 -> {
                        try {
                            Files.delete(path1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        try (Stream<Path> settings = Files.list(toolDir)) {
            long size = settings
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> Utils.getExtension(path.toFile().getName())
                            .orElse("none").equals(ToolFactory.SETTING_EXTENSION))
                    .count();
            assertEquals(size, 0);
        }

        ToolFactory.reset(); // need to do this because previous tests may have already created tool instances
        // ToolFactory.tools() should now create default settings and return a collection of AbstractTool
        Collection<AbstractTool> tools = ToolFactory.tools();
        assertEquals(tools.size(), ToolName.values().length);
        tools.forEach(tool -> System.out.println("Tool Found: " + tool.getSettings().getName()));

        try (Stream<Path> settings = Files.list(toolDir)) {
            long size = settings
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> Utils.getExtension(path.toFile().getName())
                            .orElse("none").equals(ToolFactory.SETTING_EXTENSION))
                    .count();
            assertEquals(size, ToolName.values().length, "Default settings were not correctly created");
        }

        // cleanup
        try (Stream<Path> settings = Files.list(toolDir)) {
            settings
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> Utils.getExtension(path.toFile().getName())
                            .orElse("none").equals(ToolFactory.SETTING_EXTENSION))
                    .forEach(path1 -> {
                        try {
                            Files.delete(path1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        try (Stream<Path> settings = Files.list(toolDir)) {
            long size = settings
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> Utils.getExtension(path.toFile().getName())
                            .orElse("none").equals(ToolFactory.SETTING_EXTENSION))
                    .count();
            assertEquals(size, 0);
        }
    }

    @Test
    public void testToolViews() throws PackagerException, IOException {
        List<Parent> toolViews = ToolFactory.toolViews();
        assertNotNull(toolViews);
        assertEquals(ToolName.values().length, toolViews.size());
    }

    @Test
    public void testGetFilters() throws PackagerException {
        try {
            ToolFactory.getFilters(new ToolSettings());
            fail("ToolSettings name was not set and should have thrown an exception");
        } catch (PackagerException ignored) {
        }
        Collection<FileChooser.ExtensionFilter> filters = ToolFactory.getFilters(new ToolSettings().setName(ToolName.LAUNCH_4_J));
        assertFalse(filters.isEmpty());
        filters = ToolFactory.getFilters(new ToolSettings().setName(ToolName.INNO_SETUP));
        assertFalse(filters.isEmpty());
        try {
            ToolFactory.getFilters(new ToolSettings().setName(ToolName.BUILD_EXTRACTOR));
            fail("BUILD_EXTRACTOR is invalid and should have thrown an exception");
        } catch (PackagerException ignored) {
        }
    }
}