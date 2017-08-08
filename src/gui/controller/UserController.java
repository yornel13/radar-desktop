package gui.controller;


import com.jfoenix.controls.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionTrigger;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import model.Group;
import model.User;
import org.joda.time.DateTime;
import util.Const;
import util.Password;
import util.RadarFilters;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static javafx.scene.paint.Color.valueOf;

@ViewController("../view/user.fxml")
public class UserController extends BaseController {


    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    @ActionTrigger("back")
    private JFXButton backButton;
    @FXML
    private JFXTextField filterField;



    @FXML
    private JFXButton newGroupBtn;
    @FXML
    private Label groupLabel;
    @FXML
    private Label createGroupLabel;
    @FXML
    private Label employeeLabel;
    @FXML
    private Label editLabel;
    @FXML
    private Label addLabel;
    @FXML
    private JFXTextField groupNameField;
    @FXML
    private JFXTextField dniField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXButton editButton;
    @FXML
    private JFXButton cancelGroupButton;
    @FXML
    private JFXButton saveButton;

    /*********Employee Tab Pane**********/
    @FXML
    private JFXTabPane tabPane;
    private List<User> users;
    private JFXListView<HBox> employeeListView;
    private ObservableList<HBox> empData;
    private JFXButton floatingButton;

    /*********Group Tab Pane**********/
    @FXML
    private JFXListView<HBox> empGroupListView;
    private List<Group> groups;
    private JFXListView<HBox> groupListView;
    private ObservableList<HBox> groupData;


    private User selectUser;

    private boolean editingPassword = false;



    @PostConstruct
    public void init() throws FileNotFoundException {

        setTitle("Empleados");

        createTabPane();
        createFloatingButton();
        loadListView();
        loadSelectedUser();
        editUser();
        addUser();

        passwordField.addEventFilter(KeyEvent.KEY_TYPED, RadarFilters.numberLetterFilter());
        dniField.addEventFilter(KeyEvent.KEY_TYPED, RadarFilters.numberFilter());
        backButton.setGraphic(new ImageView(new Image(new FileInputStream("src/img/arrow_back_icon16.png"))));


    }

    private void loadListView() {

        try {
            loadUserListView();
            loadGroupListView();
            nonUserInfo();
            filterUser();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadUserListView() throws IOException {
        users = service.getAllUser();

        empData = FXCollections.observableArrayList();

        for(User user: users) {
            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            VBox nameDniVBox = new VBox();

            ImageView iconUser = new ImageView(new Image(new FileInputStream("src/img/policeman_64.png")));
            if (!user.getActive()) {
                ColorAdjust desaturate = new ColorAdjust();
                desaturate.setSaturation(-1);
                iconUser.setEffect(desaturate);
            }
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
            empData.add(parentHBox);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/popup.fxml"));
            InputController inputController = new InputController(this);
            loader.setController(inputController);
            JFXPopup popup = new JFXPopup(loader.load());
            inputController.setPopup(popup);
            if (user.getActive()) {
                inputController.setText("Borrar");
            } else {
                inputController.setText("Activar");
            }

            parentHBox.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    nonUserInfo();
                    selectUser = user;
                    popup.show(parentHBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
                }
            });
        }
        employeeListView.setItems(empData);

    }

    private void loadGroupListView() throws FileNotFoundException {
        groups = service.getAllGroup();
        groupData = FXCollections.observableArrayList();

        for (Group group: groups) {

            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            HBox groupNameHBox = new HBox();

            ImageView iconGroup = new ImageView(new Image(new FileInputStream("src/img/group1_64.png")));
            iconGroup.setFitHeight(55);
            iconGroup.setFitWidth(55);
            Label groupNameLabel = new Label("    "+group.getName());
            groupNameLabel.setFont(new Font(null,16));

            imageHBox.getChildren().add(iconGroup);
            groupNameHBox.getChildren().add(groupNameLabel);
            parentHBox.getChildren().addAll(imageHBox, groupNameHBox);
            parentHBox.setUserData(group);
            groupData.add(parentHBox);


        }
        groupListView.setItems(groupData);

    }

    private void createFloatingButton() {
        floatingButton = new JFXButton("+");
        floatingButton.setButtonType(JFXButton.ButtonType.RAISED);
        floatingButton.getStyleClass().addAll("floatingButton");
        floatingButton.setLayoutX(230);
        floatingButton.setLayoutY(525);
        anchorPane.getChildren().add(floatingButton);
    }

    private void loadSelectedUser() {
        employeeListView.setOnMouseClicked(event -> {

            if (event.getButton() == MouseButton.PRIMARY
                    && employeeListView.getSelectionModel().getSelectedItem() != null) {
                editingPassword = false;

                nonEditableUser();

                User user = (User) employeeListView
                        .getSelectionModel().getSelectedItem().getUserData();

                if (!user.getActive()) {
                    nonUserInfo();
                    return;
                }

                dniField.setText(user.getDni());
                nameField.setText(user.getName());
                lastNameField.setText(user.getLastname());
                passwordField.setText(user.getPassword());
                saveChanges(user.getDni(), user.getName(), user.getLastname());

                passwordField.setOnMousePressed(event1 -> {
                    if (!editingPassword) {
                        editingPassword = true;
                        passwordField.setText("");
                    }
                });
            }
        });
    }

    private void initializeFields() {
        employeeLabel.setVisible(true);
        createGroupLabel.setVisible(false);
        editLabel.setVisible(false);
        addLabel.setVisible(false);
        groupLabel.setVisible(false);

        dniField.setVisible(true);
        dniField.setEditable(false);
        dniField.setDisable(true);
        nameField.setVisible(true);
        nameField.setEditable(false);
        nameField.setDisable(true);
        lastNameField.setVisible(true);
        lastNameField.setEditable(false);
        lastNameField.setDisable(true);
        passwordField.setVisible(true);
        passwordField.setEditable(false);
        passwordField.setDisable(true);
        empGroupListView.setVisible(false);
        groupNameField.setVisible(false);


        editButton.setVisible(true);
        editButton.setDisable(true);
        saveButton.setVisible(true);
        saveButton.setDisable(true);
        saveButton.setLayoutY(430);
        saveButton.setLayoutX(120);
        saveButton.setPrefWidth(320);
        cancelGroupButton.setVisible(false);
        floatingButton.setStyle("-fx-background-color: #ffc107");

    }

    private void loadEmpGroupListView() throws FileNotFoundException {
        users = service.getAllUser();

        empData = FXCollections.observableArrayList();

        for(User user: users) {
            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            VBox nameDniVBox = new VBox();

            ImageView iconUser = new ImageView(new Image(new FileInputStream("src/img/policeman_32.png")));
            if (!user.getActive()) {
                ColorAdjust desaturate = new ColorAdjust();
                desaturate.setSaturation(-1);
                iconUser.setEffect(desaturate);
            }

            Label fullNameUser = new Label("    "+user.getLastname()+"  "+user.getName());
            Label dniUser = new Label("    "+user.getDni());
            fullNameUser.setFont(new Font(null,16));
            dniUser.setFont(new Font(null,14));
            dniUser.setTextFill(valueOf("#aaaaaa"));
            imageHBox.setPadding(new Insets(5));
            imageHBox.getChildren().add(iconUser);
            nameDniVBox.getChildren().add(fullNameUser);
            nameDniVBox.getChildren().add(dniUser);
            parentHBox.getChildren().addAll(imageHBox, nameDniVBox);

            parentHBox.setUserData(user);
            parentHBox.setPrefHeight(35);
            empData.add(parentHBox);
        }
        empGroupListView.setItems(empData);
    }

    private void setAddUserFields() {
        addLabel.setVisible(true);
        lastNameField.setVisible(false);
        employeeLabel.setVisible(false);
        editLabel.setVisible(false);

        dniField.clear();
        nameField.clear();
        lastNameField.clear();
        passwordField.clear();

        dniField.setVisible(true);
        dniField.setEditable(true);
        dniField.setDisable(false);
        nameField.setEditable(true);
        nameField.setDisable(false);
        lastNameField.setVisible(true);
        lastNameField.setEditable(true);
        lastNameField.setDisable(false);
        passwordField.setEditable(true);
        passwordField.setDisable(false);
        editButton.setDisable(true);
        saveButton.setDisable(false);
    }

    private void addUser() {

        floatingButton.setOnAction(eventAction -> {
            setAddUserFields();
            saveButton.setOnAction(event -> {
                if ((nameField.getText().isEmpty() || lastNameField.getText().isEmpty())
                        || passwordField.getText().isEmpty()){

                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Campo vacio",
                            "Debe llenar todos los campos para crear un empleado");
                } else if (passwordField.getText().length() < 4) {
                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Error de contraseña",
                            "La contraseña debe tener al menos 4 caracteres");
                } else if (dniField.getText().length() < 5) {
                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Error de dni",
                            "La dni es muy corto");
                } else if (service.findUserByDni(dniField.getText()) != null) {
                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Error de dni",
                            "El dni ya esta siendo usado para otro empleado");
                } else {
                    dialogType = Const.DIALOG_SAVE;
                    showDialog("Confirmacion",
                            "¿Seguro que desea crear el empleado: "
                                    +lastNameField.getText()+" "+nameField.getText()+"?");
                }
            });
        });
    }

    private void editUser() {
        editButton.setOnAction(event -> {

            if((nameField.getText().isEmpty() && lastNameField.getText().isEmpty())
                    && passwordField.getText().isEmpty()) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Debe seleccionar un usuario!",
                        "Seleccione un usuario de la lista para modificar");
            } else {
                editableUser();
            }
        });
    }

    private void editableUser() {

        editLabel.setVisible(true);
        employeeLabel.setVisible(false);
        addLabel.setVisible(false);

        dniField.setEditable(true);
        dniField.setDisable(false);
        nameField.setEditable(true);
        nameField.setDisable(false);
        lastNameField.setEditable(true);
        lastNameField.setDisable(false);
        passwordField.setEditable(true);
        passwordField.setDisable(false);

        editButton.setDisable(true);
        saveButton.setDisable(false);
    }

    private void nonEditableUser() {

        employeeLabel.setVisible(true);
        editLabel.setVisible(false);
        addLabel.setVisible(false);
        groupLabel.setVisible(false);

        dniField.setEditable(false);
        dniField.setDisable(true);
        nameField.setEditable(false);
        nameField.setDisable(true);
        lastNameField.setEditable(false);
        lastNameField.setDisable(true);
        passwordField.setEditable(false);
        passwordField.setDisable(true);
        groupNameField.setVisible(false);

        editButton.setDisable(false);
        saveButton.setDisable(true);
        cancelGroupButton.setVisible(false);
    }

    private void nonUserInfo() {

        employeeLabel.setVisible(true);
        editLabel.setVisible(false);
        addLabel.setVisible(false);
        groupLabel.setVisible(false);

        dniField.clear();
        nameField.clear();
        lastNameField.clear();
        passwordField.clear();
        groupNameField.clear();

        dniField.setEditable(false);
        dniField.setDisable(true);
        nameField.setEditable(false);
        nameField.setDisable(true);
        lastNameField.setEditable(false);
        lastNameField.setDisable(true);
        passwordField.setEditable(false);
        passwordField.setDisable(true);
        groupNameField.setVisible(false);

        editButton.setDisable(true);
        saveButton.setDisable(true);
        cancelGroupButton.setVisible(false);
    }

    private void setTabGroupFields() {
        groupLabel.setVisible(true);
        createGroupLabel.setVisible(false);
        employeeLabel.setVisible(false);
        editLabel.setVisible(false);
        addLabel.setVisible(false);

        dniField.clear();
        nameField.clear();
        lastNameField.clear();
        passwordField.clear();
        groupNameField.clear();

        groupNameField.setVisible(true);
        groupNameField.setDisable(true);
        dniField.setVisible(false);
        nameField.setVisible(false);
        lastNameField.setVisible(false);
        passwordField.setVisible(false);
        empGroupListView.setVisible(true);
        empGroupListView.setDisable(true);
        editButton.setVisible(false);
        saveButton.setVisible(true);
        saveButton.setDisable(true);
        saveButton.setLayoutY(490);
        saveButton.setLayoutX(40);
        saveButton.setPrefWidth(220);
        cancelGroupButton.setVisible(true);
        cancelGroupButton.setLayoutY(490);
        cancelGroupButton.setLayoutX(300);
        cancelGroupButton.setPrefWidth(220);
        cancelGroupButton.setDisable(true);
        floatingButton.setStyle("-fx-background-color: #ffc107");

    }

    private void setCreateGroupFields(){
        createGroupLabel.setVisible(true);
        groupLabel.setVisible(false);
        employeeLabel.setVisible(false);
        editLabel.setVisible(false);
        addLabel.setVisible(false);


        groupNameField.setDisable(false);
        dniField.setVisible(false);
        nameField.setVisible(false);
        lastNameField.setVisible(false);
        passwordField.setVisible(false);
        empGroupListView.setDisable(false);
        editButton.setVisible(false);
        saveButton.setDisable(false);
        saveButton.setVisible(true);
        saveButton.setLayoutY(490);
        saveButton.setLayoutX(40);
        saveButton.setPrefWidth(220);
        cancelGroupButton.setVisible(true);
        cancelGroupButton.setLayoutY(490);
        cancelGroupButton.setLayoutX(300);
        cancelGroupButton.setPrefWidth(220);
        cancelGroupButton.setDisable(false);
        floatingButton.setStyle("-fx-background-color: #f47442");
        floatingButton.setFocusTraversable(false);
    }

    private void createGroup() {

        floatingButton.setOnAction(event -> setCreateGroupFields());

        cancelGroupButton.setOnAction(event -> setTabGroupFields());
    }

    private void createTabPane() {
        employeeListView = new JFXListView();
        employeeListView.setExpanded(true);
        employeeListView.setVerticalGap(2.0);
        employeeListView.depthProperty().set(1);

        Tab empTab = new Tab();
        empTab.setText("Empleados");
        empTab.setContent(employeeListView);
        empTab.setUserData(0);

        groupListView = new JFXListView<>();
        groupListView.setExpanded(true);
        groupListView.setVerticalGap(2.0);
        groupListView.depthProperty().set(1);

        Tab groupTab = new Tab();
        groupTab.setText("Grupos");
        groupTab.setContent(groupListView);
        groupTab.setUserData(1);

        tabPane.getTabs().addAll(empTab, groupTab);

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, t0, t1) -> {

            if((int) t1.getUserData() == 0) {
                initializeFields();
                addUser();
            } else {
                setTabGroupFields();
                try {
                   loadEmpGroupListView();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                createGroup();
            }
       });

    }

    private void saveChanges(String dni, String name, String lastName) {
        saveButton.setOnAction(event -> {
            if ((nameField.getText().isEmpty() || lastNameField.getText().isEmpty())
                                               || passwordField.getText().isEmpty()){

                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Campo vacio",
                        "Debe llenar todos los campos para la modificacion del usuario");
            } else if (editingPassword && passwordField.getText().length() < 4) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Error de contraseña",
                        "La contraseña debe tener al menos 4 caracteres");
            } else if (dniField.getText().length() < 5) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Error de dni",
                        "La dni es muy corto");
            } else if (!dniField.getText().equals(dni) && service.findUserByDni(dniField.getText()) != null) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Error de dni",
                        "El dni ya esta siendo usado para otro empleado");
            } else {
                dialogType = Const.DIALOG_SAVE_EDIT;
                showDialog("¿Desea Guardar los cambios?",
                        "¿Seguro que desea modificar el usuario: "+lastName+" "+name+"?");
            }
        });
    }

    public void deleteUser() {
        User user = selectUser;
        if (selectUser.getActive()) {
            dialogType = Const.DIALOG_DELETE;
            showDialog("Confirmacion",
                    "¿Seguro que desea eliminar el empleado: "
                            + user.getLastname() + " " + user.getName() + "?");
        } else {
            dialogType = Const.DIALOG_ENABLE;
            showDialog("Confirmacion",
                    "¿Seguro que desea reactivar el empleado: "
                            + user.getLastname() + " " + user.getName() + "?");
        }
    }

    private void filterUser() {
        FilteredList<HBox> filteredData = new FilteredList<>(empData, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            nonUserInfo();
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
        employeeListView.setItems(sortedData);
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

    @Override
    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);
        User user;
        switch (dialogType) {
            case Const.DIALOG_SAVE_EDIT:
                user = service.findUserById(((User)
                        employeeListView.getSelectionModel().getSelectedItem().getUserData()).getId());
                user.setDni(dniField.getText());
                user.setName(nameField.getText());
                user.setLastname(lastNameField.getText());
                if (editingPassword) {
                    editingPassword = false;
                    user.setPassword(Password.MD5(passwordField.getText()));
                }
                user.setLastUpdate(new DateTime().getMillis());
                service.doEdit();
                loadListView();
                showSnackBar("Empleado modificado con exito");
                break;
            case Const.DIALOG_SAVE:
                user = new User();
                user.setDni(dniField.getText());
                user.setName(nameField.getText());
                user.setLastname(lastNameField.getText());
                user.setPassword(Password.MD5(passwordField.getText()));
                user.setCreateDate(new DateTime().getMillis());
                user.setLastUpdate(new DateTime().getMillis());
                user.setActive(true);
                service.saveUser(user);
                loadListView();
                showSnackBar("Empleado creado con exito");
                break;
            case Const.DIALOG_DELETE:
                user = service.findUserById(selectUser.getId());
                if (service.deleteUser(user)) {
                    showSnackBar("Empleado borrado con exito");
                } else {
                    showSnackBar("No se puede borrar porque " +
                            "tiene registros, el empleado ha sido desactivado.");
                }
                loadListView();
                break;
            case Const.DIALOG_ENABLE:
                user = service.findUserById(selectUser.getId());
                user.setActive(true);
                service.doEdit();
                loadListView();
                showSnackBar("Empleado activado con exito");
                break;
        }
    }


    public class InputController {

        @FXML
        private JFXListView<?> toolbarPopupList;

        @FXML
        private Label popupLabel;

        private UserController principal;

        private JFXPopup popup;

        public InputController(UserController principal) {
            this.principal = principal;
        }

        @FXML
        private void submit() {
            popup.hide();
            if (toolbarPopupList.getSelectionModel().getSelectedIndex() == 0) {
                principal.deleteUser();
            }
        }

        public void setPopup(JFXPopup popup) {
            this.popup = popup;
        }

        public void setText(String content) {
            popupLabel.setText(content);
        }
    }

}
