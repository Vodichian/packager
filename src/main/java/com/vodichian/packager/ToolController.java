package com.vodichian.packager;

import com.vodichian.packager.tool.AbstractTool;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class ToolController {
    /**
     * The {@link AbstractTool} this controller is controlling.
     */
    private AbstractTool tool;

    /**
     * This name of the tool
     */
    @FXML
    private Label nameLabel;
    /**
     * Visual indicator for correct tool existence
     */
    @FXML
    private Sphere toolSphere;
    /**
     * Visual indicator for existence of correct tool configuration
     */
    @FXML
    private Sphere configSphere;

    /**
     * The view this controller is controlling
     */
    @FXML
    private void initialize() {
        setSphereColor(toolSphere, Color.RED);
        setSphereColor(configSphere, Color.RED);
    }

    public void setTool(AbstractTool tool) {
        this.tool = tool;
        nameLabel.setText(tool.getSettings().getName().toString());
        monitorToolStatus();
    }

    @FXML
    private void run() {
        System.out.println("Run was clicked");
    }

    @FXML
    private void config() {
        System.out.println("Config was clicked");
    }

    private void monitorToolStatus() {
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

    private void setSphereColor(Sphere sphere, Color color) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(Color.BLACK);
        sphere.setMaterial(material);
    }

}