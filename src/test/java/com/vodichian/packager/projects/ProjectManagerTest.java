package com.vodichian.packager.projects;

import com.vodichian.packager.tool.*;
import javafx.beans.property.ObjectProperty;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.file.Files.createTempFile;
import static org.testng.Assert.*;

/**
 * @startuml
 * object setup
 * object testGetLastAccessed
 * object testLoad
 * object testSave
 * object buildFillVerify
 * object testAdd
 * object testFind
 * object testRemove
 * setup --> testAdd
 * setup --> testFind
 * setup --> testRemove
 * testAdd --> buildFillVerify
 * testFind --> buildFillVerify
 * testRemove --> buildFillVerify
 * buildFillVerify --> testSave
 * testSave --> testLoad
 * testLoad --> testGetLastAccessed
 * @enduml
 */

public class ProjectManagerTest {
    private Project project1, project2, project3;
    private ToolSettings settings1, settings2, settings3, settings4, settings5;
    private AbstractTool tool1, tool2, tool3, tool4, tool5;
    private Path path;


    private List<Project> projects;

    @Test
    @BeforeClass
    public void setup() throws IOException {
        path = createTempFile("projects", ".tmp");

        // make project1
        project1 = new Project();
        project1.setName("First Project");
        project1.setLastAccessed(LocalDateTime.now());
        settings1 = new ToolSettings()
                .setEnabled(true)
                .setName(ToolName.BUILD_EXTRACTOR)
                .setPriority(1)
                .setConfiguration(new File("config1"))
                .setToolLocation(new File("tool1"));
        settings2 = new ToolSettings()
                .setEnabled(true)
                .setName(ToolName.INNO_SETUP)
                .setPriority(3)
                .setConfiguration(new File("config2"))
                .setToolLocation(new File("tool2"));
        tool1 = new MockTool(settings1, new MockExecutor());
        tool2 = new MockTool(settings2, new MockExecutor());
        project1.add(tool1);
        project1.add(tool2);

        // make project2
        project2 = new Project();
        project2.setName("Second Project");
        project2.setLastAccessed(LocalDateTime.now().minusDays(1));
        settings3 = new ToolSettings()
                .setEnabled(true)
                .setName(ToolName.BUILD_EXTRACTOR)
                .setPriority(4)
                .setConfiguration(new File("config3"))
                .setToolLocation(new File("tool3"));
        settings4 = new ToolSettings()
                .setEnabled(true)
                .setName(ToolName.LAUNCH_4_J)
                .setPriority(2)
                .setConfiguration(new File("config4"))
                .setToolLocation(new File("tool4"));
        settings5 = new ToolSettings()
                .setEnabled(true)
                .setName(ToolName.INNO_SETUP)
                .setPriority(3)
                .setConfiguration(new File("config5"))
                .setToolLocation(new File("tool5"));
        tool3 = new MockTool(settings3, new MockExecutor());
        tool4 = new MockTool(settings4, new MockExecutor());
        tool5 = new MockTool(settings5, new MockExecutor());
        project2.add(tool3);
        project2.add(tool4);
        project2.add(tool5);


        // make project3
        project3 = new Project();
        project3.setName("Third Project");
        project3.setLastAccessed(LocalDateTime.now().minusDays(2));
        settings3 = new ToolSettings()
                .setEnabled(true)
                .setName(ToolName.BUILD_EXTRACTOR)
                .setPriority(5)
                .setConfiguration(new File("config3"))
                .setToolLocation(new File("tool3"));
        settings4 = new ToolSettings()
                .setEnabled(true)
                .setName(ToolName.LAUNCH_4_J)
                .setPriority(2)
                .setConfiguration(new File("config4"))
                .setToolLocation(new File("tool4"));
        settings5 = new ToolSettings()
                .setEnabled(true)
                .setName(ToolName.INNO_SETUP)
                .setPriority(3)
                .setConfiguration(new File("config5"))
                .setToolLocation(new File("tool5"));
        tool3 = new MockTool(settings3, new MockExecutor());
        tool4 = new MockTool(settings4, new MockExecutor());
        tool5 = new MockTool(settings5, new MockExecutor());
        project3.add(tool3);
        project3.add(tool4);
        project3.add(tool5);


        projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projects.add(project3);
    }

    @Test(dependsOnMethods = "testLoad")
    public void testGetLastAccessed() {
        ProjectManager pm = ProjectManager.getInstance();
        pm.getLastAccessed().ifPresentOrElse(
                project -> assertEquals(project1.getName(), project.getName(), project.getName() + " was returned"),
                () -> fail("lastAccessed should not have been null"));
    }

    @Test(dependsOnMethods = {"testSave"})
    public void testLoad() throws IOException {
        ProjectManager pm = ProjectManager.getInstance();
        // ensure testSave had been successfully run
        assertFalse(projects.isEmpty());
        assertEquals(pm.getProjects().size(), projects.size());

        // clear and verify, then load from file
        pm.clearProjects();
        assertTrue(pm.getProjects().isEmpty());
        pm.getLastAccessed().ifPresentOrElse(project -> fail("should not have been present"),
                () -> System.out.println("Did not have a lastAccessed project, this is correct"));

        pm.load(path);
        assertEquals(pm.getProjects().size(), projects.size());

        pm.getProjects().forEach(project -> {
            String name = project.getName();
            Project actual = projects.stream().filter(p -> p.getName().equals(name)).findAny().orElseThrow();
            assertEquals(actual.getLastAccessed().getDayOfYear(), project.getLastAccessed().getDayOfYear());
            assertEquals(actual.getTools().size(), project.getTools().size());
        });
        // TODO: 10/25/2022  this test is incomplete, but I'm running out of time and it is close enough coupled with
        //  manual validation.
    }

    /**
     * Testing {@link ProjectManager}'s ability to saving its content to the given Path. The actual written content are
     * not verified here, but in {@link #testLoad()}
     *
     * @throws IOException test fails on IO error
     */
    @Test(dependsOnMethods = {"buildFillVerify"})
    public void testSave() throws IOException {
        ProjectManager pm = ProjectManager.getInstance();
        assertEquals(pm.getProjects().size(), projects.size());
        assertEquals(Files.size(path), 0);
        System.out.println("Saving to: " + path.toFile().getAbsolutePath());
        pm.save(path);
        assertTrue(Files.size(path) > 0);
    }

    /**
     * First test called, used to build the ProjectManager, fill it programmatically with projects and tools,
     * and verify the success of these steps. This data will be used by later methods.
     */
    @Test(dependsOnMethods = {"testAdd", "testFind", "testRemove"})
    public void buildFillVerify() {
        ProjectManager pm = ProjectManager.getInstance();
        pm.clearProjects();
        assertEquals(pm.getProjects().size(), 0);

        pm.add(project2);
        pm.add(project1);
        pm.add(project3);
        assertEquals(pm.getProjects().size(), projects.size());
        for (Project project : projects) {
            assertTrue(pm.getProjects().contains(project));
        }
    }

    @Test
    public void testAdd() {
        ProjectManager pm = ProjectManager.getInstance();
        pm.clearProjects();
        assertTrue(pm.getProjects().isEmpty());
        String name = "a project";
        Optional<Project> result = pm.add(new Project(name));
        assertTrue(result.isPresent());
        assertEquals(name, result.get().getName());
        assertEquals(pm.getProjects().size(), 1);
        result = pm.add(new Project(name));
        assertFalse(result.isPresent(), "Project already exists, should have returned false");
    }

    @Test
    public void testFind() {
        ProjectManager pm = ProjectManager.getInstance();
        pm.clearProjects();
        pm.add(project1);
        pm.add(project2);
        pm.add(project3);

        pm.find(project2.getName()).ifPresentOrElse(
                project -> assertEquals(project, project2),
                () -> fail("Did not find " + project2));
        pm.find(project1.getName()).ifPresentOrElse(
                project -> assertEquals(project, project1),
                () -> fail("Did not find " + project1));
        pm.find(project3.getName()).ifPresentOrElse(
                project -> assertEquals(project, project3),
                () -> fail("Did not find " + project3.getName()));

        assertFalse(pm.find("non-existing").isPresent());
    }

    @Test
    public void testRemove() throws IOException {
        ProjectManager pm = ProjectManager.getInstance();
        pm.clearProjects();
        pm.add(project1);
        assertTrue(pm.getProjects().contains(project1));
        pm.remove(project1);
        assertFalse(pm.getProjects().contains(project1));
    }

    private static class MockTool extends AbstractTool {

        protected MockTool(ToolSettings settings, Executor executor) {
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

    private static class MockExecutor implements Executor {

        @Override
        public void execute(ToolSettings settings, ObjectProperty<ToolState> toolState) {
            System.out.println("mock execute was called....whaaaaat?");
        }
    }
}