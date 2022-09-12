module com.vodichian.packager {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.vodichian.packager to javafx.fxml;
    exports com.vodichian.packager;
    exports com.vodichian.packager.tool;
    opens com.vodichian.packager.tool to javafx.fxml;
}
