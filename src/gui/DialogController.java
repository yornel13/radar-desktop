package gui;

import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import util.HibernateSessionFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogController implements Initializable {

    @FXML
    private JFXDialog dialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onDialogAccept(ActionEvent event) {
        dialog.close();
        HibernateSessionFactory.closeSession();
        Platform.exit();
        System.exit(0);
    }

    public void onDialogCancel(ActionEvent event) {
        dialog.close();
    }

    public void setDialog(JFXDialog dialog) {
        this.dialog = dialog;
    }
}
