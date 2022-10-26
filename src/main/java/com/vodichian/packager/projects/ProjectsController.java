package com.vodichian.packager.projects;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

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

    @FXML
    public void initialize() {
        ProjectManager pm = ProjectManager.getInstance();
        projectsListView.setCellFactory(new ProjectCellFactory());
        projectsListView.setItems(pm.getProjects());
        pm.getLastAccessed().ifPresent(project -> projectsListView.getSelectionModel().select(project));

        // install MenuItems
        newProjectMenuItem.setOnAction(actionEvent -> onNewProject());
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
