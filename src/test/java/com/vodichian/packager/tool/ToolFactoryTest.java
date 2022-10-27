package com.vodichian.packager.tool;

import com.vodichian.packager.PackagerException;
import com.vodichian.packager.projects.Project;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.*;

public class ToolFactoryTest {

    @BeforeTest
    static void initJfxRuntime() {
        Platform.startup(() -> {
        });
    }

    @Test
    public void testToolViews() {
        Project project = new Project();
        List<Parent> toolViews = ToolFactory.toolViews(project);
        assertTrue(toolViews.isEmpty());
        assertNotNull(toolViews);

        project.add(new MockTool());
        project.add(new MockTool());
        project.add(new MockTool());
        assertEquals(ToolFactory.toolViews(project).size(), project.getTools().size());
    }

    @Test
    public void testGetFilters() throws PackagerException {
        try {
            ToolFactory.getFilters(new ToolSettings());
            fail("ToolSettings name was not set and should have thrown an exception");
        } catch (PackagerException ignored) {
        }
        // test Launch4j
        Collection<FileChooser.ExtensionFilter> filters = ToolFactory.getFilters(new ToolSettings().setName(ToolName.LAUNCH_4_J));
        List<String> extensionsLaunch = new ArrayList<>();
        filters.forEach(extensionFilter -> extensionsLaunch.addAll(extensionFilter.getExtensions()));
        assertEquals(extensionsLaunch.size(), 2);
        assertTrue(extensionsLaunch.contains("*.xml"));
        assertTrue(extensionsLaunch.contains("*.*"));

        // test Inno
        filters = ToolFactory.getFilters(new ToolSettings().setName(ToolName.INNO_SETUP));
        List<String> extensionsInno = new ArrayList<>();
        filters.forEach(extensionFilter -> extensionsInno.addAll(extensionFilter.getExtensions()));
        assertEquals(extensionsInno.size(), 2);
        assertTrue(extensionsInno.contains("*.iss"));
        assertTrue(extensionsInno.contains("*.*"));

        // test BuildTool
        filters = ToolFactory.getFilters(new ToolSettings().setName(ToolName.BUILD_EXTRACTOR));
        List<String> extensionsBuild = new ArrayList<>();
        filters.forEach(extensionFilter -> extensionsBuild.addAll(extensionFilter.getExtensions()));
        assertEquals(extensionsBuild.size(), 2);
        assertTrue(extensionsBuild.contains("*.xml"));
        assertTrue(extensionsBuild.contains("*.*"));
    }

    private static class MockTool extends AbstractTool {


        protected MockTool() {
            this(new ToolSettings().setName(ToolName.BUILD_EXTRACTOR), null);
        }

        protected MockTool(ToolSettings settings, Executor executor) {
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

}