package gui.controller;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.HibernateSessionFactory;

public class StartApp extends Application {

    static Stage stage = new Stage();

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
        flowContext.register("Stage", stage);
        flow.createHandler(flowContext).start(container);

        JFXDecorator decorator = new JFXDecorator(primaryStage,  container.getView(), false, false , true);
        String css = StartApp.class.getResource("../style/style.css").toExternalForm();
        Scene scene = new Scene(decorator);
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        primaryStage.setResizable(false);
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
