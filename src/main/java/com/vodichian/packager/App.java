package com.vodichian.packager;

import com.vodichian.packager.tool.ToolSettings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static CloseListener currentController;

    private static Model model;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
        Parent parent = fxmlLoader.load();
        scene = new Scene(parent, 640, 480);
        stage.setScene(scene);
        currentController = fxmlLoader.getController();
        stage.show();
    }

    static void displaySettings(ToolSettings settings) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("settings.fxml"));
        Parent parent = fxmlLoader.load();
        scene.setRoot(parent);
        SettingsController controller = fxmlLoader.getController();
        if (currentController != null) currentController.onClose();
        currentController = controller;
        controller.load(settings);

    }

    static void displayPrimary() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
        Parent parent = fxmlLoader.load();
        scene.setRoot(parent);
        if (currentController != null) currentController.onClose();
        currentController = fxmlLoader.<PrimaryController>getController();

    }

    public static Model getModel() {
        return model;
    }

    public static void main(String[] args) {
        model = new Model();
        launch();
    }

}