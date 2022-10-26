package com.vodichian.packager.projects;

import com.amihaiemil.eoyaml.*;
import com.vodichian.packager.tool.*;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class ProjectManager {
    private final ReadOnlyListWrapper<Project> projects;
    private static ProjectManager INSTANCE;

    private ProjectManager() {
        projects = new ReadOnlyListWrapper<>(FXCollections.observableList(new ArrayList<>()));
    }

    public static ProjectManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProjectManager();
        }
        return INSTANCE;
    }

    public void load(Path path) throws IOException {
        projects.clear();
        YamlMapping projectsYaml = Yaml.createYamlInput(path.toFile()).readYamlMapping();
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
        Project project = new Project();
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

        return project;
    }

    private AbstractTool buildTool(String toolNameString, YamlMapping toolYaml) {
        System.out.println("toolNameString: " + toolNameString);
        System.out.println("endoftool");
        ToolName toolName = ToolName.valueOf(toolNameString);
        AbstractTool tool;
        ToolSettings settings = buildSettings(toolYaml);
        settings.setName(toolName);
        switch (toolName) {

            case INNO_SETUP:
                tool = new InnoTool(settings, new InnoExecutor());
                break;
            case LAUNCH_4_J:
                tool = new Launch4jTool(settings, new LaunchExecutor());
                break;
            case BUILD_EXTRACTOR:
                tool = new BuildTool(settings, new BuildExecutor());
                break;
            default: {
                throw new RuntimeException("Unknown tool: " + toolName);
            }
        }
        return tool;
    }

    private ToolSettings buildSettings(YamlMapping toolYaml) {
        ToolSettings settings = new ToolSettings();

        String toolLocationString = toolYaml.string("tool_location");
        if (toolLocationString != null) {
            settings.setToolLocation(new File(toolLocationString));
        }

        String configLocationString = toolYaml.string("config_location");
        if (configLocationString != null) {
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
        return Yaml.createYamlMappingBuilder()
                .add("tool_location", settings.toolLocationProperty.get().getAbsolutePath())
                .add("config_location", settings.configurationProperty.get().getAbsolutePath())
                .add("priority", String.valueOf(settings.getPriority()))
                .add("enabled", String.valueOf(settings.getEnabled()))
                .build();
    }

    public void clearProjects() {
        projects.clear();
    }

    public Optional<Project> add(final Project project) {
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

    public void remove(Project project) {
        projects.remove(project);
    }

    public ObservableList<Project> getProjects() {
        return projects.getReadOnlyProperty();
    }

    /**
     * @return the lass accessed {@link Project}, or null if no projects have been created
     */
    public Optional<Project> getLastAccessed() {
        return projects.stream().max(Comparator.comparing(Project::getLastAccessed));
    }
}
