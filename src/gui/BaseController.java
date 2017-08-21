package gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
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
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Company;
import service.RadarService;
import util.ExtendedAnimatedFlowContainer;

import java.awt.*;
import java.io.File;
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

    protected Company company;

    private javafx.scene.control.Dialog<Void> dialogLoading;

    public Company getCompany() {
        company = (Company) flowContext.getRegisteredObject("company");
        return company;
    }

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

    protected void onBackToSync() {
        try {
            ExtendedAnimatedFlowContainer animations = (ExtendedAnimatedFlowContainer)
                    flowContext.getRegisteredObject("AnimatedFlow");
            animations.changeAnimation(ContainerAnimations.SWIPE_RIGHT);
            actionHandler.handle("sync");
            animations.changeAnimation(ContainerAnimations.SWIPE_LEFT);
        } catch (VetoException e) {
            e.printStackTrace();
        } catch (FlowException e) {
            e.printStackTrace();
        }
    }

    protected void onBackToStart() {
        try {
            ExtendedAnimatedFlowContainer animations = (ExtendedAnimatedFlowContainer)
                    flowContext.getRegisteredObject("AnimatedFlow");
            animations.changeAnimation(ContainerAnimations.SWIPE_RIGHT);
            actionHandler.handle("start");
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
        acceptButton.setText("ACEPTAR");
        acceptButton.setVisible(true);
    }

    protected void showDialogPrint(String content) {
        dialogTitle.setText("Imprimir");
        dialogContent.setText(content);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.show(root);
        cancelButton.setText("CANCELAR");
        cancelButton.setVisible(true);
        acceptButton.setText("SELECCIONAR RUTA");
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

    protected void setTitleToCompany(String title) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        cancel();
                        Platform.runLater(() -> {
                            Stage stage = (Stage) flowContext.getRegisteredObject("Stage");
                            if (title == null || title.isEmpty())
                                stage.setTitle(getCompany().getName());
                            else
                            stage.setTitle(getCompany().getName()+" - "+title);
                        });
                    }
                }, 500, 500
        );
    }

    protected void setBackButtonImageBlack() {
        backButton.setGraphic(new ImageView(
                    new Image(getClass().getResource("img/arrow_back_icon16.png").toExternalForm())));

    }

    protected void setBackButtonImageWhite() {
        backButton.setGraphic(new ImageView(
                new Image(getClass().getResource("img/back_white_16.png").toExternalForm())));

    }

    protected File selectDirectory() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Selecciona un directorio para guardar el reporte");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return fileChooser.showDialog(root.getScene().getWindow());
    }

    protected void dialogLoadingPrint(){
        dialogLoading = new javafx.scene.control.Dialog<>();
        dialogLoading.initModality(Modality.WINDOW_MODAL);
        dialogLoading.initOwner(root.getScene().getWindow());//stage here is the stage of your webview
        dialogLoading.initStyle(StageStyle.TRANSPARENT);
        Label loader = new Label("      Imprimiendo, por favor espere...");
        loader.setContentDisplay(ContentDisplay.LEFT);
        JFXSpinner spinner = new JFXSpinner();
        spinner.setRadius(23);
        loader.setGraphic(spinner);
        dialogLoading.getDialogPane().setContent(loader);
        dialogLoading.getDialogPane().setStyle("-fx-background-color: #E0E0E0;");
        dialogLoading.getDialogPane().setPrefSize(300, 50);
        dialogLoading.getDialogPane().setStyle("" + "-fx-border-color:#0288D1; "
                +"-fx-border-width:0.5; "
                +"-fx-background-color: white;");
        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.DARKGRAY);
        dialogLoading.getDialogPane().setEffect(ds);
        dialogLoading.show();
    }

    public void closeDialogLoading() {
        if (dialogLoading != null) {
            Stage toClose = (Stage) dialogLoading.getDialogPane()
                    .getScene().getWindow();
            toClose.close();
            dialogLoading.close();
            dialogLoading = null;
        }
    }



}
