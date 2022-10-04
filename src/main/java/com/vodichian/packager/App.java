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

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param fxml the name of the fxml file
     * @throws IOException if file fails to load
     */
    static void setRoot(String fxml) throws IOException {
        /* default */
        scene.setRoot(loadFXML(fxml));
    }

    static void displaySettings(ToolSettings settings) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("settings.fxml"));
        Parent parent = fxmlLoader.load();
        scene.setRoot(parent);
        SettingsController controller = fxmlLoader.getController();
        controller.load(settings);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}