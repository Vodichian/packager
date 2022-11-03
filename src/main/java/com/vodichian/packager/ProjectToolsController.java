package com.vodichian.packager;

import com.vodichian.packager.projects.Project;
import com.vodichian.packager.projects.ProjectManager;
import com.vodichian.packager.tool.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
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
    private ListView<ToolController> projectListView;
    private final ObjectProperty<Project> projectProperty = new SimpleObjectProperty<>();
    @FXML
    private Pane topPane;

    @FXML
    public void initialize() {
        removeToolButton.disableProperty().bind(projectListView.getSelectionModel().selectedItemProperty().isNull());
        removeToolButton.setOnAction(this::onRemove);
        topPane.visibleProperty().bind(projectProperty.isNotNull());
        projectListView.setCellFactory(new ToolCellFactory());
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
        AbstractTool toolToRemove = projectListView.getSelectionModel().getSelectedItem().getTool();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Remove \"" + toolToRemove.getSettings().getName() + "\" from this project?");
        alert.setHeaderText("Confirm tool removal");
        alert.setTitle("Remove tool");
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                post("Removing " + toolToRemove.getSettings().getName() + " from the project");
                projectProperty.get().remove(toolToRemove);
                try {
                    ProjectManager.getInstance().save();
                    update();
                } catch (IOException e) {
                    post("Save failed: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void setProject(Project project) {
        if (project == null) {
            projectProperty.set(null);
        } else {
            projectListView.getItems().clear();
            post("Setting project " + project.getName());
            List<ToolController> views = ToolFactory.toolViews(project);
            projectListView.getItems().addAll(views);
            projectNameLabel.setText(project.getName());
            projectProperty.set(project);
        }
    }

    protected void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }

    private static class ToolCellFactory implements Callback<ListView<ToolController>, ListCell<ToolController>> {
        @Override
        public ListCell<ToolController> call(ListView<ToolController> projectListView) {
            return new ListCell<>() {
                @Override
                public void updateItem(ToolController controller, boolean empty) {
                    super.updateItem(controller, empty);
                    if (empty || controller == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(null);
                        setGraphic(controller.getParent());
                    }
                }
            };
        }
    }

}
