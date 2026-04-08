module com.work.cashier {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires spring.security.crypto;
    requires annotations;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;
    requires org.json;
    requires java.desktop;
    requires com.jfoenix;
    requires org.kordamp.ikonli.javafx;
    requires org.controlsfx.controls;
    requires AnimateFX;
    requires static lombok;
    requires org.burningwave.core;
    requires slf4j.api;
    requires java.logging;
    requires spring.messaging;
    requires spring.websocket;
    requires itextpdf;
    requires org.apache.pdfbox;
    requires spring.context;
    requires spring.beans;
    requires jakarta.annotation;

    opens com.work.cashier to javafx.fxml;
    exports com.work.cashier;
}