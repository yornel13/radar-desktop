package gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionTrigger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.User;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static javafx.scene.paint.Color.valueOf;

@ViewController("../view/assign.fxml")
public class AssignController extends BaseController {

    @FXML
    @ActionTrigger("back")
    private JFXButton backButton;

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXTextField filterField;

    @FXML
    private JFXListView<HBox> userListView;

    private ObservableList<HBox> dataUser;

    private List<User> users;

    @PostConstruct
    public void init() throws FileNotFoundException {

        loadListView();

        backButton.setGraphic(new ImageView(new Image(new
                FileInputStream("src/img/arrow_back_icon16.png"))));
    }

    public void loadListView() {
        users = service.getAllUserActive();
        try {
            loadUserListView();
            filterUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUserListView() throws IOException {

        dataUser = FXCollections.observableArrayList();

        for(User user: users) {
            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            VBox nameDniVBox = new VBox();

            ImageView iconUser = new ImageView(new Image(
                    new FileInputStream("src/img/policeman_64.png")));
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

            parentHBox.setUserData(user);
            dataUser.add(parentHBox);
        }
        userListView.setItems(dataUser);
        userListView.setExpanded(true);
        userListView.setVerticalGap(2.0);
        userListView.depthProperty().set(1);
    }

    private void filterUser() {
        FilteredList<HBox> filteredData = new FilteredList<>(dataUser, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                User user = (User) hBox.getUserData();
                if (user == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = filterField.getText().toLowerCase();

                String fullName = user.getLastname()+" "+user.getName();
                if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (user.getLastname().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (user.getDni().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (fullName.toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        userListView.setItems(sortedData);
        checkFilter(filteredData);
    }

    void checkFilter(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (filterField.getText() == null || filterField.getText().isEmpty()) {
                return true;
            }

            User user = (User) hBox.getUserData();
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = filterField.getText().toLowerCase();

            String fullName = user.getLastname()+" "+user.getName();
            if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            } else if (user.getLastname().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            } else if (user.getDni().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            } else if (fullName.toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            }
            return false; // Does not match.
        });
    }

}
