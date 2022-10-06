package com.vodichian.packager;

import com.vodichian.packager.tool.ToolFactory;
import com.vodichian.packager.tool.ToolMessage;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class PrimaryController implements CloseListener {
    @FXML
    private VBox toolVBox;
    @FXML
    private ListView<ToolMessage> messageListView;

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
}
