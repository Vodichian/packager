package com.vodichian.packager;

import com.vodichian.packager.projects.ProjectManager;
import com.vodichian.packager.tool.ToolFactory;
import com.vodichian.packager.tool.ToolName;
import com.vodichian.packager.tool.ToolSettings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    @FXML
    private Button toolButton;
    @FXML
    private Button configButton;
    @FXML
    private TextField priorityTextField;
    @FXML
    private ChoiceBox<Boolean> enableChoiceBox;

    private ToolSettings settings;
    private final ProjectManager projectManager = ProjectManager.getInstance();

    @FXML
    private void initialize() {

        toolButton.setOnAction(actionEvent -> {
            File file;
            try {
                file = displayExeFileChooser();
            } catch (PackagerException e) {
                throw new RuntimeException(e);
            }
            if (file != null) {
                settings.setToolLocation(file);
                try {
                    projectManager.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        configButton.setOnAction(actionEvent -> {
            File file;
            try {
                file = displayConfigFileChooser(settings);
            } catch (PackagerException e) {
                throw new RuntimeException(e);
            }
            if (file != null) {
                settings.setConfiguration(file);
                try {
                    projectManager.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        priorityTextField.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if (text.isBlank()) return change;
            try {
                Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return null;
            }
            return change;
        }));

        priorityTextField.textProperty().addListener(observable -> {
            String text = priorityTextField.getText();
            if (text.isBlank()) {
                return;
            }
            settings.setPriority(Integer.parseInt(text));
            try {
                projectManager.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        enableChoiceBox.getItems().addAll(Boolean.TRUE, Boolean.FALSE);
        enableChoiceBox.getSelectionModel().selectedItemProperty().addListener(observable -> {
            Boolean enabled = enableChoiceBox.getSelectionModel().getSelectedItem();
            settings.setEnabled(enabled);
            try {
                projectManager.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
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
            toolButton.setDisable(true);
        } else {
            toolTextField.setDisable(false);
            configTextField.setDisable(false);
            toolButton.setDisable(false);
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

        int priority = settings.getPriority();
        priorityTextField.setText(String.valueOf(priority));
        enableChoiceBox.getSelectionModel().select(settings.getEnabled());
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
