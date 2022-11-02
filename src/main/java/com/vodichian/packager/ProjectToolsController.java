package com.vodichian.packager;

import com.vodichian.packager.projects.Project;
import com.vodichian.packager.projects.ProjectManager;
import com.vodichian.packager.tool.ToolFactory;
import com.vodichian.packager.tool.ToolMessage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ProjectToolsController {

    @FXML
    private Label projectNameLabel;
    @FXML
    private Button addToolButton;
    @FXML
    private Button removeToolButton;
    @FXML
    private VBox toolVBox;

    public void setProject(Project project) {
        if(project == null) return;
        ProjectManager.getInstance().find(project.getName())
                .ifPresentOrElse(actual -> {
                    toolVBox.getChildren().clear();
                    post("Changing to project " + actual.getName());
                    List<Parent> views = ToolFactory.toolViews(actual);
                    toolVBox.getChildren().addAll(views);
                    projectNameLabel.setText(actual.getName());
                }, () -> post("Project was not found: " + project.getName()));
    }

    protected void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }
}
