package gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSnackbar;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.ActionHandler;
import io.datafx.controller.flow.context.FlowActionHandler;
import io.datafx.controller.util.VetoException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import service.RadarService;
import util.Const;

import java.beans.Transient;

public class BaseController {

    @FXML
    protected StackPane root;

    @FXML
    protected JFXDialog dialog;

    @FXML
    protected Label dialogTitle;

    @FXML
    protected Label dialogContent;

    @FXML
    protected JFXButton acceptButton;

    @FXML
    protected JFXButton cancelButton;

    protected int dialogType;

    @ActionHandler
    protected FlowActionHandler actionHandler;

    protected RadarService service = RadarService.getInstance();

    protected void onBackController() {
        try {
            actionHandler.navigateBack();
        } catch (VetoException e) {
            e.printStackTrace();
        } catch (FlowException e) {
            e.printStackTrace();
        }
    }

    protected void showDialog(String title, String content) {
        dialogTitle.setText(title);
        dialogContent.setText(content);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.show(root);
        cancelButton.setText("CANCELAR");
        cancelButton.setVisible(true);
        acceptButton.setVisible(true);
    }

    protected void showDialogNotification(String title, String content) {
        dialogTitle.setText(title);
        dialogContent.setText(content);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.show(root);
        cancelButton.setText("ACEPTAR");
        cancelButton.setVisible(true);
        acceptButton.setVisible(false);
    }

    @FXML
    public void onDialogCancel(ActionEvent actionEvent) {
        dialog.close();
    }

    @FXML
    public void onDialogAccept(ActionEvent actionEvent) {
        dialog.close();
    }

    protected void showSnackBar(String content) {
        JFXSnackbar bar = new JFXSnackbar(root);
        bar.enqueue(new JFXSnackbar.SnackbarEvent(content));
    }

}
