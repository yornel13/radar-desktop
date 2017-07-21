package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import util.HibernateSessionFactory;

public class StartApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sync.fxml"));
        primaryStage.setTitle("Panel de Control");
        String css = StartApp.class.getResource("style.css").toExternalForm();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            HibernateSessionFactory.closeSession();
            Platform.exit();
            System.exit(0);
        });
    }
}
