package com.vodichian.packager.projects;

import com.vodichian.packager.PackagerException;
import com.vodichian.packager.tool.ToolMessage;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
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
    private MenuItem newProjectMenuItem;
    @FXML
    private MenuItem deleteProjectMenuItem;
    @FXML
    private MenuItem renameProjectMenuItem;

    public ReadOnlyObjectProperty<Project> currentProject;

    @FXML
    public void initialize() {
        // install MenuItems
        newProjectMenuItem.setOnAction(this::onNewProject);
        renameProjectMenuItem.setOnAction(this::onRenameProject);
        currentProject = projectsListView.getSelectionModel().selectedItemProperty();

        ProjectManager pm = ProjectManager.getInstance();
        projectsListView.setCellFactory(new ProjectCellFactory());
        projectsListView.setItems(pm.getProjects());
        pm.getLastAccessed().ifPresent(project -> projectsListView.getSelectionModel().select(project));
    }

    private void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }

    private void onNewProject(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Project");
        dialog.setContentText("Enter project's name");
        dialog.setHeaderText(null);
        dialog.showAndWait().ifPresent(this::createAndAdd);
    }

    private void onRenameProject(ActionEvent actionEvent) {
        if (currentProject.get() == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename Project");
        dialog.setContentText("Enter new name:");
        dialog.setHeaderText("Rename project \"" + currentProject.get().getName() + "\"");
        dialog.showAndWait().ifPresent(this::rename);
    }

    private void rename(String name) {
        post("Renaming \"" + currentProject.get().getName() + "\" to \"" + name + "\"");
        Project project = currentProject.get();
        project.setName(name);
        try {
            ProjectManager pm = ProjectManager.getInstance();
            project = pm.save(project);
            projectsListView.getSelectionModel().select(project);

        } catch (IOException | PackagerException e) {
            post("Failed to save renamed project:" + e.getMessage());
        }

    }

    /**
     * Create and add a new project using the given project's name
     *
     * @param name project's name
     */
    private void createAndAdd(String name) {
        if (name.isBlank()) return;
        ProjectManager pm = ProjectManager.getInstance();

        pm.add(new Project((name)))
                .ifPresent(project -> {
                    try {
                        pm.save();
                    } catch (IOException e) {
                        post("Failed to save new project: " + e);
                        throw new RuntimeException(e);
                    }
                    projectsListView.getSelectionModel().select(project);
                });
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
