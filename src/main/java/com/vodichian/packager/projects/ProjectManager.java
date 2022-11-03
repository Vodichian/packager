package com.vodichian.packager.projects;

import com.amihaiemil.eoyaml.*;
import com.vodichian.packager.PackagerException;
import com.vodichian.packager.tool.*;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class ProjectManager {
    private final ReadOnlyListWrapper<Project> projects;
    private static ProjectManager INSTANCE;

    private Path projectsPath;

    private ProjectManager() {

        projects = new ReadOnlyListWrapper<>(FXCollections.observableList(new ArrayList<>()));
        projectsPath = Path.of("projects.yaml");
    }

    public static ProjectManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProjectManager();
        }
        return INSTANCE;
    }

    public Optional<Project> find(String name) {
        return projects.stream().filter(project -> project.getName().equals(name)).findAny();
    }

    public void load(Path path) throws IOException {
        if (!Files.exists(path)) {
            post("Path does not exist: " + path);
            return;
        }
        projectsPath = path;
        load();
    }

    public void load() throws IOException {
        if (projectsPath == null) {
            throw new IOException("Project paths has not been set");
        } else if (!Files.exists(projectsPath)) {
            throw new IOException("Project paths has been set but does not exist");
        }
        projects.clear();
        YamlMapping projectsYaml = Yaml.createYamlInput(projectsPath.toFile()).readYamlMapping();
        for (YamlNode projectKey : projectsYaml.keys()) {
            System.out.println(projectKey);
            Project project = buildProject(nameFromKey(projectKey.toString()), projectsYaml.value(projectKey).asMapping());
            projects.add(project);
        }
    }

    private String nameFromKey(final String keyString) {
        int beginIndex = keyString.indexOf("---");
        int endIndex = keyString.lastIndexOf("...");
        return keyString.substring(beginIndex + 3, endIndex).trim();
    }

    private Project buildProject(String projectName, YamlMapping projectYaml) {
        Project project = new ProjectImpl();
        System.out.println("Projectname: " + projectName);
        project.setName(projectName);

        LocalDateTime date = projectYaml.dateTime("lastAccessed");
        if (date != null) project.setLastAccessed(date);

        projectYaml.keys().forEach(yamlNode -> {
            System.out.println("key: " + yamlNode.toString());
            System.out.println("value: " + projectYaml.value(yamlNode));
        });

        for (YamlNode toolKey : projectYaml.keys()) {
            YamlNode node = projectYaml.value(toolKey);
            System.out.println("toolKey node: " + node);
            if (node.type().equals(Node.MAPPING)) {
                AbstractTool tool = buildTool(nameFromKey(toolKey.toString()), node.asMapping());
                project.add(tool);
            }
        }
        postProcessBuildTool(project);

        return project;
    }

    /**
     * {@link BuildToolSettings} requires special handling, so if it exists for <code>project</code>, do the
     * post-processing here.
     *
     * @param project the project to post-process
     */
    private void postProcessBuildTool(Project project) {
        Collection<AbstractTool> tools = project.getTools();
        Optional<AbstractTool> buildOpt = tools.stream()
                .filter(tool -> tool.getSettings().getName().equals(ToolName.BUILD_EXTRACTOR))
                .findAny();
        if (buildOpt.isPresent()) {
            // get InnoTool
            Optional<AbstractTool> innoOpt = tools.stream()
                    .filter(tool -> tool.getSettings().getName().equals(ToolName.INNO_SETUP))
                    .findAny();
            // get LaunchTool
            Optional<AbstractTool> launchOpt = tools.stream()
                    .filter(tool -> tool.getSettings().getName().equals(ToolName.LAUNCH_4_J))
                    .findAny();
            // if project has both, add to BuildTool.ToolSettings
            if (innoOpt.isPresent() && launchOpt.isPresent()) {
                ((BuildToolSettings) buildOpt.get().getSettings()).setInnoTool((InnoTool) innoOpt.get());
                ((BuildToolSettings) buildOpt.get().getSettings()).setLaunchTool((Launch4jTool) launchOpt.get());
            }
            // else post error and return
            post("Found a build tool without corresponding Inno or Launch tools");
        }
    }

    private AbstractTool buildTool(String toolNameString, YamlMapping toolYaml) {
        ToolName toolName = ToolName.valueOf(toolNameString);
        AbstractTool tool;
        switch (toolName) {

            case INNO_SETUP: {
                ToolSettings settings = buildSettings(toolYaml, new ToolSettings());
                settings.setName(toolName);
                tool = new InnoTool(settings, new InnoExecutor());
                break;
            }
            case LAUNCH_4_J: {
                ToolSettings settings = buildSettings(toolYaml, new ToolSettings());
                settings.setName(toolName);
                tool = new Launch4jTool(settings, new LaunchExecutor());
                break;
            }
            case BUILD_EXTRACTOR: {
                ToolSettings settings = buildSettings(toolYaml, new BuildToolSettings());
                settings.setName(toolName);
                tool = new BuildTool(settings, new BuildExecutor());
                break;
            }
            default: {
                throw new RuntimeException("Unknown tool: " + toolName);
            }
        }
        return tool;
    }

    private ToolSettings buildSettings(YamlMapping toolYaml, ToolSettings settings) {

        String toolLocationString = toolYaml.string("tool_location");
        if (toolLocationString != null && !toolLocationString.isEmpty()) {
            settings.setToolLocation(new File(toolLocationString));
        }

        String configLocationString = toolYaml.string("config_location");
        if (configLocationString != null && !configLocationString.isEmpty()) {
            settings.setConfiguration(new File(configLocationString));
        }

        int priority = toolYaml.integer("priority");
        if (priority >= 0) {
            settings.setPriority(priority);
        }

        String enabledString = toolYaml.string("enabled");
        if (enabledString != null) {
            settings.setEnabled(Boolean.parseBoolean(enabledString));
        }
        return settings;
    }

    public void save(Path path) throws IOException {
        YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();
        for (Project project : projects) {
            builder = builder.add(project.getName(), toYaml(project));
        }
        YamlMapping projectsYaml = builder.build("Project configuration for Packager");
        System.out.println("projects.yaml");
        System.out.println("=============");
        System.out.println(projectsYaml.toString());
        final PrintWriter writer = new PrintWriter(new FileWriter(path.toFile()));
        writer.print(projectsYaml);
        writer.close();
    }

    public void save() throws IOException {
        if (projectsPath == null) {
            post("Failed to save, path to projects has not been set");
            return;
        }
        save(projectsPath);
    }

    private YamlMapping toYaml(Project project) {
        YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();
        builder = builder.add("lastAccessed", project.getLastAccessed().toString());
        for (AbstractTool tool : project.getTools()) {
            ToolSettings settings = tool.getSettings();
            builder = builder.add(settings.getName().toString(), toYaml(settings));
        }
        return builder.build();
    }

    private YamlMapping toYaml(ToolSettings settings) {
        File toolFile = settings.getToolLocation();
        File configFile = settings.getConfiguration();
        return Yaml.createYamlMappingBuilder()
                .add("tool_location", (toolFile == null) ? "" : toolFile.getAbsolutePath())
                .add("config_location", (configFile == null) ? "" : configFile.getAbsolutePath())
                .add("priority", String.valueOf(settings.getPriority()))
                .add("enabled", String.valueOf(settings.getEnabled()))
                .build();
    }

    public void clearProjects() {
        projects.clear();
    }

    protected Optional<Project> add(final Project project) {
        boolean exists = projects.stream().anyMatch(p -> p.getName().equals(project.getName()));
        Project result;
        if (exists) {
            post("A project named \"" + project.getName() + "\" already exists.");
            result = null;
        } else {
            projects.add(project);
            result = project;
        }
        return Optional.ofNullable(result);
    }

    private void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }

    public void remove(Project project) throws IOException {
        projects.remove(project);
        save();
    }

    public ObservableList<Project> getProjects() {
        return projects.getReadOnlyProperty();
    }

    /**
     * @return the lass accessed {@link ProjectImpl}, or null if no projects have been created
     */
    public Optional<Project> getLastAccessed() {
        return projects.stream().max(Comparator.comparing(Project::getLastAccessed));
    }

    public Project save(Project project) throws IOException, PackagerException {
        save();
        Optional<Project> optional = find(project.getName());
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new PackagerException("Failed to find saved project");
        }
    }

    /**
     * Create a new project with the given <code>name</code>. Will fail to return a project if a project with
     * <code>name</code> already exists.
     *
     * @param name the name of the project
     * @return an {@link Optional} containing the newly created project
     */
    public Optional<Project> newProject(String name) {
        Project project = new ProjectImpl();
        project.setName(name);
        return add(project);
    }
}
