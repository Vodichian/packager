package com.vodichian.packager;

import com.vodichian.packager.tool.ToolFactory;
import com.vodichian.packager.tool.ToolMessage;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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

    private final Sequencer sequencer = new Sequencer();

    @FXML
    private void initialize() {
        Model model = App.getModel();
        messageListView.itemsProperty().bind(model.messages);
        makeAutoScroll(messageListView);
        try {
            displayTools();
        } catch (PackagerException | IOException e) {
            throw new RuntimeException(e);
        }

        runButton.disableProperty().bind(sequencer.readyProperty.not());
        try {
            sequencer.setTools(ToolFactory.tools());
        } catch (IOException | PackagerException e) {
            System.err.println(e.getMessage());
            post("Failed to set tools in sequencer: " + e.getMessage());
            throw new RuntimeException(e);
        }
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

    private void displayTools() throws PackagerException, IOException {
        List<Parent> toolViews = ToolFactory.toolViews();
        toolVBox.getChildren().clear();
        toolVBox.getChildren().addAll(toolViews);
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
}
