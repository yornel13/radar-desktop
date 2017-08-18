package gui;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionTrigger;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.annotation.PostConstruct;

@ViewController("view/sync.fxml")
public class SyncController extends BaseController {

    @FXML
    @ActionTrigger("workman")
    private JFXButton buttonMap1;
    @FXML
    @ActionTrigger("control")
    private JFXButton buttonMap2;

    @FXML
    @ActionTrigger("employee")
    private JFXButton optionEmployee;
    @FXML
    @ActionTrigger("assign")
    private JFXButton optionAssign;


    @PostConstruct
    public void init() {

        setTitleToCompany(null);
        setBackButtonImageWhite();

        buttonMap1.setOnMouseEntered(event -> buttonMap1.setStyle(" -fx-background-color: #d6d6d6"));
        buttonMap1.setOnMouseExited( event -> buttonMap1.setStyle(" -fx-background-color: #ffc107"));

        buttonMap2.setOnMouseEntered(event -> buttonMap2.setStyle(" -fx-background-color: #d6d6d6"));
        buttonMap2.setOnMouseExited( event -> buttonMap2.setStyle(" -fx-background-color: #ffc107"));

        optionEmployee.setGraphic(new ImageView(new Image(getClass().getResource("img/employee_32.png").toExternalForm())));
        optionAssign.setGraphic(new ImageView(new Image(getClass().getResource("img/assign_32.png").toExternalForm())));

        ImageView mapImage1 = new ImageView(new Image(getClass().getResource("img/control_user.png").toExternalForm()));
        mapImage1.setFitHeight(62);
        mapImage1.setFitWidth(62);
        buttonMap1.setGraphic(mapImage1);
        buttonMap1.setText("Control por\nEMPLEADOS");

        ImageView mapImage2 = new ImageView(new Image(getClass().getResource("img/control_marker.png").toExternalForm()));
        mapImage2.setFitHeight(62);
        mapImage2.setFitWidth(62);
        buttonMap2.setGraphic(mapImage2);
        buttonMap2.setText("Control por\nUBICACIONES");

    }

    @Override
    protected void onBackController() {
        super.onBackToStart();
    }
}

