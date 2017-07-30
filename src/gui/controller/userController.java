package gui.controller;


import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.User;
import service.RadarService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import static javafx.scene.paint.Color.valueOf;

public class userController implements Initializable {

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



    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
                    StartApp startApp = new StartApp();
                    try {
                        startApp.start(startApp.stage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void editUser() {
        editButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                if((nameField.getText().isEmpty() && lastNameField.getText().isEmpty())
                                                  && passwordField.getText().isEmpty()) {
                    JFXDialogLayout dialogLayout = new JFXDialogLayout();
                    dialogLayout.setHeading(new Label("Debe seleccionar un usuario!"));
                    dialogLayout.setBody(new Text("Seleccione un usuario de la lista para modificar"));
                    JFXDialog dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
                    dialog.show();
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
            }
        });
    }

    private void saveChanges(String name, String lastName) {
        saveButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if ((nameField.getText().isEmpty() || lastNameField.getText().isEmpty())
                                                   || passwordField.getText().isEmpty()){
                    JFXDialogLayout dialogLayout = new JFXDialogLayout();
                    dialogLayout.setHeading(new Label("Existen campos vacios!"));
                    dialogLayout.setBody(new Text("Debe llenar todos los campos para la modificacion de usuario"));
                    JFXDialog dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
                    JFXButton okeyButton = new JFXButton();
                    okeyButton.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            dialog.close();
                            saveButton.setDisable(true);
                        }
                    });
                    dialogLayout.setActions(okeyButton);
                    dialog.show();

                }else{
                    JFXDialogLayout dialogLayout = new JFXDialogLayout();
                    dialogLayout.setHeading(new Label("¿Desea Guardar los cambios?"));
                    dialogLayout.setBody(new Text("Algunos datos seran modificados para el usuario: "+lastName+" "+name));
                    JFXDialog dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
                    JFXButton okeyButton = new JFXButton();
                    okeyButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            dialog.close();
                            saveButton.setDisable(true);
                        }
                    });
                    dialogLayout.setActions(okeyButton);
                    dialog.show();
                }
            }
        });
    }

    private  void addUser() {
        JFXButton floatingButton = new JFXButton("+");
        floatingButton.setButtonType(JFXButton.ButtonType.RAISED);
        floatingButton.getStyleClass().addAll("floatingButton");
        floatingButton.setLayoutX(230);
        floatingButton.setLayoutY(495);
        anchorPane.getChildren().add(floatingButton);

        floatingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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
            }
        });
    }

    private void filterUser() {

    }





}
