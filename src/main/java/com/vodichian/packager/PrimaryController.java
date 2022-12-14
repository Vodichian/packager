package com.vodichian.packager;

import com.vodichian.packager.projects.Project;
import com.vodichian.packager.projects.ProjectsController;
import com.vodichian.packager.tool.ToolMessage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.time.LocalDateTime;

public class PrimaryController implements CloseListener {
    @FXML
    private VBox toolVBox;
    @FXML
    private ListView<ToolMessage> messageListView;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ToggleButton projectsToggleButton;

    private Parent projectsView;
    private ProjectToolsController projectToolsController;
    private final ObjectProperty<Project> currentProjectProperty = new SimpleObjectProperty<>();

    @FXML
    private void initialize() {
        Model model = App.getModel();
        messageListView.itemsProperty().bind(model.messages);
        makeAutoScroll(messageListView);

        // install ProjectsController and view
        try {
            projectsToggleButton.setSelected(true);
            installProjectsUI();
            installProjectToolsUI();
            setProject(currentProjectProperty.get());
        } catch (IOException e) {
            post("Failed to install UI element(s): " + e.getMessage());
            throw new RuntimeException(e);
        }

        projectsToggleButton.selectedProperty()
                .addListener(observable -> toggleProjects(projectsToggleButton.isSelected()));
    }

    private void installProjectToolsUI() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("project_tools.fxml"));
        Parent projectToolsView = fxmlLoader.load();
        projectToolsController = fxmlLoader.getController();
        toolVBox.getChildren().add(projectToolsView);
    }

    private void toggleProjects(boolean selected) {
        Stage stage = (Stage) toolVBox.getScene().getWindow();
        if (!selected) {
            mainBorderPane.leftProperty().set(null);
            stage.setWidth(640);
        } else {
            mainBorderPane.leftProperty().set(projectsView);
            stage.setWidth(800);
        }
    }

    private void installProjectsUI() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("projects/projects.fxml"));
        projectsView = fxmlLoader.load();
        ProjectsController projectsController = fxmlLoader.getController();
        currentProjectProperty.bind(projectsController.currentProject);
        currentProjectProperty.addListener(projectChangeListener);
        if (projectsToggleButton.isSelected()) mainBorderPane.leftProperty().set(projectsView);
    }

    protected void post(String message) {
        System.out.println(getClass().getSimpleName() + "> " + message);
        EventBus.getDefault().post(new ToolMessage(getClass().getSimpleName(), message));
    }

    private void makeAutoScroll(ListView<ToolMessage> listView) {
        listView.getItems().addListener(
                (ListChangeListener<? super ToolMessage>) change ->
                        listView.scrollTo(listView.getItems().size()));
    }

    @FXML
    private void onExit() {
        System.exit(0);
    }

    @Override
    public void onClose() {
        currentProjectProperty.unbind();
        currentProjectProperty.removeListener(projectChangeListener);
    }

    @FXML
    private final ChangeListener<Project> projectChangeListener = (observableValue, p1, p2) -> {
        Project project = (Project) ((ObjectProperty<?>) observableValue).get();
        if (p1 != null) {
            p1.setLastAccessed(LocalDateTime.now().minusSeconds(10));
        }
        if (p2 != null) {
            p2.setLastAccessed(LocalDateTime.now());
        }
        setProject(project);
    };

    private void setProject(Project project) {
        projectToolsController.setProject(project);
    }
}
