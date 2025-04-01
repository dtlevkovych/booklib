module com.example.javafxprojrct {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires password4j;

    opens com.example.javafxprojrct to javafx.fxml;
    exports com.example.javafxprojrct;
}