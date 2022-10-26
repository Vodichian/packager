package com.vodichian.packager.projects;

import com.vodichian.packager.PackagerException;
import com.vodichian.packager.PrimaryController;
import com.vodichian.packager.tool.ToolMessage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * Controller for the FXML view "projects.fxml"
 */
public class ProjectsController {
    @FXML
    public ListView<Project> projectsListView;
    @FXML
    private MenuButton operationsMenuButton;
    @FXML
    private MenuItem newProjectMenuItem;
    @FXML
    private MenuItem loadProjectMenuItem;
    @FXML
    private MenuItem deleteProjectMenuItem;
    @FXML
    private MenuItem renameProjectMenuItem;

    private PrimaryController primaryController;

    @FXML
    public void initialize() {
        // install MenuItems
        newProjectMenuItem.setOnAction(actionEvent -> onNewProject());
    }

    /**
     * Called when a change to project selection is detected.
     */
    private void onProjectSelection() {
        Project selected = projectsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                primaryController.setProject(selected);
            } catch (PackagerException | IOException e) {
                post("Failed to set project: " + selected.getName() + " : " + e);
                throw new RuntimeException(e);
            }
        }
    }

    private void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }

    private void onNewProject() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Project");
        dialog.setContentText("Enter project's name");
        dialog.setHeaderText(null);
        dialog.showAndWait().ifPresent(this::createAndAdd);
    }

    /**
     * Create and add a new project using the given project's name
     *
     * @param name project's name
     */
    private void createAndAdd(String name) {
        if (name.isBlank()) return;

        ProjectManager.getInstance().add(new Project((name)))
                .ifPresent(project -> projectsListView.getSelectionModel().select(project));
    }

    public void setPrimary(PrimaryController primaryController) {
        this.primaryController = primaryController;
        ProjectManager pm = ProjectManager.getInstance();
        projectsListView.setCellFactory(new ProjectCellFactory());
        projectsListView.setItems(pm.getProjects());
        projectsListView.getSelectionModel().selectedItemProperty().addListener((observable -> onProjectSelection()));
        pm.getLastAccessed().ifPresent(project -> projectsListView.getSelectionModel().select(project));
    }

    private static class ProjectCellFactory implements Callback<ListView<Project>, ListCell<Project>> {

        @Override
        public ListCell<Project> call(ListView<Project> projectListView) {
            return new ListCell<>() {
                @Override
                public void updateItem(Project project, boolean empty) {
                    super.updateItem(project, empty);
                    if (empty || project == null) {
                        setText(null);
                    } else {
                        setText(project.getName());
                    }
                }
            };
        }
    }
}
