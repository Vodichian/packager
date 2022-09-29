package com.vodichian.packager;

import com.vodichian.packager.tool.ToolFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class PrimaryController {
    @FXML
    private VBox toolVBox;


    @FXML
    private void initialize() {
        try {
            displayTools();
        } catch (PackagerException | IOException e) {
            throw new RuntimeException(e);
        }
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
}
