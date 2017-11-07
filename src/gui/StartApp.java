package gui;

import com.jfoenix.controls.JFXDialog;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import util.AppDecorator;
import util.HibernateSessionFactory;

import java.io.IOException;

public class StartApp extends Application {

    static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    private DefaultFlowContainer container;
    private AppDecorator decorator;

    @Override
    public void start(Stage primaryStage) throws Exception {

        container = new DefaultFlowContainer();
        decorator = new AppDecorator(primaryStage,  container.getView(),
                false, false , true);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/login.fxml"));
        StackPane rootPane = loader.load();
        Scene scene = new Scene(rootPane);
        String css = StartApp.class.getResource("style/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.getIcons().add(new Image(getClass().getResource("img/radar_splash.png").toExternalForm()));
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        LoginController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        controller.setStartApp(this);
        Platform.setImplicitExit(false);
        primaryStage.show();

        loadHibernate();
    }

    public void initApp(Stage primaryStage) throws FlowException {
        Flow flow = new Flow(MainController.class);
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", primaryStage);
        flow.createHandler(flowContext).start(container);
        String css = StartApp.class.getResource("style/style.css").toExternalForm();
        Scene scene = new Scene(decorator);
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setHeight(620);
        primaryStage.setWidth(865);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass()
                .getResource("img/radar_splash.png").toExternalForm()));
        stage = primaryStage;
        decorator.setOnCloseButtonAction(() -> {
            closeApp();
        });
    }

    public void closeApp() {
        try {
            closeDialog();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void closeDialog() throws IOException {

        StackPane root = (StackPane) flowContext.getRegisteredObject("root");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/dialog.fxml"));
        JFXDialog dialog = loader.load();
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.show(root);
    }

    private void loadHibernate() {
        try {
            HibernateSessionFactory.getConfiguration().configure();
        } catch (Exception e) {
            System.out.println("Error to load hibernate config");
        }
    }
}
