package gui.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.User;
import service.RadarService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.valueOf;

public class userController implements Initializable, EventHandler<MouseEvent> {

   private List<User> users;

   @FXML
   private JFXListView<HBox> userListView;
   private ObservableList<HBox> dataUser;

   private JFXTextField filterField;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        users = RadarService.getInstance().getAllUser();
        try {
            loadUserListView();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        filterUser();

    }

    private void filterUser() {

    }

    public void loadUserListView() throws FileNotFoundException {

        dataUser = FXCollections.observableArrayList();

        int i = 0;
        for(User user: users) {
           HBox parentHBox = new HBox();
           HBox imageHBox = new HBox();
           VBox nameDniVBox = new VBox();

           while (i < 1) {

               HBox backButtonHBox = new HBox();
               Label backButton = new Label();
               backButton.setGraphic(new ImageView(new Image(new FileInputStream("src/img/Arrow-Back-icon16.png"))));
               backButtonHBox.setPrefHeight(25);
               backButtonHBox.getChildren().add(backButton);
               dataUser.add(backButtonHBox);
               i++;
           }

           ImageView iconUser = new ImageView(new Image(new FileInputStream("src/img/policeman_64.png")));
           iconUser.setFitHeight(55);
           iconUser.setFitWidth(55);
           Label fullNameUser = new Label("    "+user.getLastname()+"  "+user.getName());
           Label dniUser = new Label("    "+user.getDni());
           fullNameUser.setFont(new Font(null,16));
           dniUser.setFont(new Font(null,14));
           dniUser.setTextFill(valueOf("#aaaaaa"));

           imageHBox.getChildren().add(iconUser);
           nameDniVBox.getChildren().add(fullNameUser);
           nameDniVBox.getChildren().add(dniUser);
           parentHBox.getChildren().addAll(imageHBox, nameDniVBox);

           dataUser.add(parentHBox);
        }
        userListView.setItems(dataUser);
        userListView.setExpanded(true);
        userListView.setVerticalGap(2.0);
        userListView.depthProperty().set(1);

        userListView.setOnMouseClicked(this);
    }


    @Override
    public void handle(MouseEvent event) {

        if(userListView.getSelectionModel().getSelectedIndex() == 0 ) {

            StartApp startApp = new StartApp();
            try {
                startApp.start(startApp.stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
