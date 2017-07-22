package gui;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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


public class workmanController implements Initializable {

    @FXML
    JFXListView<HBox> listView;

    @FXML
    Pane cardPane;



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


        data = FXCollections.observableArrayList();
        for (User user: users) {

            HBox hbox = new HBox();
            HBox imgHbox = new HBox();
            VBox labelsVbox = new VBox();
            Label imgLabel  = new Label();
            imgLabel.setGraphic(new ImageView(new Image(new FileInputStream("src/gui/policeman32.png"))));
            Label nameLabel = new Label("\t  "+user.getLastname()+" "+user.getName()+"\n");
            Label dniLabel  = new Label("\t"+user.getDni());
            dniLabel.setFont( new Font(null, 14));
            dniLabel.setTextFill(Color.valueOf("#aaaaaa"));
            imgHbox.getChildren().add(imgLabel);
            labelsVbox.getChildren().addAll(nameLabel, dniLabel);
            hbox.getChildren().addAll(imgHbox, labelsVbox);
            data.addAll(hbox);
       }
        listView.setItems(data);
        listView.setExpanded(true);
        listView.setVerticalGap(6.0);
        listView.depthProperty().set(1);

    }



}
