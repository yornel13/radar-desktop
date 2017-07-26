package gui.controller;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Admin;
import service.RadarService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private JFXListView<HBox> adminListView;
    private ObservableList<HBox> dataAdmin;

    private List<Admin> admins;

    @FXML
    private HBox hBoxBack;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        admins = RadarService.getInstance().getAllAdmin();

        if(admins == null) {
            System.out.println("No admins");
            admins = new ArrayList<>();
        }

        try {
            loadAdminListView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void loadAdminListView() throws FileNotFoundException {

        dataAdmin = FXCollections.observableArrayList();

        int i = 0;
        for(Admin admin: admins) {


            HBox hBox = new HBox();
            HBox imageHBox = new HBox();
            VBox labelsVBox = new VBox();

            while (i < 1){
                hBoxBack  = new HBox();
                Label backButton = new Label();
                backButton.setGraphic(new ImageView(new Image(new FileInputStream("src/img/arrows-Back-icon16.png"))));
                hBoxBack.getChildren().add(backButton);
                dataAdmin.add(hBoxBack);
                i++;
            }
            // ListCells
            Label nameLabel = new Label("   "+admin.getLastname()+" "+admin.getName());
            nameLabel.setFont(new Font(null, 16));
            Label dniLabel  = new Label("   "+admin.getDni());
            dniLabel.setFont( new Font(null, 14));
            dniLabel.setTextFill(Color.valueOf("#aaaaaa"));
            ImageView guardImg = new ImageView(new Image(new FileInputStream("src/img/policeman64.png")));
            guardImg.setFitHeight(55);
            guardImg.setFitWidth(60);

            imageHBox.getChildren().addAll(guardImg, nameLabel);
            imageHBox.setPrefHeight(4);
            labelsVBox.getChildren().addAll(nameLabel, dniLabel);
            labelsVBox.setPadding(new Insets(-1,3,-1,3));
            hBox.getChildren().addAll(imageHBox, labelsVBox);

            dataAdmin.addAll(hBox);

        }

        adminListView.setItems(dataAdmin);
        adminListView.setExpanded(true);
        adminListView.setVerticalGap(2.0);
        adminListView.depthProperty().set(1);
    }

}
