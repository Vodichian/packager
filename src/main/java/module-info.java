module com.vodichian.packager {
    requires javafx.controls;
    requires javafx.fxml;
    requires eventbus.java;
    requires maven.model; // extracts version from pom.xml
    requires plexus.utils;
    requires com.amihaiemil.eoyaml;
    requires java.desktop; // required for maven-model

    opens com.vodichian.packager to javafx.fxml;
    exports com.vodichian.packager;
    exports com.vodichian.packager.tool;
    opens com.vodichian.packager.tool to javafx.fxml;
    exports com.vodichian.packager.projects;
    opens com.vodichian.packager.projects to javafx.fxml;
}
