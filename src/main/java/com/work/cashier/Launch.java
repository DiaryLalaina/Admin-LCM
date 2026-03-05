package com.work.cashier;

import com.work.cashier.burningwave.AllModulesToAllModulesExporter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class Launch extends Application {
    private final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    private final int height = (int)dimension.getHeight();
    private final int width  = (int)dimension.getWidth();
    @Override
    public void start(Stage stage) throws IOException {
        AllModulesToAllModulesExporter.execute();
        FXMLLoader fxmlLoader = new FXMLLoader(Launch.class.getResource("main/mainContainer.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),width-100, height-150);
        stage.setTitle("i Dev Appli");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}