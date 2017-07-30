package gui.controller;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import service.RadarService;
import util.Const;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.valueOf;

@ViewController("../view/user.fxml")
public class UserController extends BaseController {

    private List<User> users;
    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXTextField filterField;
    @FXML
    private JFXListView<HBox> userListView;

    private ObservableList<HBox> dataUser;
    @FXML
    private Label employeeLabel;
    @FXML
    private Label editLabel;
    @FXML
    private Label addLabel;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXButton editButton;
    @FXML
    private JFXButton saveButton;

    @PostConstruct
    public void init() {

        users = RadarService.getInstance().getAllUser();
        try {
            loadUserListView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        filterUser();
        loadSelectedUser();
        editUser();
        addUser();

    }

    public void loadUserListView() throws FileNotFoundException {

        editButton.setDisable(true);
        employeeLabel.setVisible(true);
        dataUser = FXCollections.observableArrayList();

        int i = 0;
        for(User user: users) {
            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            VBox nameDniVBox = new VBox();

            while (i < 1) {
                HBox backButtonHBox = new HBox();
                Label backButton = new Label();
                backButton.setGraphic(new ImageView(new Image(new FileInputStream("src/img/arrow_back_icon16.png"))));
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

    }

    private void loadSelectedUser() {
        userListView.setOnMouseClicked(event -> {

            employeeLabel.setVisible(true);
            editLabel.setVisible(false);
            addLabel.setVisible(false);

            nameField.setEditable(false);
            nameField.setDisable(true);
            lastNameField.setEditable(false);
            lastNameField.setDisable(true);
            passwordField.setEditable(false);
            passwordField.setDisable(true);

            editButton.setDisable(false);
            saveButton.setDisable(true);

            int index = userListView.getSelectionModel().getSelectedIndex()-1;
            ArrayList<User> userIndex = new ArrayList();
            for(User user : users) {
                userIndex.add(user);
            }

            try {
                nameField.setText(userIndex.get(index).getName());
                lastNameField.setText(userIndex.get(index).getLastname());
                passwordField.setText("1234");
                saveChanges(userIndex.get(index).getName(), userIndex.get(index).getLastname());

            }catch (IndexOutOfBoundsException ex) {
                if (index == -2) {

                } else {
                    onBackController();
                }
            }
        });
    }

    private void editUser() {
        editButton.setOnAction(event -> {

            if((nameField.getText().isEmpty() && lastNameField.getText().isEmpty())
                                              && passwordField.getText().isEmpty()) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialog("Debe seleccionar un usuario!",
                        "Seleccione un usuario de la lista para modificar",
                        false,
                        true);
            }
            editLabel.setVisible(true);
            employeeLabel.setVisible(false);
            addLabel.setVisible(false);

            nameField.setEditable(true);
            nameField.setDisable(false);
            lastNameField.setEditable(true);
            lastNameField.setDisable(false);
            passwordField.setEditable(true);
            passwordField.setDisable(false);

            editButton.setDisable(true);
            saveButton.setDisable(false);
        });
    }

    private void saveChanges(String name, String lastName) {
        saveButton.setOnAction(event -> {
            if ((nameField.getText().isEmpty() || lastNameField.getText().isEmpty())
                                               || passwordField.getText().isEmpty()){

                dialogType = Const.DIALOG_NOTIFICATION;
                showDialog("¿Desea Guardar los cambios?",
                        "Debe llenar todos los campos para la modificacion de usuario",
                        false,
                        true);

            }else{
                dialogType = Const.DIALOG_SAVE_EDIT;
                showDialog("Existen campos vacios!",
                        "Algunos datos seran modificados para el usuario: "+lastName+" "+name);
            }
        });
    }

    @Override
    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);
        switch (dialogType) {
            case Const.DIALOG_SAVE_EDIT:
                // TODO, code here
                break;
        }
    }

    private  void addUser() {
        JFXButton floatingButton = new JFXButton("+");
        floatingButton.setButtonType(JFXButton.ButtonType.RAISED);
        floatingButton.getStyleClass().addAll("floatingButton");
        floatingButton.setLayoutX(230);
        floatingButton.setLayoutY(495);
        anchorPane.getChildren().add(floatingButton);

        floatingButton.setOnAction(event -> {
            employeeLabel.setVisible(false);
            editLabel.setVisible(false);
            addLabel.setVisible(true);

            nameField.clear();
            lastNameField.clear();
            passwordField.clear();

            nameField.setEditable(true);
            nameField.setDisable(false);
            lastNameField.setEditable(true);
            lastNameField.setDisable(false);
            passwordField.setEditable(true);
            passwordField.setDisable(false);
            editButton.setDisable(true);
            saveButton.setDisable(false);
        });
    }

    private void filterUser() {

    }





}
