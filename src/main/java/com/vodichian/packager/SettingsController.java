package com.vodichian.packager;

import javafx.fxml.FXML;

import java.io.IOException;

public class SettingsController {

    @FXML
    private void initialize() {
    }

    @FXML
    private void back() {
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
