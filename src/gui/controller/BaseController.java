package gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.ActionHandler;
import io.datafx.controller.flow.context.FlowActionHandler;
import io.datafx.controller.util.VetoException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
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
        this.cancelButton.setVisible(true);
        this.acceptButton.setVisible(true);
    }

    protected void showDialog(String title, String content, Boolean cancelButton, Boolean acceptButton) {
        dialogTitle.setText(title);
        dialogContent.setText(content);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.show(root);
        this.cancelButton.setVisible(cancelButton);
        this.acceptButton.setVisible(acceptButton);
    }

    @FXML
    public void onDialogCancel(ActionEvent actionEvent) {
        dialog.close();
    }

    @FXML
    public void onDialogAccept(ActionEvent actionEvent) {
        dialog.close();
    }

}
