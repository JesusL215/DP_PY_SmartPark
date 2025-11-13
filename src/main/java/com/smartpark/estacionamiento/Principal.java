package com.smartpark.estacionamiento;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class Principal extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            String fxmlPath = "/com/smartpark/estacionamiento/view/MainDashboard.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("No se pudo encontrar el archivo FXML: " + fxmlPath);
            }
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);
            primaryStage.setTitle("Sistema de Gestión de Estacionamiento");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}