package ru.zakhse.jamming;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.zakhse.jamming.controllers.JammingController;
import ru.zakhse.jamming.lattice.ExperimentProperties;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Jamming extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String bundlePath = "localizations/Jamming";
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle(bundlePath, Locale.getDefault());
        } catch (MissingResourceException e) {
            bundle = ResourceBundle.getBundle(bundlePath, Locale.ENGLISH);
        }

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/mainWindow.fxml"));
        fxmlLoader.setResources(bundle);

        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Jamming");
        Scene scene = new Scene(root, 650, 450);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(410);
        primaryStage.setMinWidth(450);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            ((JammingController) fxmlLoader.getController()).stopAction();
            ExperimentProperties.getInstance().saveSettings();
        });
    }

    public static void main() {
        launch();
    }
}
