package gui;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.events.JFXDrawerEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.User;
import service.RadarService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WorkmanController  implements Initializable {

    @FXML
    JFXListView<HBox> listView;
    @FXML
    JFXDrawer drawer;

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

    public void loadListView() throws FileNotFoundException {
        //Drawer content
        HBox drawerBox = new HBox();

        drawer.setSidePane(drawerBox);
        VBox vBoxHead = new VBox();
        vBoxHead.setStyle("-fx-background-color: #ffffff");
        vBoxHead.setPrefWidth(300);
        vBoxHead.setPadding(new Insets(20));
        Label iconHeader = new Label();
        iconHeader.setGraphic(new ImageView(new Image(new FileInputStream("src/img/map_64.png"))));

        VBox vBoxDetail = new VBox();
        vBoxDetail.setStyle("-fx-background-color: #f2f2f2");
        vBoxDetail.setPrefHeight(200);
        vBoxHead.getChildren().add(iconHeader);
        drawerBox.getChildren().addAll(vBoxHead, vBoxDetail);
        drawer.setVisible(false);
        drawer.setOnDrawerClosed(event -> drawer.setVisible(false));

        int i = 0;
        data = FXCollections.observableArrayList();
        for (User user: users) {

            while (i < 1){
                HBox hboxBack  = new HBox();
                Label backbtn = new Label();
                backbtn.setGraphic(new ImageView(new Image(new FileInputStream("src/img/arrows-Back-icon16.png"))));
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
            hbox.getChildren().addAll(imgHbox, labelsVbox);

            data.addAll(hbox);
       }

        listView.setItems(data);
        listView.setExpanded(true);
        listView.setVerticalGap(2.0);
        listView.depthProperty().set(1);
        listView.setOnMouseClicked(event -> {
            if (listView.getSelectionModel().getSelectedIndex() == 0) {//Back button

                if (drawer.isShown()) {
                    drawer.close();
                } else
                    try {
                        StartApp sa = new StartApp();
                        sa.start(StartApp.stage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            } else {  //  Drawer
                drawer.open();
                drawer.setVisible(true);

                    User user = users.get(listView.getSelectionModel().getSelectedIndex() - 1);






            }
        });
    }


}
