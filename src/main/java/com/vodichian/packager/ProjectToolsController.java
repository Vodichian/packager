package com.vodichian.packager;

import com.vodichian.packager.projects.Project;
import com.vodichian.packager.tool.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectToolsController {

    @FXML
    private Label projectNameLabel;
    @FXML
    private MenuButton addToolMenuButton;
    @FXML
    private Button removeToolButton;
    @FXML
    private VBox toolVBox;
    private final ObjectProperty<Project> projectProperty = new SimpleObjectProperty<>();
    @FXML
    private Pane topPane;

    @FXML
    public void initialize() {
        removeToolButton.setOnAction(this::onRemove);
        topPane.visibleProperty().bind(projectProperty.isNotNull());
        initAddToolMenuButton();
    }

    private void initAddToolMenuButton() {
        addToolMenuButton.getItems().clear();
        List<MenuItem> menuItems = Arrays.stream(ToolName.values())
                .map(this::toMenuItem)
                .collect(Collectors.toList());
        addToolMenuButton.getItems().addAll(menuItems);
    }

    private MenuItem toMenuItem(ToolName name) {
        return new ToolMenuItem(name);
    }

    private class ToolMenuItem extends MenuItem {
        private final ToolName toolName;

        private ToolMenuItem(ToolName toolName) {
            super(toolName.toString());
            this.toolName = toolName;
            setOnAction(this::onAction);
        }

        private void onAction(ActionEvent actionEvent) {
            Project project = projectProperty.get();
            switch (toolName) {
                case INNO_SETUP:
                    project.add(new InnoTool(new ToolSettings().setName(toolName), new InnoExecutor()));
                    break;
                case LAUNCH_4_J:
                    project.add(new Launch4jTool(new ToolSettings().setName(toolName), new LaunchExecutor()));
                    break;
                case BUILD_EXTRACTOR:
                    project.add(new BuildTool(new ToolSettings().setName(toolName), new BuildExecutor()));
                    break;
            }
            update();
        }
    }

    private void update() {
        setProject(projectProperty.get());
    }

    private void onRemove(ActionEvent actionEvent) {
        System.out.println("onRemove called");
    }

    public void setProject(Project project) {
        if (project == null) {
            projectProperty.set(null);
        } else {
            toolVBox.getChildren().clear();
            post("Setting project " + project.getName());
            List<Parent> views = ToolFactory.toolViews(project);
            toolVBox.getChildren().addAll(views);
            projectNameLabel.setText(project.getName());
            projectProperty.set(project);
        }
    }

    protected void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }
}
