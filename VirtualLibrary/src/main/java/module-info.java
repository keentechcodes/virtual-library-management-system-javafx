module com.smart.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;
    requires javafx.web;

    opens com.smart.javafx to javafx.fxml;
    exports com.smart.javafx;
}