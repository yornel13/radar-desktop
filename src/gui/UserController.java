package gui;


import com.jfoenix.controls.*;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.MapReadyListener;
import gui.async.PrintGroupReportTask;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.joda.time.DateTime;
import util.Const;
import util.Password;
import util.RadarDate;
import util.RadarFilters;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javafx.scene.paint.Color.valueOf;

@ViewController("view/user.fxml")
public class UserController extends BaseController implements MapComponentInitializedListener,
        MapReadyListener, EventHandler<MouseEvent>,PrintGroupReportTask.PrintTask {

    /*********Employee Tab Pane**********/
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private Pane barPane;
    private List<User> userList;
    private JFXListView<HBox> employeeListView;
    private ObservableList<HBox> empData;
    private ObservableList<User> empGroupSelectedData;
    private JFXButton floatingButton;
    @FXML
    private Label employeeLabel;
    @FXML
    private Label editLabel;
    @FXML
    private Label addLabel;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXTextField dniField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXButton editButton;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelGroupButton;


    /*********Group Tab Pane**********/
    @FXML
    private JFXListView<User> empGroupListView;
    private ObservableList<User> empGroupData;
    private List<User> empGroupList;

    private JFXListView<HBox> groupListView;
    private ObservableList<HBox> groupData;
    private List<Group> groupList;
    @FXML
    private JFXButton printGroupReport;

    @FXML
    private Label groupLabel;
    @FXML
    private Label createGroupLabel;
    @FXML
    private JFXTextField filterFieldGroup;
    @FXML
    private JFXTextField filterFieldGroupUser;
    @FXML
    private Label editGroupLabel;
    @FXML
    private JFXButton editGroup;
    @FXML
    private JFXTextField groupNameField;
    private Group selectedGroup;

    private List<Group> groups;


    /**********OTHERS*********/
    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    @ActionTrigger("back")
    private JFXButton backButton;
    @FXML
    private JFXTextField filterField;

    private User selectedUser;
    private boolean editingPassword = false;

    private javafx.scene.control.Dialog<Void> dialogLoading;



    @PostConstruct
    public void init() throws FileNotFoundException {

        setTitleToCompany("Empleados");
        setBackButtonImageBlack();

        createTabPane();
        createFloatingButton();
        loadListView();
        loadSelectedUser();
        editUser();

        passwordField.addEventFilter(KeyEvent.KEY_TYPED, RadarFilters.numberLetterFilter());
        dniField.addEventFilter(KeyEvent.KEY_TYPED, RadarFilters.numberFilter());

        cancelGroupButton.setOnAction(event -> {
            printGroupReport.setVisible(true);
            printGroupReport.setFocusTraversable(false);
            setTabGroupFields();
            loadAllEmpGroupListView(null);
            editGroup.setVisible(true);
            filterFieldGroupUser.setVisible(false);
         });
    }

    @Override
    protected void onBackController() {
        barPane.setEffect(null);
        super.onBackController();
    }

    private void loadListView() {

        try {
            loadUserListView();
            nonUserInfo();
            setFilterUser();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadUserListView() throws IOException {

        userList = service.getAllUserByCompany(getCompany());

        empData = FXCollections.observableArrayList();

        for(User user: userList) {
            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            VBox nameDniVBox = new VBox();

            ImageView iconUser = new ImageView(getClass()
                    .getResource("img/policeman_64.png").toExternalForm());
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/popup.fxml"));
            InputController inputController = new InputController(this, 1);
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
                    selectedUser = user;
                    popup.show(parentHBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
                }
            });
        }
        employeeListView.setItems(empData);

    }

    private void createFloatingButton() {
        floatingButton = new JFXButton("+");
        floatingButton.setButtonType(JFXButton.ButtonType.RAISED);
        floatingButton.getStyleClass().addAll("floatingButton");
        floatingButton.setLayoutX(230);
        floatingButton.setLayoutY(525);
        anchorPane.getChildren().add(floatingButton);
        floatingButton.setTooltip(
                new Tooltip("Agregar empleado")
        );

        floatingButton.setOnMouseClicked(event -> {
            if ((int) tabPane.getSelectionModel().getSelectedItem().getUserData() == 0) {
                setAddUserFields();
            } else if ((int) tabPane.getSelectionModel().getSelectedItem().getUserData() == 1) {
                setCreateGroupFields();
                loadAllEmpGroupListView(null);
                filterUserGroup();
                filterFieldGroupUser.clear();
            }
        });
    }

    private void setEditGroupFields() {
        editGroupLabel.setVisible(true);
        employeeLabel.setVisible(false);
        editLabel.setVisible(false);
        addLabel.setVisible(false);
        createGroupLabel.setVisible(false);
        groupLabel.setVisible(false);

        filterFieldGroupUser.clear();
        groupNameField.setDisable(false);
        groupNameField.setEditable(true);
        dniField.setVisible(false);
        dniField.setDisable(true);
        nameField.setVisible(false);
        nameField.setDisable(true);
        lastNameField.setVisible(false);
        lastNameField.setDisable(true);
        passwordField.setVisible(false);
        passwordField.setDisable(true);

        editButton.setVisible(false);
        saveButton.setVisible(true);
        saveButton.setDisable(false);
        cancelGroupButton.setDisable(false);
    }

    private void setCreateGroupFields() {
        createGroupLabel.setVisible(true);
        groupLabel.setVisible(false);
        employeeLabel.setVisible(false);
        editLabel.setVisible(false);
        addLabel.setVisible(false);

        editGroup.setVisible(false);
        filterFieldGroupUser.setVisible(true);
        filterFieldGroupUser.setDisable(false);
        groupNameField.clear();
        groupNameField.setDisable(false);
        groupNameField.setEditable(true);
        dniField.setVisible(false);
        dniField.setDisable(true);
        nameField.setVisible(false);
        nameField.setDisable(true);
        lastNameField.setVisible(false);
        lastNameField.setDisable(true);
        passwordField.setVisible(false);
        passwordField.setDisable(true);
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

    private void loadSelectedUser() {

        employeeListView.setOnMouseClicked(event -> {

            if (event.getButton() == MouseButton.PRIMARY
                    && employeeListView.getSelectionModel().getSelectedItem() != null) {
                editingPassword = false;
                nonEditableUser();

                selectedUser = (User) employeeListView
                        .getSelectionModel().getSelectedItem().getUserData();

                if (!selectedUser.getActive()) {
                    nonUserInfo();
                    return;
                }

                dniField.setText(selectedUser.getDni());
                nameField.setText(selectedUser.getName());
                lastNameField.setText(selectedUser.getLastname());
                passwordField.setText(selectedUser.getPassword());

                passwordField.setOnMousePressed(event1 -> {
                    if (!editingPassword) {
                        editingPassword = true;
                        passwordField.setText("");
                    }
                });
            }
        });
    }

    private void editUser() {
        if(employeeListView.isVisible()) {
            editButton.setOnAction(event -> {
                editableUser();
            });
        }
    }

    private void initializeFields() {
        employeeLabel.setVisible(true);
        createGroupLabel.setVisible(false);
        editLabel.setVisible(false);
        editGroupLabel.setVisible(false);
        addLabel.setVisible(false);
        groupLabel.setVisible(false);

        filterFieldGroupUser.setVisible(false);
        editGroup.setVisible(false);
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

    @FXML
    public void saveAction(ActionEvent event) {
        if ((int) tabPane.getSelectionModel().getSelectedItem().getUserData() == 0) {
            if (editLabel.isVisible()) {
                saveEditEmployee();
            } else if (addLabel.isVisible()) {
                saveEmployee();
            }
        } else if ((int) tabPane.getSelectionModel().getSelectedItem().getUserData() == 1) {
            if (createGroupLabel.isVisible()) {
                saveGroup();
            } else if (editGroupLabel.isVisible()) {
                saveEditedGroup(selectedGroup);
            }
        }
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

    private void editGroup() {
        editGroup.setOnMouseClicked(event -> {
            if(!groupNameField.getText().isEmpty()) {
                printGroupReport.setVisible(false);
                setEditGroupFields();
                loadAllEmpGroupListView(selectedGroup);
                filterUserGroup();
            }else {
                showDialogNotification("Seleccione un Grupo!",
                        "Debe seleccionar un grupo para editar");
            }
        });
    }

    private void setTabGroupFields() {
        groupLabel.setVisible(true);
        createGroupLabel.setVisible(false);
        employeeLabel.setVisible(false);
        editLabel.setVisible(false);
        editGroupLabel.setVisible(false);
        addLabel.setVisible(false);

        dniField.clear();
        nameField.clear();
        lastNameField.clear();
        passwordField.clear();
        groupNameField.clear();
        filterFieldGroupUser.clear();

        groupNameField.setVisible(true);
        groupNameField.setDisable(true);
        dniField.setVisible(false);
        dniField.setDisable(true);
        nameField.setVisible(false);
        nameField.setDisable(true);
        lastNameField.setVisible(false);
        lastNameField.setDisable(true);
        passwordField.setVisible(false);
        passwordField.setDisable(true);
        empGroupListView.setVisible(true);
        //empGroupListView.getItems().clear();

        editButton.setVisible(false);

        saveButton.setVisible(true);
        saveButton.setDisable(true);
        saveButton.setLayoutY(490);
        saveButton.setLayoutX(40);
        saveButton.setPrefWidth(220);
        cancelGroupButton.setVisible(true);
        cancelGroupButton.setDisable(true);
        cancelGroupButton.setLayoutY(490);
        cancelGroupButton.setLayoutX(300);
        cancelGroupButton.setPrefWidth(220);

        floatingButton.setStyle("-fx-background-color: #ffc107");
    }

    private void loadGroupListView() {
        groupList = service.getAllGroup();

        groupData = FXCollections.observableArrayList();

        for (Group group: groupList) {

            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            HBox groupNameHBox = new HBox();

            ImageView iconGroup = null;
            iconGroup = new ImageView(new Image(getClass()
                    .getResource("img/group1_64.png").toExternalForm()));
            iconGroup.setFitHeight(55);
            iconGroup.setFitWidth(55);
            Label groupNameLabel = new Label("    "+group.getName());
            groupNameLabel.setFont(new Font(null,16));

            imageHBox.getChildren().add(iconGroup);
            groupNameHBox.getChildren().add(groupNameLabel);
            parentHBox.getChildren().addAll(imageHBox, groupNameHBox);
            parentHBox.setUserData(group);
            groupData.add(parentHBox);

            try {
                createPopup(group, parentHBox);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        groupListView.setItems(groupData);
        floatingButton.setStyle("-fx-background-color: #ffc107");

        groupListView.setOnMouseClicked(event -> {
            groupListViewClick(event);
        });
    }

    public void createPopup(Group group, HBox parentHBox) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/popup.fxml"));
        InputController inputController = new InputController(this,2);
        loader.setController(inputController);
        JFXPopup popup = new JFXPopup(loader.load());
        inputController.setPopup(popup);
        if (group.getActive()) {
            inputController.setText("Borrar");
        } else {
            inputController.setText("Activar");
        }


        parentHBox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                selectedGroup = group;
                popup.show(parentHBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);

            }
        });
    }

    public void groupListViewClick(MouseEvent event) {

        if (groupListView.getSelectionModel().getSelectedItem() != null) {

            groupListViewClick(event, (Group) groupListView
                    .getSelectionModel().getSelectedItem().getUserData());
        }
    }

    public void groupListViewClick(MouseEvent event, Group group) {

        if (event.getButton() == MouseButton.PRIMARY) {
            filterFieldGroupUser.setDisable(false);
            filterFieldGroupUser.setVisible(true);
            filterFieldGroupUser.clear();
            printGroupReport.setVisible(true);
            selectedGroup = group;

            groupNameField.setText(selectedGroup.getName());
            userList = service.findUsersByGroupIdAndCompany(selectedGroup.getId(), getCompany());

            ImageView printGroupIcon = new ImageView(new Image(getClass().getResource("img/printer.png").toExternalForm()));
            printGroupReport.setGraphic(printGroupIcon);
            ImageView editIcon = new ImageView(new Image(getClass().getResource("img/bluePencil_16.png").toExternalForm()));
            editGroup.setVisible(true);
            editGroup.setGraphic(editIcon);

            setTabGroupFields();
            loadEmpGroupListView();
            filterUserGroup();

        }
    }

    private void loadEmpGroupListView() {

        groupNameField.setText(selectedGroup.getName());
        groupNameField.setDisable(false);
        groupNameField.setEditable(false);
        groupNameField.setFocusTraversable(false);

        empGroupData = FXCollections.observableArrayList();

        empGroupData.addAll(userList);
        empGroupListView.setItems(empGroupData);

        empGroupListView.setCellFactory(lv -> {
            ListCell<User> cell = new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox hBox = createParentBox(user);
                        setGraphic(hBox);
                    }
                }
            };
            return cell ;
        });

    }

    private void loadAllEmpGroupListView(Group group) {

        editGroup.setVisible(false);
        empGroupList = service.findAllOrderByGroup(getCompany());

        empGroupData = FXCollections.observableArrayList();

        for(User user: empGroupList) {
            user.setSelected(false);
            if (user.getGroup() != null && group != null
                    && user.getGroup().getId().equals(group.getId())) {
                user.setSelected(true);
            }
        }

        empGroupData.addAll(empGroupList);
        empGroupListView.setItems(empGroupData);

        empGroupListView.setCellFactory(lv -> {
            ListCell<User> cell = new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty) {
                        setGraphic(null);
                        setStyle(null);

                    } else {
                        HBox hBox = createParentBox(user);
                        setGraphic(hBox);

                        if (user.isSelected()) {
                            setStyle("-fx-background-color: #03A9F4;");
                            ((ArrayList<Label>) hBox.getUserData()).get(0).setStyle("-fx-text-fill: white");
                            ((ArrayList<Label>) hBox.getUserData()).get(1).setStyle("-fx-text-fill: white");
                        } else {
                            setStyle("-fx-background-color: white;");
                            ((ArrayList<Label>) hBox.getUserData()).get(0).setStyle("-fx-text-fill: black");
                            ((ArrayList<Label>) hBox.getUserData()).get(1).setStyle("-fx-text-fill: #aaaaaa");
                        }
                    }
                }
            };

            cell.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY
                        && cell.getItem() != null) {
                    cell.getItem().setSelected(!cell.getItem().isSelected());
                    empGroupListView.refresh();
                }
            });

            return cell ;
        });
    }

    private HBox createParentBox(User user) {
        HBox parentHBox = new HBox();
        HBox imageHBox = new HBox();
        VBox nameDniVBox = new VBox();
        HBox groupHBox = new HBox();

        ImageView iconUser = new ImageView(new Image(getClass().getResource("img/policeman_32.png").toExternalForm()));
        ImageView iconGroup = new ImageView(new Image(getClass().getResource("img/group_16.png").toExternalForm()));

        if (!user.getActive()) {
            ColorAdjust desaturate = new ColorAdjust();
            desaturate.setSaturation(-1);
            iconUser.setEffect(desaturate);
        }

        Label fullNameUser = new Label("    "+user.getLastname()+"  "+user.getName());
        Label dniUser = new Label("    "+user.getDni());

        if(user.getGroup() != null) {
            Label groupLabel = new Label("   "+user.getGroup().getName());
            groupLabel.setTextFill(Color.web("black"));
            groupHBox.setAlignment(Pos.CENTER);

            if(editGroupLabel.isVisible()) {
                try {
                    groupHBox.getChildren().addAll(iconGroup, groupLabel);

                } catch (NullPointerException e ){
                }
            }else {
                groupHBox.getChildren().addAll( iconGroup, groupLabel);
            }
        }

        fullNameUser.setFont(new Font(null,16));
        dniUser.setFont(new Font(null,14));
        dniUser.setTextFill(valueOf("#aaaaaa"));
        imageHBox.setPadding(new Insets(5));
        imageHBox.getChildren().add(iconUser);
        nameDniVBox.getChildren().add(fullNameUser);
        nameDniVBox.getChildren().add(dniUser);
        nameDniVBox.setPrefWidth(360);

        parentHBox.getChildren().addAll(imageHBox, nameDniVBox, groupHBox);

        List<Label> arrayLabels = new ArrayList<>();
        arrayLabels.add(fullNameUser);
        arrayLabels.add(dniUser);
        parentHBox.setUserData(arrayLabels);

        return parentHBox;
    }

    private void saveEmployee() {
        if ((nameField.getText().isEmpty() || lastNameField.getText().isEmpty())
                || passwordField.getText().isEmpty()
                || dniField.getText().isEmpty()) {

            dialogType = Const.DIALOG_NOTIFICATION;
            showDialogNotification("Campo vacio!",
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
                            + lastNameField.getText() + " " + nameField.getText() + "?");
        }
    }

    private void saveGroup() {
        if(groupNameField.getText().isEmpty()) {
            showDialog("Nombre de grupo vacio",
                    "Debe asignar un nombre al grupo");
        } else {
            Group group = new Group();
            group.setName(groupNameField.getText());
            group.setCreateDate(new DateTime().getMillis());
            group.setLastUpdate(new DateTime().getMillis());
            group.setActive(true);
            service.saveGroup(group);

            for(User user : empGroupData) {
                if(user.isSelected()){
                    user.setGroup(group);
                }
            }
            service.doEdit();
            showSnackBar("Grupo creado con exito");
            loadGroupListView();
            setTabGroupFields();
        }
    }

    public void saveEditEmployee() {
        if ((nameField.getText().isEmpty() || lastNameField.getText().isEmpty())
                || dniField.getText().isEmpty()
                || passwordField.getText().isEmpty()) {

            dialogType = Const.DIALOG_NOTIFICATION;
            showDialogNotification("Campo vacio!",
                    "Debe llenar todos los campos para la modificacion del usuario");
        } else if (editingPassword && passwordField.getText().length() < 4) {
            dialogType = Const.DIALOG_NOTIFICATION;
            showDialogNotification("Error de contraseña",
                    "La contraseña debe tener al menos 4 caracteres");
        } else if (dniField.getText().length() < 5) {
            dialogType = Const.DIALOG_NOTIFICATION;
            showDialogNotification("Error de dni",
                    "La dni es muy corto");
        } else if (!dniField.getText().equals(selectedUser.getDni()) && service.findUserByDni(dniField.getText()) != null) {
            dialogType = Const.DIALOG_NOTIFICATION;
            showDialogNotification("Error de dni",
                    "El dni ya esta siendo usado para otro empleado");
        } else {
            dialogType = Const.DIALOG_SAVE_EDIT;
            showDialog("¿Desea Guardar los cambios?",
                    "¿Seguro que desea modificar el usuario: "+selectedUser.getLastname()+" "+selectedUser.getName()+"?");
        }
    }

    public void saveEditedGroup(Group group) {
        if(groupNameField.getText().isEmpty()) {
            showDialog("Nombre de grupo vacio",
                    "Debe asignar un nombre al grupo");
        } else {
            group.setName(groupNameField.getText());
            group.setLastUpdate(new DateTime().getMillis());
            group.setActive(true);

            for (User user : empGroupData) {
                if (user.isSelected()) {
                    user.setGroup(group);
                } else {
                    if (user.getGroup() != null
                            && user.getGroup().getId().equals(group.getId())) {
                        user.setGroup(null);
                    }
                }
            }
            service.doEdit();
            showSnackBar("Grupo editado con exito");
            loadGroupListView();
            groupListViewClick(clickPrimaryMouseButton(), group);
        }
    }

    private void createTabPane() {
        employeeListView = new JFXListView();
        //employeeListView.setExpanded(true);
        employeeListView.setVerticalGap(2.0);
        employeeListView.depthProperty().set(1);

        Tab empTab = new Tab();
        empTab.setText("Empleados");
        empTab.setContent(employeeListView);
        empTab.setUserData(0);

        groupListView = new JFXListView<>();
        //groupListView.setExpanded(true);
        groupListView.setVerticalGap(2.0);
        groupListView.depthProperty().set(1);

        Tab groupTab = new Tab();
        groupTab.setText("Grupos");
        groupTab.setContent(groupListView);
        groupTab.setUserData(1);
        printGroupReport.setOnAction(event -> printReport());

        AssignController assignController = new AssignController();
        Tab assignTab = new Tab();
        assignTab.setText("Asignar");

        assignTab.setUserData(2);


        tabPane.getTabs().addAll(empTab, groupTab, assignTab);

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, t0, t1) -> {

            if((int) t1.getUserData() == 0) {
                initializeFields();
                setFilterUser();

                floatingButton.setTooltip(
                        new Tooltip("Agregar empleado")
                );
            } else if((int)t1.getUserData() == 1){
                setTabGroupFields();
                loadGroupListView();
                editGroup();

                setFilterGroup();
                floatingButton.setTooltip(
                        new Tooltip("Agregar grupo")
                );
            } else if ((int)t1.getUserData() == 2){
                new Timer().schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                cancel();
                                Platform.runLater(() -> {
                                    try {
                                        actionHandler.handle("assign");
                                    } catch (VetoException e) {
                                        e.printStackTrace();
                                    } catch (FlowException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }, 80, 80

                );


            }
        });

    }

    public void loadPrint(File file){
        dialogLoadingPrint();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("group_name", selectedGroup.getName());
        parameters.put("date_created", RadarDate.getDateWithMonthAndTime(selectedGroup.getCreateDate()));
        parameters.put("group_id", selectedGroup.getId().toString());
        parameters.put("emp_num", selectedGroup.getUsers().size());


        //routeReport.setTime(RadarDate.getDateWithMonthAndTime(selectedRoute.getCreateDate()));
        List<GroupReport> groupReportList = new ArrayList<>();

        for(User userData: empGroupData) {

            GroupReport groupReport = new GroupReport();
            groupReport.setDni(userData.getDni());
            groupReport.setUser_name(userData.getFullName());
            groupReport.setCompany_name(userData.getCompany().getName());

            groupReportList.add(groupReport);
        }

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Runnable worker = new PrintGroupReportTask(this,
                new JRBeanCollectionDataSource(groupReportList),file,
                "Group_"+selectedGroup.getId().toString(), parameters);
        executor.execute(worker);
        executor.shutdown();
    }

    public void printReport() {
        dialogType = Const.DIALOG_PRINT_BASIC;
        showDialogPrint("Debe seleccionar una ruta para guardar el reporte");
    }



    void setFilterUser() {
        filterUser();
        filterField.clear();
        filterField.setVisible(true);
        filterFieldGroup.setVisible(false);
    }


    void setFilterGroup() {
        filterGroup();
        filterFieldGroup.setOnMouseClicked(event -> {
            filterFieldGroup.clear();
            filterFieldGroupUser.clear();
            loadAllEmpGroupListView(null);
            filterFieldGroupUser.setVisible(false);
            setTabGroupFields();
        });
        filterFieldGroup.clear();
        filterField.setVisible(false);
        filterFieldGroup.setVisible(true);
    }

    public void deleteUser() {
        User user = selectedUser;
        if (selectedUser.getActive()) {
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

    public void deleteGroup() {
        Group group = selectedGroup;
        if(selectedGroup.getActive()) {
            dialogType = Const.DIALOG_DELETE;
            showDialog("Confirmacion",
                    "¿Seguro que desea borrar este grupo: " +group.getName()+"? \n" +
                            "Todos Los empleados relacionados con este grupo, incluyendo los de otras empresas,\nquedaran sin grupo y disponibles " +
                            "para ser asignados a otro grupo.");
        } else {
            dialogType = Const.DIALOG_ENABLE;
            showDialog("Confirmacion",
                    "¿Seguro que desea reactivar el grupo: " +group.getName()+"?");
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

    void filterUserGroup() {
        filterFieldGroup.clear();
        FilteredList<User> filteredData = new FilteredList<>(empGroupData, p -> true);
        filterFieldGroupUser.textProperty().addListener((observable, oldValue, newValue) -> {
            //nonUserInfo();
            filteredData.setPredicate(user -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                if (user == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = filterFieldGroupUser.getText().toLowerCase();

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

        SortedList<User> sortedData = new SortedList<>(filteredData);
        empGroupListView.setItems(sortedData);
        checkEmpGroupFilter(filteredData);
    }

    void checkEmpGroupFilter(FilteredList<User> filteredData){
        filteredData.setPredicate(user -> {
            if(filterFieldGroupUser.getText() == null || filterFieldGroupUser.getText() == null ) {
                return  true;
            }
            String lowerCaseFilter = filterFieldGroupUser.getText().toLowerCase();

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

    private void filterGroup() {

        FilteredList<HBox> filteredData = new FilteredList<>(groupData, p -> true);
        filterFieldGroup.textProperty().addListener((observable, oldValue, newValue) -> {
            //nonUserInfo();

            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                Group group = (Group) hBox.getUserData();
                if (group == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = filterFieldGroup.getText().toLowerCase();

                if (group.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        groupListView.setItems(sortedData);
        checkGroupFilter(filteredData);
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

    void checkGroupFilter(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (filterFieldGroup.getText() == null || filterFieldGroup.getText().isEmpty()) {
                return true;
            }

            Group group = (Group) hBox.getUserData();
            if (group == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = filterFieldGroup.getText().toLowerCase();

            if (group.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            }
            return false; // Does not match.
        });
    }

    @Override
    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);
        User user;
        Group group;
        switch (dialogType) {
            case Const.DIALOG_PRINT_BASIC:
            File file = selectDirectory();
            if(file != null){
                loadPrint(file);
            }
            break;
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
                user.setCompany(service.findCompanyById(getCompany().getId()));
                service.saveUser(user);
                loadListView();
                showSnackBar("Empleado creado con exito");
                break;
            case Const.DIALOG_DELETE:

                if(employeeLabel.isVisible()){
                    user = service.findUserById(selectedUser.getId());
                    if (service.deleteUser(user)) {
                        showSnackBar("Empleado borrado con exito");
                    } else {
                        showSnackBar("No se puede borrar porque " +
                                "tiene registros, el empleado ha sido desactivado.");
                    }
                    loadListView();
                }

                if(groupLabel.isVisible()) {
                    group = service.findGroupById(selectedGroup.getId());
                    userList = service.findUserByGroupId(selectedGroup.getId());

                    for (User userGroupId : userList)
                        userGroupId.setGroup(null);
                    service.doEdit();

                    if(service.deleteGroup(group)) {
                        showSnackBar("Grupo borrado con exito");
                    }

                    setTabGroupFields();
                    loadGroupListView();

                }


                break;
            case Const.DIALOG_ENABLE:
                user = service.findUserById(selectedUser.getId());
                user.setActive(true);
                service.doEdit();
                loadListView();
                showSnackBar("Empleado activado con exito");
                break;
        }
    }

    @Override
    public void mapInitialized() {

    }

    @Override
    public void mapReady() {

    }

    @Override
    public void handle(MouseEvent event) {

    }

    @Override
    public void onPrintCompleted() {
        closeDialogLoading();
        showSnackBar("Guardado completado");
    }

    @Override
    public void onPrintFailure(String message) {
        closeDialogLoading();
        showSnackBar("Guardado fallido");
    }

    public class InputController {

        @FXML
        private JFXListView<?> toolbarPopupList;

        @FXML
        private Label popupLabel;

        private UserController principal;

        private JFXPopup popup;

        private Integer popupType;

        public InputController(UserController principal, Integer popupType) {
            this.principal = principal;
            this.popupType = popupType;
        }

        @FXML
        private void submit() {
            popup.hide();
            if (toolbarPopupList.getSelectionModel().getSelectedIndex() == 0) {
                if(popupType == 1){
                    principal.deleteUser();
                } else if (popupType == 2) {
                    principal.deleteGroup();

                }

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
