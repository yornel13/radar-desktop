package gui;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.User;
import service.RadarService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class WorkmanController  implements Initializable {

    @FXML
    JFXListView<HBox> listView;

    List<User> users;
    ObservableList<HBox> data;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        users = RadarService.getInstance().getAllUser();
        if(users == null) {
            System.out.println("No users");
            users = new ArrayList<>();
        }

        try {
            loadListView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    HBox hboxBack;
    public void loadListView() throws FileNotFoundException {

        int i = 0;
        data = FXCollections.observableArrayList();
        for (User user: users) {

            while (i < 1){
                hboxBack  = new HBox();
                Label backbtn = new Label();
                backbtn.setGraphic(new ImageView(new Image(new FileInputStream("src/img/arrows-Back-icon16.png"))));
                //hboxBack.setStyle("-fx-background-color: #fce72a");
                hboxBack.getChildren().add(backbtn);
                data.add(hboxBack);
                i++;
            }

            HBox hbox = new HBox();
            HBox imgHbox = new HBox();
            VBox labelsVbox = new VBox();

            Label nameLabel = new Label("   "+user.getLastname()+" "+user.getName());
            nameLabel.setFont(new Font(null, 16));
            Label dniLabel  = new Label("   "+user.getDni());
            dniLabel.setFont( new Font(null, 14));
            dniLabel.setTextFill(Color.valueOf("#aaaaaa"));
            ImageView guardImg = new ImageView(new Image(new FileInputStream("src/img/policeman64.png")));
            guardImg.setFitHeight(55);
            guardImg.setFitWidth(60);

            imgHbox.getChildren().addAll(guardImg, nameLabel);
            imgHbox.setPrefHeight(4);
            labelsVbox.getChildren().addAll(nameLabel, dniLabel);
            labelsVbox.setPadding(new Insets(-1,3,-1,3));
            hbox.getChildren().addAll(imgHbox, labelsVbox);
            //hbox.setStyle("-fx-background-color: #fce72a");

            data.addAll(hbox);
       }

        listView.setItems(data);
        listView.setExpanded(true);
        listView.setVerticalGap(2.0);
        listView.depthProperty().set(1);

        StartApp sa = new StartApp();
        hboxBack.setOnMouseClicked(event -> {
            try {
                sa.start(StartApp.stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
