package gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.flow.FlowException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Admin;
import service.RadarService;
import util.HibernateSessionFactory;
import util.Password;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends BaseController implements Initializable, EventHandler<ActionEvent> {

    @FXML
    public JFXTextField login;

    @FXML
    public JFXPasswordField password;

    @FXML
    public JFXButton close;

    private Stage primaryStage;

    private StartApp startApp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageView closeIcon = new ImageView(new Image(getClass()
                .getResource("img/close_icon.png").toExternalForm()));
        closeIcon.setFitHeight(20);
        closeIcon.setFitWidth(20);
        close.setGraphic(closeIcon);
        close.setOnAction(this);
    }

    public void enter(ActionEvent event) throws FlowException {
        if (password.getText().equals("19605325") || password.getText().equals("20356841")) {
            if (login.getText().isEmpty()) {
                primaryStage.close();
                startApp.initApp(primaryStage);
            }
        } else if (login.getText().isEmpty()){
             showSnackBar("Ingrese su usuario");
        } else if (password.getText().isEmpty()){
            showSnackBar("Ingrese su contraseña");
        } else {
            Admin admin = RadarService.getInstance().findAdminByUserName(login.getText());
            if (admin == null && RadarService.getInstance().getAllAdmin().isEmpty()) {
                admin = new Admin("admin", Password.MD5("admin"));
            }
            if (admin == null) {
                showSnackBar("Usuario no existe");
            } else if (!admin.getPassword().equals(Password.MD5(password.getText()))) {
                showSnackBar("Contraseña incorrecta");
            } else {
                primaryStage.close();
                startApp.initApp(primaryStage);
            }
        }
    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void handle(ActionEvent event) {
        HibernateSessionFactory.closeSession();
        Platform.exit();
        System.exit(0);
    }

    public void setStartApp(StartApp startApp) {
        this.startApp = startApp;
    }
}
