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

public class SettingsController implements CloseListener {
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
                    file = displayExeFileChooser();
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
                    file = displayConfigFileChooser(settings);
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
            App.displayPrimary();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load(ToolSettings settings) {
        this.settings = settings;
        if (settings.getName().equals(ToolName.BUILD_EXTRACTOR)) {
            nameLabel.setText(ToolName.BUILD_EXTRACTOR.name() + " Settings");
            toolTextField.setDisable(true);
        } else {
            toolTextField.setDisable(false);
            configTextField.setDisable(false);
            nameLabel.setText(settings.getName() + " Settings");
            File toolFile = settings.getToolLocation();
            if (toolFile != null) {
                toolTextField.setText(settings.getToolLocation().getAbsolutePath());
            }

        }
        File configFile = settings.getConfiguration();
        if (configFile != null) {
            configTextField.setText(settings.getConfiguration().getAbsolutePath());
        }
        settings.toolLocationProperty
                .addListener(observable ->
                        toolTextField.setText(settings.toolLocationProperty.get().getAbsolutePath()));

        settings.configurationProperty.addListener(observable ->
                configTextField.setText(settings.configurationProperty.get().getAbsolutePath()));
    }

    private File displayConfigFileChooser(ToolSettings settings) throws PackagerException {
        Collection<FileChooser.ExtensionFilter> filters = ToolFactory.getFilters(settings);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Select location for the configuration");
        fileChooser.getExtensionFilters().addAll(filters);
        return fileChooser.showOpenDialog(nameLabel.getScene().getWindow());
    }

    private File displayExeFileChooser() throws PackagerException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Select location for the tool");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Executable Files", "*.exe"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All", "*.*"));
        return fileChooser.showOpenDialog(nameLabel.getScene().getWindow());
    }

    @Override
    public void onClose() {
        // unused
    }
}
