package gui.controller;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Admin;
import service.RadarService;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable, EventHandler<MouseEvent> {

    @FXML
    private JFXListView<HBox> adminListView;
    private ObservableList<HBox> dataAdmin;

    private List<Admin> admins;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        admins = RadarService.getInstance().getAllAdmin();

        if(admins == null) {
            System.out.println("No admins");
            admins = new ArrayList<>();
        }

        try {
            loadAdminListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAdminListView() throws Exception {

        dataAdmin = FXCollections.observableArrayList();

        int i = 0;
        for(Admin admin: admins) {
            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            VBox nameDniVBox = new VBox();

            while (i < 1) {
                HBox backButtonHBox = new HBox();
                Label backButton = new Label();
                backButton.setGraphic(new ImageView(new Image(new FileInputStream("src/img/Arrow-Back-icon16.png"))));
                backButtonHBox.getChildren().add(backButton);
                dataAdmin.add(backButtonHBox);
                i++;
            }

            ImageView iconUser = new ImageView(new Image(new FileInputStream("src/img/user_64.png")));
            iconUser.setFitHeight(55);
            iconUser.setFitWidth(55);
            Label fullNameUser = new Label("    "+admin.getLastname()+"  "+admin.getName());
            Label dniUser = new Label("    "+admin.getDni());
            fullNameUser.setFont(new Font(null,16));
            dniUser.setFont(new Font(null,14));
            dniUser.setTextFill(Color.valueOf("#aaaaaa"));

            imageHBox.getChildren().add(iconUser);
            nameDniVBox.getChildren().add(fullNameUser);
            nameDniVBox.getChildren().add(dniUser);
            parentHBox.getChildren().addAll(imageHBox, nameDniVBox);

            dataAdmin.add(parentHBox);
        }
        adminListView.setItems(dataAdmin);
        adminListView.setExpanded(true);
        adminListView.setVerticalGap(2.0);
        adminListView.depthProperty().set(1);

        adminListView.setOnMouseClicked(this::handle);
    }

    @Override
    public void handle(MouseEvent event) {
        if (adminListView.getSelectionModel().getSelectedIndex() == 0){
            StartApp startApp = new StartApp();
            try {
                startApp.start(StartApp.stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
