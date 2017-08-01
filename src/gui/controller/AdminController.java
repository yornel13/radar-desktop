package gui.controller;


import com.jfoenix.controls.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionTrigger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Admin;
import org.joda.time.DateTime;
import util.Const;
import util.Password;
import util.RadarFilters;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@ViewController(value = "../view/admin.fxml")
public class AdminController extends BaseController implements EventHandler<MouseEvent> {

    @FXML
    private JFXListView<HBox> adminListView;
    private ObservableList<HBox> dataAdmin;

    private List<Admin> admins;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    @ActionTrigger("back")
    private JFXButton backButton;
    @FXML
    private JFXTextField filterField;
    @FXML
    private Label adminLabel;
    @FXML
    private Label editLabel;
    @FXML
    private Label addLabel;
    @FXML
    private JFXTextField userNameField;
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
    private JFXButton saveButton;

    private Admin selectAdmin;

    private boolean  editingPassword = false;



    @PostConstruct
    public void init() throws FileNotFoundException {

        loadListView();
        loadSelectedAdmin();
        editAdmin();
        addAdmin();

        passwordField.addEventFilter(KeyEvent.KEY_TYPED, RadarFilters.numberLetterFilter());
        dniField.addEventFilter(KeyEvent.KEY_TYPED, RadarFilters.numberFilter());
        backButton.setGraphic(new ImageView(new Image(new FileInputStream("src/img/arrow_back_icon16.png"))));

    }

    public void loadListView() {
        admins = service.getAllAdmin();
        try {
            loadAdminListView();
            nonAdminInfo();
            filterAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadAdminListView() throws Exception {

        dataAdmin = FXCollections.observableArrayList();

        for(Admin admin: admins) {
            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            VBox nameDniVBox = new VBox();


            ImageView iconAdmin = new ImageView(new Image(new FileInputStream("src/img/user_64.png")));
            if(!admin.getActive()) {
                ColorAdjust desaturate = new ColorAdjust();
                desaturate.setSaturation(-1);
                iconAdmin.setEffect(desaturate);
            }
            iconAdmin.setFitHeight(55);
            iconAdmin.setFitWidth(55);
            Label fullNameAdmin = new Label("    "+admin.getLastname()+"  "+admin.getName());
            Label dniAdmin = new Label("    "+admin.getDni());
            fullNameAdmin.setFont(new Font(null,16));
            dniAdmin.setFont(new Font(null,14));
            dniAdmin.setTextFill(Color.valueOf("#aaaaaa"));
            imageHBox.getChildren().add(iconAdmin);
            nameDniVBox.getChildren().add(fullNameAdmin);
            nameDniVBox.getChildren().add(dniAdmin);
            parentHBox.getChildren().addAll(imageHBox, nameDniVBox);

            parentHBox.setUserData(admin);
            dataAdmin.add(parentHBox);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/popup.fxml"));
            InputController inputController = new InputController(this);
            loader.setController(inputController);
            JFXPopup popup = new JFXPopup(loader.load());
            inputController.setPopup(popup);
            if(admin.getActive()) {
                inputController.setText("Borrar");
            }else {
                inputController.setText("Activar");
            }

            parentHBox.setOnMouseClicked(event -> {
                if(event.getButton() == MouseButton.SECONDARY) {
                    nonAdminInfo();
                    selectAdmin = admin;
                    popup.show(parentHBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
                }
            });
        }
        adminListView.setItems(dataAdmin);
        adminListView.setExpanded(true);
        adminListView.setVerticalGap(2.0);
        adminListView.depthProperty().set(1);

    }

    void editableAdmin() {

        editLabel.setVisible(true);
        adminLabel.setVisible(false);
        addLabel.setVisible(false);

        userNameField.setEditable(true);
        userNameField.setDisable(false);
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

    void nonEditableAdmin() {

        adminLabel.setVisible(true);
        editLabel.setVisible(false);
        addLabel.setVisible(false);

        userNameField.setEditable(false);
        userNameField.setDisable(true);
        dniField.setEditable(false);
        dniField.setDisable(true);
        nameField.setEditable(false);
        nameField.setDisable(true);
        lastNameField.setEditable(false);
        lastNameField.setDisable(true);
        passwordField.setEditable(false);
        passwordField.setDisable(true);

        editButton.setDisable(false);
        saveButton.setDisable(true);
    }

    void createAdmin() {

        adminLabel.setVisible(false);
        editLabel.setVisible(false);
        addLabel.setVisible(true);

        userNameField.clear();
        dniField.clear();
        nameField.clear();
        lastNameField.clear();
        passwordField.clear();

        userNameField.setEditable(true);
        userNameField.setDisable(false);
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

    void nonAdminInfo() {

        adminLabel.setVisible(true);
        editLabel.setVisible(false);
        addLabel.setVisible(false);

        userNameField.clear();
        dniField.clear();
        nameField.clear();
        lastNameField.clear();
        passwordField.clear();

        userNameField.setEditable(false);
        userNameField.setDisable(true);
        dniField.setEditable(false);
        dniField.setDisable(true);
        nameField.setEditable(false);
        nameField.setDisable(true);
        lastNameField.setEditable(false);
        lastNameField.setDisable(true);
        passwordField.setEditable(false);
        passwordField.setDisable(true);

        editButton.setDisable(true);
        saveButton.setDisable(true);
    }


    @Override
    public void handle(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {

            if (adminListView.getSelectionModel().getSelectedIndex() == 0) {
                onBackController();
            }
        }
    }

    private  void loadSelectedAdmin() {
        adminListView.setOnMouseClicked(event -> {

            if(event.getButton() == MouseButton.PRIMARY
                    && adminListView.getSelectionModel().getSelectedItem() != null) {
                editingPassword = false;

                nonEditableAdmin();

                Admin admin = (Admin) adminListView.getSelectionModel().getSelectedItem().getUserData();

                if(!admin.getActive()) {
                    nonAdminInfo();
                    return;
                }

                userNameField.setText(admin.getUsername());
                dniField.setText(admin.getDni());
                nameField.setText(admin.getName());
                lastNameField.setText(admin.getLastname());
                passwordField.setText(admin.getPassword());
                saveChanges(admin.getUsername(), admin.getDni(), admin.getName(), admin.getLastname());

                passwordField.setOnMousePressed(event1 -> {
                    if (!editingPassword) {
                        editingPassword = true;
                        passwordField.setText("");
                    }
                });
            }
        });
    }

    private void editAdmin() {
        editButton.setOnAction(event -> {
            if((nameField.getText().isEmpty() && lastNameField.getText().isEmpty())
                                              && (passwordField.getText().isEmpty()
                                              && userNameField.getText().isEmpty())) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Debe seleccionar un usuario!",
                        "Seleccione un usuario de la lista para modificar");
            } else {
                editableAdmin();
            }
        });
    }

    private void saveChanges(String userName, String dni, String name, String lastName) {
        saveButton.setOnAction(event -> {
            if ((nameField.getText().isEmpty() || lastNameField.getText().isEmpty())
                    || passwordField.getText().isEmpty()
                    || userNameField.getText().isEmpty()) {

                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Campo vacio",
                        "Debe llenar todos los campos para la modificacion del administrador");
            } else if (userNameField.getText().equals(userName) && service.findAdminByUserName(userNameField.getText()) != null){
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Error en nombre de usuario",
                        "Este nombre de usuario esta siendo usado por otro administrador");
            }else if (editingPassword && passwordField.getText().length() < 4) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Error de contraseña",
                        "La contraseña debe tener al menos 4 caracteres");
            } else if (dniField.getText().length() < 5) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Error de dni",
                        "La dni es muy corto");
            } else if (!dniField.getText().equals(dni) && service.findAdminByDni(dniField.getText()) != null) {
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

    private void addAdmin() {
        JFXButton floatingButton = new JFXButton("+");
        floatingButton.setButtonType(JFXButton.ButtonType.RAISED);
        floatingButton.getStyleClass().addAll("floatingButton");
        floatingButton.setLayoutX(230);
        floatingButton.setLayoutY(525);
        anchorPane.getChildren().add(floatingButton);

        floatingButton.setOnAction(eventAction -> {
            createAdmin();
            saveButton.setOnAction(event -> {
                if ((nameField.getText().isEmpty() || lastNameField.getText().isEmpty())
                        || passwordField.getText().isEmpty()
                        || userNameField.getText().isEmpty()) {
                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Campo vacio",
                            "Debe llenar todos los campos para crear un administrador");
                } else if (passwordField.getText().length() < 4) {
                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Error de contraseña",
                            "La contraseña debe tener al menos 4 caracteres");
                } else if (dniField.getText().length() < 5) {
                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Error de dni",
                            "La dni es muy corto");
                } else if (service.findAdminByUserName(userNameField.getText()) != null) {
                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Error en nombre de usuario",
                            "Este nombre de usuario esta siendo usado por otro administrador");
                }else if (service.findAdminByDni(dniField.getText()) != null) {
                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Error de dni",
                            "El dni ya esta siendo usado para otro adminitrador");
                } else {
                    dialogType = Const.DIALOG_SAVE;
                    showDialog("Confirmacion",
                            "¿Seguro que desea crear el administrador: "
                                    +lastNameField.getText()+" "+nameField.getText()+"?");
                }
            });
        } );
    }

    @Override
    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);
        Admin admin;
        switch (dialogType) {
            case Const.DIALOG_SAVE_EDIT:
                admin = service.findAdminById(((Admin)
                        adminListView.getSelectionModel().getSelectedItem().getUserData()).getId());
                admin.setUsername(userNameField.getText());
                admin.setDni(dniField.getText());
                admin.setName(nameField.getText());
                admin.setLastname(lastNameField.getText());
                if (editingPassword) {
                    editingPassword = false;
                    admin.setPassword(Password.MD5(passwordField.getText()));
                }
                admin.setLastUpdate(new DateTime().getMillis());
                service.doEdit();
                loadListView();
                showSnackBar("Administrador modificado con exito");
                break;
            case Const.DIALOG_SAVE:
                admin = new Admin();
                admin.setUsername(userNameField.getText());
                admin.setDni(dniField.getText());
                admin.setName(nameField.getText());
                admin.setLastname(lastNameField.getText());
                admin.setPassword(Password.MD5(passwordField.getText()));
                admin.setCreateDate(new DateTime().getMillis());
                admin.setLastUpdate(new DateTime().getMillis());
                admin.setActive(true);
                service.saveAdmin(admin);
                loadListView();
                showSnackBar("Administrador creado con exito");
                break;
            case Const.DIALOG_DELETE:
                admin = service.findAdminById(selectAdmin.getId());
                service.deleteAdmin(admin);
                loadListView();
                showSnackBar("Administrador borrado");
                break;
        }


    }
    private void deleteAdmin() {
        Admin admin = selectAdmin;
        if (selectAdmin.getActive()) {
            dialogType = Const.DIALOG_DELETE;
            showDialog("Confirmacion",
                    "¿Seguro que desea eliminar el empleado: "
                            + admin.getLastname() + " " + admin.getName() + "?");
        } else {
            dialogType = Const.DIALOG_ENABLE;
            showDialog("Confirmacion",
                    "¿Seguro que desea reactivar el empleado: "
                            + admin.getLastname() + " " + admin.getName() + "?");
        }
    }

    private void filterAdmin() {
        FilteredList<HBox> filteredData = new FilteredList<>(dataAdmin, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            nonAdminInfo();
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                Admin admin = (Admin) hBox.getUserData();
                if (admin == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = filterField.getText().toLowerCase();

                String fullName = admin.getLastname() + " " + admin.getName();
                if (admin.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (admin.getLastname().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }else if (admin.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }else if (admin.getDni().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (fullName.toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        adminListView.setItems(sortedData);
        checkFilter(filteredData);
    }

    void checkFilter(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (filterField.getText() == null || filterField.getText().isEmpty()) {
                return true;
            }
            Admin admin = (Admin) hBox.getUserData();
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = filterField.getText().toLowerCase();

            String fullName = admin.getLastname() + " " + admin.getName();
            if (admin.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            } else if (admin.getLastname().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            } else if (admin.getDni().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            } else if (admin.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            }else if (fullName.toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            }
            return false; // Does not match.
        });
    }

    public class InputController {

        @FXML
        private JFXListView<?> toolbarPopupList;

        @FXML
        private Label popupLabel;

        private AdminController principal;

        private JFXPopup popup;

        public InputController(AdminController principal) {
            this.principal = principal;
        }

        @FXML
        private void submit() {
            popup.hide();
            if (toolbarPopupList.getSelectionModel().getSelectedIndex() == 0) {
                principal.deleteAdmin();
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
