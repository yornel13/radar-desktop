package gui.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.HibernateSessionFactory;

public class StartApp extends Application {

    static Stage stage = new Stage();
    @Override
    public  void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../view/sync.fxml"));
        primaryStage.setTitle("Panel de Control");
        String css = StartApp.class.getResource("../style/style.css").toExternalForm();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;
        primaryStage.setOnCloseRequest(event -> {
            HibernateSessionFactory.closeSession();
            Platform.exit();
            System.exit(0);
        });
    }
}
