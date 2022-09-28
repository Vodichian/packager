package com.vodichian.packager;

import com.vodichian.packager.tool.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static javafx.stage.FileChooser.ExtensionFilter;

public class PrimaryController {
    private static final ExtensionFilter XML_EXT = new ExtensionFilter("XML Files", "*.xml");
    private static final ExtensionFilter ISS_EXT = new ExtensionFilter("Inno Config Files", "*.iss");
    private static final ExtensionFilter ALL_EXT = new ExtensionFilter("All Files", "*.*");
    private static final Collection<ExtensionFilter> LAUNCH4J_FILTERS = Arrays.asList(XML_EXT, ALL_EXT);
    private static final Collection<ExtensionFilter> INNO_FILTERS = Arrays.asList(ISS_EXT, ALL_EXT);

    private AbstractTool launch4jTool;
    private AbstractTool innoTool;

    @FXML
    private Sphere launch4jSphere;
    @FXML
    private Sphere innoSphere;
    @FXML
    private Sphere launchConfigSphere;
    @FXML
    private Sphere innoConfigSphere;
    @FXML
    private TextField launch4jTextField;
    @FXML
    private TextField innoTextField;

    @FXML
    private void initialize() {
        launch4jTool = new Launch4jTool(new ToolSettings());
        innoTool = new InnoTool(new ToolSettings());

        setSphereColor(launch4jSphere, Color.RED);
        setSphereColor(innoSphere, Color.RED);

        initTool(launch4jTextField, launch4jTool, LAUNCH4J_FILTERS, launchConfigSphere, launch4jSphere);
        initTool(innoTextField, innoTool, INNO_FILTERS, innoConfigSphere, innoSphere);

    }

    private void initTool(
            TextField textField,
            AbstractTool tool,
            Collection<ExtensionFilter> filters,
            Sphere configSphere,
            Sphere toolSphere) {
        textField.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() >= 2) {
                displayFileChooser(tool, filters);
            }
        });
        tool.configuration().addListener((observableValue, file, t1) -> {
            textField.setText(t1.getAbsolutePath());
        });

        monitorToolStatus(tool, configSphere, toolSphere);
    }

    private void monitorToolStatus(AbstractTool tool, Sphere configSphere, Sphere toolSphere) {

        tool.toolIsValid().addListener((observableValue, aBoolean, t1) -> processStatusChange(t1, toolSphere));
        tool.configIsValid().addListener((observableValue, aBoolean, t1) -> processStatusChange(t1, configSphere));
    }

    private void processStatusChange(Boolean valid, Sphere sphere) {
        if (valid) {
            setSphereColor(sphere, Color.LIGHTGREEN);
        } else {
            setSphereColor(sphere, Color.RED);
        }
    }

    private void displayFileChooser(AbstractTool tool, Collection<ExtensionFilter> filters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Configuration File");
        fileChooser.getExtensionFilters().addAll(filters);
        File selectedFile = fileChooser.showOpenDialog(innoTextField.getScene().getWindow());
        tool.setConfiguration(selectedFile);
    }

    private void setSphereColor(Sphere sphere, Color color) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(Color.BLACK);
        sphere.setMaterial(material);
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void onExit() {
        System.exit(0);
    }
}
