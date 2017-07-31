package gui.controller;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.User;
import org.joda.time.DateTime;
import util.Const;
import util.Password;

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

    private boolean editingPassword = false;

    @PostConstruct
    public void init() {

        users = service.getAllUser();
        try {
            loadUserListView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        loadSelectedUser();
        editUser();
        addUser();

        filterUser();

        passwordField.addEventFilter(KeyEvent.KEY_TYPED, Password.numberLetterFilter());
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

            parentHBox.setUserData(user);
            dataUser.add(parentHBox);
        }
        userListView.setItems(dataUser);
        userListView.setExpanded(true);
        userListView.setVerticalGap(2.0);
        userListView.depthProperty().set(1);
    }

    private void loadSelectedUser() {
        userListView.setOnMouseClicked(event -> {

            editingPassword = false;

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
                passwordField.setText(userIndex.get(index).getPassword());
                saveChanges(userIndex.get(index).getName(), userIndex.get(index).getLastname());

            }catch (IndexOutOfBoundsException ex) {
                if (index == -2) {

                } else {
                    onBackController();
                }
            }

            passwordField.setOnMousePressed(event1 -> {
                if (!editingPassword) {
                    editingPassword = true;
                    passwordField.setText("");
                }
            });
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
                showDialog("Campo vacio",
                        "Debe llenar todos los campos para la modificacion de usuario",
                        false,
                        true);

            }else if (editingPassword && passwordField.getText().length() < 4) {
                showDialog("Error de contraseña",
                        "La contraseña debe tener al menos 4 caracteres",
                        false,
                        true);
            } else {
                dialogType = Const.DIALOG_SAVE_EDIT;
                showDialog("¿Desea Guardar los cambios?",
                        "¿Seguro que desea modificar el usuario: "+lastName+" "+name+"?");
            }
        });
    }

    @Override
    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);
        switch (dialogType) {
            case Const.DIALOG_SAVE_EDIT:
                User user = service.findUserById(((User)
                        userListView.getSelectionModel().getSelectedItem().getUserData()).getId());
                user.setName(nameField.getText());
                user.setLastname(lastNameField.getText());
                if (editingPassword) {
                    editingPassword = false;
                    user.setPassword(Password.MD5(passwordField.getText()));
                }
                user.setUpdate(new DateTime().getMillis());
                service.doEdit();

                try {
                    loadUserListView();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
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
        FilteredList<HBox> filteredData = new FilteredList<>(dataUser, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                if (dataUser.indexOf(hBox) == 0)
                    return true;

                User user = (User) hBox.getUserData();
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

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
    }





}
