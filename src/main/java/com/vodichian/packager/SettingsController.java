package com.vodichian.packager;

import com.vodichian.packager.tool.AbstractTool;
import com.vodichian.packager.tool.ToolName;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class SettingsController {
    @FXML
    private Label nameLabel;
    @FXML
    private TextField toolTextField;
    @FXML
    private TextField configTextField;
    private AbstractTool tool;

    @FXML
    private void initialize() {
        toolTextField.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() >= 2) {
                File file = displayFileChooser(tool, "Select location for the tool");
                if (file != null) {
                    try {
                        tool.setTool(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    toolTextField.setText(file.getAbsolutePath());
                }
            }
        });
        configTextField.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() >= 2) {
                File file = displayFileChooser(tool, "Select location for the configuration");
                if (file != null) {
                    try {
                        tool.setConfiguration(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    configTextField.setText(file.getAbsolutePath());
                }
            }
        });
    }

    @FXML
    private void back() {
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load(AbstractTool tool) {
        this.tool = tool;
        if (tool.getSettings().getName().equals(ToolName.BUILD_EXTRACTOR)) {
            nameLabel.setText(ToolName.BUILD_EXTRACTOR.name() + " Settings");
            toolTextField.setDisable(true);
            configTextField.setDisable(true);
        } else {
            toolTextField.setDisable(false);
            configTextField.setDisable(false);
            nameLabel.setText(tool.getSettings().getName() + " Settings");
            File toolFile = tool.getSettings().getToolLocation();
            if (toolFile != null) {
                toolTextField.setText(tool.getSettings().getToolLocation().getAbsolutePath());
            }

            File configFile = tool.getSettings().getConfiguration();
            if (configFile != null) {
                configTextField.setText(tool.getSettings().getConfiguration().getAbsolutePath());
            }
        }
    }

    private File displayFileChooser(AbstractTool tool, String title) {
        Collection<FileChooser.ExtensionFilter> filters = tool.getFilters();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(filters);
        return fileChooser.showOpenDialog(nameLabel.getScene().getWindow());
    }
}
