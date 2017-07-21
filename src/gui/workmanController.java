package gui;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import model.User;
import service.RadarService;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;


public class workmanController implements Initializable {

    @FXML
    JFXListView<Label> listView;

    @FXML
    Pane cardPane;

    @FXML
    Label img;

    @FXML
    Label name;

    @FXML
    Label dni;

    List<User> users;
    ObservableList<Label> data;

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


        data = FXCollections.observableArrayList();
        for (User user: users) {


            Label dniLabel = new Label(user.getDni());
            dniLabel.setFont(new Font("Arial", 15));
            Label nameLabel = new Label(user.getLastname()+" "+user.getName()+"\n");
            nameLabel.setGraphic(new ImageView(new Image(new FileInputStream("src/gui/policeman32.png"))));
            data.addAll(nameLabel);


        }
        listView.setItems(data);
        listView.setExpanded(true);
        listView.setVerticalGap(6.0);
        listView.depthProperty().set(1);

    }



}
