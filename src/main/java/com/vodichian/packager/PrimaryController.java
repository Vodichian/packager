package com.vodichian.packager;

import com.vodichian.packager.projects.Project;
import com.vodichian.packager.projects.ProjectsController;
import com.vodichian.packager.tool.ToolFactory;
import com.vodichian.packager.tool.ToolMessage;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
    private Button runButton;
    @FXML
    private ListView<ToolMessage> messageListView;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ToggleButton projectsToggleButton;

    private final Sequencer sequencer = new Sequencer();
    private Parent projectsView;
    private Project currentProject;

    @FXML
    private void initialize() {
        Model model = App.getModel();
        messageListView.itemsProperty().bind(model.messages);
        makeAutoScroll(messageListView);

        runButton.disableProperty().bind(sequencer.readyProperty.not());
        try {
            sequencer.setTools(ToolFactory.tools());
        } catch (IOException | PackagerException e) {
            System.err.println(e.getMessage());
            post("Failed to set tools in sequencer: " + e.getMessage());
            throw new RuntimeException(e);
        }

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
        projectsController.setPrimary(this);
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

    public void setProject(Project project) throws PackagerException, IOException {
        if (project == null || currentProject == project) {
            post("setProject> was either null or unchanged");
            return;
        }
        post("Changing to project " + project.getName());
        this.currentProject = project;
        List<Parent> views = ToolFactory.toolViews(currentProject);
        toolVBox.getChildren().clear();
        toolVBox.getChildren().addAll(views);
    }
}
