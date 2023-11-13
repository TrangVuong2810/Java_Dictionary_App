module main.dictionary_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens main to javafx.fxml;
    opens game to javafx.fxml;
    exports game;
    exports main;
    exports base;
}