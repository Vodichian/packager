package com.vodichian.packager;

import com.vodichian.packager.projects.Project;
import com.vodichian.packager.projects.ProjectsController;
import com.vodichian.packager.tool.ToolFactory;
import com.vodichian.packager.tool.ToolMessage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

public class PrimaryController implements CloseListener {
    @FXML
    private VBox toolVBox;
    @FXML
    private TitledPane titledPane;
    @FXML
    private Button runButton;
    @FXML
    private ListView<ToolMessage> messageListView;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ToggleButton projectsToggleButton;

    private final Sequencer sequencer = new Sequencer();
    private Parent projectsView;
    private final ObjectProperty<Project> currentProjectProperty = new SimpleObjectProperty<>();

    @FXML
    private void initialize() {
        Model model = App.getModel();
        messageListView.itemsProperty().bind(model.messages);
        makeAutoScroll(messageListView);

        runButton.disableProperty().bind(sequencer.readyProperty.not());
        // TODO: 10/27/2022 review and refactor the sequencer usage with regards to new Project architecture
//        sequencer.setTools(currentProject.getTools());


        // install ProjectsController and view
        try {
            projectsToggleButton.setSelected(true);
            installProjectsUI();
        } catch (IOException e) {
            post("Failed to install Projects UI: " + e.getMessage());
            throw new RuntimeException(e);
        }

        projectsToggleButton.selectedProperty()
                .addListener(observable -> toggleProjects(projectsToggleButton.isSelected()));
    }

    private void toggleProjects(boolean selected) {
        if (!selected) {
            mainBorderPane.leftProperty().set(null);
        } else {
            mainBorderPane.leftProperty().set(projectsView);
        }
    }

    private void installProjectsUI() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("projects/projects.fxml"));
        projectsView = fxmlLoader.load();
        ProjectsController projectsController = fxmlLoader.getController();
        currentProjectProperty.bind(projectsController.currentProject);
        currentProjectProperty.addListener(projectChangeListener);
        setProject(currentProjectProperty.get());
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
    public void onRun() {
        try {
            sequencer.runSequence();
        } catch (PackagerException e) {
            post("Sequencer failed its run: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private final ChangeListener<Project> projectChangeListener = (observableValue, p1, p2) -> {
        Project project = (Project) ((ObjectProperty<?>) observableValue).get();
        setProject(project);
    };

    private void setProject(Project project) {
        toolVBox.getChildren().clear();
        if (project == null) {
            post("setProject> was either null or unchanged");
            return;
        }
        post("Changing to project " + project.getName());
        List<Parent> views = ToolFactory.toolViews(project);
        toolVBox.getChildren().addAll(views);
        titledPane.setText(project.getName());
    }
}
