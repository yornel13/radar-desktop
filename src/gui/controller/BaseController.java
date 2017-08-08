package gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSnackbar;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.ActionHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.FlowActionHandler;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import service.RadarService;
import util.ExtendedAnimatedFlowContainer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

public class BaseController {

    @ActionHandler
    protected FlowActionHandler actionHandler;

    @FXMLViewFlowContext
    protected ViewFlowContext flowContext;

    @FXML
    @ActionTrigger("back")
    private JFXButton backButton;

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

    protected RadarService service = RadarService.getInstance();

    @ActionMethod("back")
    protected void onBackController() {
        try {
            ExtendedAnimatedFlowContainer animations = (ExtendedAnimatedFlowContainer)
                    flowContext.getRegisteredObject("AnimatedFlow");
            animations.changeAnimation(ContainerAnimations.SWIPE_RIGHT);
            actionHandler.navigateBack();
            animations.changeAnimation(ContainerAnimations.SWIPE_LEFT);
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
        JFXSnackbar snackBar = new JFXSnackbar(root);
        snackBar.enqueue(new JFXSnackbar.SnackbarEvent(content));
    }

    public MouseEvent clickPrimaryMouseButton() {
        MouseEvent event = new MouseEvent(null, 0,0,0,0,
                MouseButton.PRIMARY,1,false,false,
                false,false,false,false,
                false,false,false,false,
                null);
        return event;
    }

    public MouseEvent clickSecondaryMouseButton() {
        MouseEvent event = new MouseEvent(null, 0,0,0,0,
                MouseButton.SECONDARY,1,false,false,
                false,false,false,false,
                false,false,false,false,
                null);
        return event;
    }

    protected void setTitle(String title) {
        new Timer().schedule(
            new TimerTask() {
                @Override
                public void run() {
                    cancel();
                    Platform.runLater(() -> {
                        Stage stage = (Stage) flowContext.getRegisteredObject("Stage");
                        stage.setTitle(title);
                    });
                }
            }, 500, 500
        );
    }

    protected void setBackButtonImage() {
        try {
            backButton.setGraphic(new ImageView(
                    new Image(new FileInputStream("src/img/arrow_back_icon16.png"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
