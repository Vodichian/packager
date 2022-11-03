package com.vodichian.packager;

import com.vodichian.packager.tool.AbstractTool;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;

import java.io.IOException;

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
     * Displays the priority for this tool
     */
    @FXML
    private Label priorityLabel;
    /**
     * Displays the enabled status for this tool
     */
    @FXML
    private Sphere enabledSphere;
    private Parent parent;

    /**
     * The view this controller is controlling
     */
    @FXML
    private void initialize() {
        setSphereColor(toolSphere, Color.RED);
        setSphereColor(configSphere, Color.RED);
        setSphereColor(enabledSphere, Color.RED);

        Tooltip enableToolTip = new Tooltip("Tool Enable");
        enableToolTip.setShowDelay(Duration.millis(2));
        Tooltip.install(enabledSphere, enableToolTip);

        Tooltip toolToolTip = new Tooltip("Tool location");
        toolToolTip.setShowDelay(Duration.millis(2));
        Tooltip.install(toolSphere, toolToolTip);

        Tooltip configToolTip = new Tooltip("Tool configuration");
        configToolTip.setShowDelay(Duration.millis(2));
        Tooltip.install(configSphere, configToolTip);
    }

    public void setTool(AbstractTool tool) {
        this.tool = tool;
        this.priorityLabel.setText(String.valueOf(tool.getSettings().getPriority()));
        nameLabel.setText(tool.getSettings().getName().toString());
        monitorToolStatus();
    }

    public AbstractTool getTool() {
        return tool;
    }

    @FXML
    private void run() {
        try {
            tool.execute();
        } catch (PackagerException e) {
            System.err.println("Run failure: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void config() {
        // switch to settings view using this ToolSetting
        try {
            App.displaySettings(tool.getSettings());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void monitorToolStatus() {
        tool.toolIsValid().addListener((observableValue, aBoolean, t1) -> processStatusChange(t1, toolSphere));
        processStatusChange(tool.toolIsValid().get(), toolSphere);
        tool.configIsValid().addListener((observableValue, aBoolean, t1) -> processStatusChange(t1, configSphere));
        processStatusChange(tool.configIsValid().get(), configSphere);
        tool.getSettings().priorityProperty.addListener(observable -> priorityLabel.setText(String.valueOf(tool.getSettings().getPriority())));
        tool.getSettings().enabledProperty.addListener((observableValue, aBoolean, t1) -> processStatusChange(t1, enabledSphere));
        processStatusChange(tool.getSettings().enabledProperty.get(), enabledSphere);
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

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Parent getParent() {
        return parent;
    }
}