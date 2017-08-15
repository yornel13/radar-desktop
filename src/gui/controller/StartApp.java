package gui.controller;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.AppDecorator;
import util.HibernateSessionFactory;

import java.io.FileInputStream;

public class StartApp extends Application {

    static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Flow flow = new Flow(MainController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", primaryStage);
        flow.createHandler(flowContext).start(container);

        AppDecorator decorator = new AppDecorator(primaryStage,  container.getView(),
                false, false , true);
        decorator.setFocusTraversable(false);
        String css = StartApp.class.getResource("../style/style.css").toExternalForm();
        Scene scene = new Scene(decorator);
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(new FileInputStream("src/img/radar_splash.png")));
        stage = primaryStage;
        decorator.setOnCloseButtonAction(() -> {
            HibernateSessionFactory.closeSession();
            Platform.exit();
            System.exit(0);
        });
        loadHibernate();
    }

    private void loadHibernate() {
        try {
            HibernateSessionFactory.getConfiguration().configure();
        } catch (Exception e) {
            System.out.println("Error to load hibernate config");
        }
    }
}
