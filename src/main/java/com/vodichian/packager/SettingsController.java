package com.vodichian.packager;

import com.vodichian.packager.tool.ToolFactory;
import com.vodichian.packager.tool.ToolName;
import com.vodichian.packager.tool.ToolSettings;
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
    private ToolSettings settings;

    @FXML
    private void initialize() {
        toolTextField.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() >= 2) {
                File file;
                try {
                    file = displayFileChooser(settings, "Select location for the tool");
                } catch (PackagerException e) {
                    throw new RuntimeException(e);
                }
                if (file != null) {
                    settings.setToolLocation(file);
                    try {
                        ToolFactory.save(settings);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        configTextField.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() >= 2) {
                File file;
                try {
                    file = displayFileChooser(settings, "Select location for the configuration");
                } catch (PackagerException e) {
                    throw new RuntimeException(e);
                }
                if (file != null) {
                    settings.setConfiguration(file);
                    try {
                        ToolFactory.save(settings);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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

    public void load(ToolSettings settings) {
        this.settings = settings;
        if (settings.getName().equals(ToolName.BUILD_EXTRACTOR)) {
            nameLabel.setText(ToolName.BUILD_EXTRACTOR.name() + " Settings");
            toolTextField.setDisable(true);
            configTextField.setDisable(true);
        } else {
            toolTextField.setDisable(false);
            configTextField.setDisable(false);
            nameLabel.setText(settings.getName() + " Settings");
            File toolFile = settings.getToolLocation();
            if (toolFile != null) {
                toolTextField.setText(settings.getToolLocation().getAbsolutePath());
            }

            File configFile = settings.getConfiguration();
            if (configFile != null) {
                configTextField.setText(settings.getConfiguration().getAbsolutePath());
            }
        }
        settings.toolLocationProperty
                .addListener(observable ->
                        toolTextField.setText(settings.toolLocationProperty.get().getAbsolutePath()));

        settings.configurationProperty.addListener(observable ->
                configTextField.setText(settings.configurationProperty.get().getAbsolutePath()));
    }

    private File displayFileChooser(ToolSettings settings, String title) throws PackagerException {
        Collection<FileChooser.ExtensionFilter> filters = ToolFactory.getFilters(settings);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(filters);
        return fileChooser.showOpenDialog(nameLabel.getScene().getWindow());
    }
}
