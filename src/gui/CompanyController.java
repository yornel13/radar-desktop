package gui;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Company;
import util.Const;
import util.RadarFilters;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@ViewController("view/company.fxml")
public class CompanyController extends BaseController implements EventHandler<MouseEvent> {

    @FXML
    private JFXListView<HBox> companyListView;

    private ObservableList<HBox> dataCompany;

    private List<Company> companies;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane barPane;
    @FXML
    private JFXTextField filterField;
    @FXML
    private Label companyLabel;
    @FXML
    private Label editLabel;
    @FXML
    private Label addLabel;

    @FXML
    private JFXTextField companyNameField;

    @FXML
    private JFXTextField acronymField;

    @FXML
    private JFXTextField numerationField;

    @FXML
    private JFXButton editButton;
    @FXML
    private JFXButton saveButton;

    private Company selectCompany;

    private boolean  editingPassword = false;

    @PostConstruct
    public void init() {

        setTitle("Empresas");
        setBackButtonImageWhite();

        loadListView();
        loadSelectedCompany();
        editAdmin();
        addCompany();

        numerationField.addEventFilter(KeyEvent.KEY_TYPED, RadarFilters.numberFilter());

        selectCompany = service.getCompany();
        if (selectCompany != null) {
            loadCompany(selectCompany);
        } else {
            createCompany();
        }

    }

    @Override
    protected void onBackController() {
        barPane.setEffect(null);
        super.onBackController();
    }

    public void loadListView() {
        companies = service.getAllCompanies();
        try {
            loadAdminListView();
            nonCompanyInfo();
            filterCompany();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadAdminListView() throws Exception {

        dataCompany = FXCollections.observableArrayList();

        for(Company company: companies) {
            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            VBox nameDniVBox = new VBox();


            ImageView iconAdmin = new ImageView(new Image(getClass().getResource("img/edifice_64.png").toExternalForm()));
            if(!company.getActive()) {
                ColorAdjust desaturate = new ColorAdjust();
                desaturate.setSaturation(-1);
                iconAdmin.setEffect(desaturate);
            }
            iconAdmin.setFitHeight(55);
            iconAdmin.setFitWidth(55);
            Label fullName = new Label("    "+company.getName());
            Label numeration = new Label("    "+company.getNumeration());
            fullName.setFont(new Font(null,16));
            numeration.setFont(new Font(null,14));
            numeration.setTextFill(Color.valueOf("#aaaaaa"));
            imageHBox.getChildren().add(iconAdmin);
            nameDniVBox.getChildren().add(fullName);
            nameDniVBox.getChildren().add(numeration);
            parentHBox.getChildren().addAll(imageHBox, nameDniVBox);

            parentHBox.setUserData(company);
            dataCompany.add(parentHBox);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/popup.fxml"));
            InputController inputController = new InputController(this);
            loader.setController(inputController);
            JFXPopup popup = new JFXPopup(loader.load());
            inputController.setPopup(popup);
            if(company.getActive()) {
                inputController.setText("Borrar");
            }else {
                inputController.setText("Activar");
            }

            parentHBox.setOnMouseClicked(event -> {
                if(event.getButton() == MouseButton.SECONDARY) {
                    nonCompanyInfo();
                    selectCompany = company;
                    popup.show(parentHBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
                }
            });
        }
        companyListView.setItems(dataCompany);
        companyListView.setExpanded(true);
        companyListView.setVerticalGap(2.0);
        companyListView.depthProperty().set(1);

    }

    void editableCompany() {

        companyLabel.setVisible(false);
        editLabel.setVisible(true);
        addLabel.setVisible(false);

        companyNameField.setEditable(true);
        companyNameField.setDisable(false);
        acronymField.setEditable(true);
        acronymField.setDisable(false);
        numerationField.setEditable(true);
        numerationField.setDisable(false);

        editButton.setDisable(true);
        saveButton.setDisable(false);
    }

    void nonEditableCompany() {

        companyLabel.setVisible(true);
        editLabel.setVisible(false);
        addLabel.setVisible(false);

        companyNameField.setEditable(false);
        companyNameField.setDisable(true);
        acronymField.setEditable(false);
        acronymField.setDisable(true);
        numerationField.setEditable(false);
        numerationField.setDisable(true);

        editButton.setDisable(false);
        saveButton.setDisable(true);
    }

    void createCompany() {

        companyLabel.setVisible(false);
        editLabel.setVisible(false);
        addLabel.setVisible(true);

        companyNameField.clear();
        acronymField.clear();
        numerationField.clear();

        companyNameField.setEditable(true);
        companyNameField.setDisable(false);
        acronymField.setEditable(true);
        acronymField.setDisable(false);
        numerationField.setEditable(true);
        numerationField.setDisable(false);

        editButton.setDisable(true);
        saveButton.setDisable(false);
    }

    void nonCompanyInfo() {

        companyLabel.setVisible(true);
        editLabel.setVisible(false);
        addLabel.setVisible(false);

        companyNameField.clear();
        acronymField.clear();
        numerationField.clear();

        companyNameField.setEditable(false);
        companyNameField.setDisable(true);
        acronymField.setEditable(false);
        acronymField.setDisable(true);
        numerationField.setEditable(false);
        numerationField.setDisable(true);

        editButton.setDisable(true);
        saveButton.setDisable(true);
    }


    @Override
    public void handle(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {

            if (companyListView.getSelectionModel().getSelectedIndex() == 0) {
                onBackController();
            }
        }
    }

    private  void loadSelectedCompany() {
        companyListView.setOnMouseClicked(event -> {

            if(event.getButton() == MouseButton.PRIMARY
                    && companyListView.getSelectionModel().getSelectedItem() != null) {
                editingPassword = false;

                nonEditableCompany();

                loadCompany((Company) companyListView.getSelectionModel().getSelectedItem().getUserData());
            }
        });
    }

    void loadCompany(Company company) {

        selectCompany = company;

        nonEditableCompany();

        companyNameField.setText(company.getName());
        acronymField.setText(company.getAcronym());
        numerationField.setText(company.getNumeration());
        saveChanges(company.getName(), company.getAcronym(), company.getNumeration());
    }

    private void editAdmin() {
        editButton.setOnAction(event -> {
            if(companyNameField.getText().isEmpty()
                    && numerationField.getText().isEmpty()
                    && acronymField.getText().isEmpty()) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Debe seleccionar una empresa!",
                        "Seleccione una empresa de la lista para modificar");
            } else {
                editableCompany();
            }
        });
    }

    private void saveChanges(String companyName, String acronym, String numeration) {
        saveButton.setOnAction(event -> {
            if ((companyNameField.getText().isEmpty()
                    || numerationField.getText().isEmpty())
                    || acronymField.getText().isEmpty()) {

                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Campo vacio",
                        "Debe llenar todos los campos para la modificacion de la empresa");
            } else if (!acronymField.getText().equals(acronym) && service.findCompanyByAcronym(acronymField.getText()) != null){
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Error en siglas de empresa",
                        "Estas siglas de empresa esta siendo usado por otro empresa");
            } else if (!numerationField.getText().equals(numeration) && service.findCompanyByNumeration(numerationField.getText()) != null) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Error de numeracion",
                        "La numeracion de la empresa ya esta siendo usada por otra empresa");
            } else {
                dialogType = Const.DIALOG_SAVE_EDIT;
                showDialog("¿Desea Guardar los cambios?",
                        "¿Seguro que desea modificar la empresa: "+companyName+"?");
            }

        });
    }

    private void addCompany() {
        /*JFXButton floatingButton = new JFXButton("+");
        floatingButton.setButtonType(JFXButton.ButtonType.RAISED);
        floatingButton.getStyleClass().addAll("floatingButton");
        floatingButton.setLayoutX(230);
        floatingButton.setLayoutY(525);
        anchorPane.getChildren().add(floatingButton);

        floatingButton.setOnAction(eventAction -> {
            createCompany();
            saveButton.setOnAction(event -> {
                if ((companyNameField.getText().isEmpty()
                        || numerationField.getText().isEmpty())
                        || acronymField.getText().isEmpty()) {

                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Campo vacio",
                            "Debe llenar todos los campos para la modificacion de la empresa");
                } else if (service.findCompanyByAcronym(acronymField.getText()) != null){
                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Error en siglas de empresa",
                            "Estas siglas de empresa esta siendo usado por otro empresa");
                } else if (service.findCompanyByNumeration(numerationField.getText()) != null) {
                    dialogType = Const.DIALOG_NOTIFICATION;
                    showDialogNotification("Error de numeracion",
                            "La numeracion de la empresa ya esta siendo usada por otra empresa");
                }  else {
                    dialogType = Const.DIALOG_SAVE;
                    showDialog("Confirmacion",
                            "¿Seguro que desea crear la empresa: "+companyNameField.getText()+"?");
                }
            });
        } );*/

        saveButton.setOnAction(event -> {
            if ((companyNameField.getText().isEmpty()
                    || numerationField.getText().isEmpty())
                    || acronymField.getText().isEmpty()) {

                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Campo vacio",
                        "Debe llenar todos los campos para la modificacion de la empresa");
            } else if (service.findCompanyByAcronym(acronymField.getText()) != null){
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Error en siglas de empresa",
                        "Estas siglas de empresa esta siendo usado por otro empresa");
            } else if (service.findCompanyByNumeration(numerationField.getText()) != null) {
                dialogType = Const.DIALOG_NOTIFICATION;
                showDialogNotification("Error de numeracion",
                        "La numeracion de la empresa ya esta siendo usada por otra empresa");
            }  else {
                dialogType = Const.DIALOG_SAVE;
                showDialog("Confirmacion",
                        "¿Seguro que desea crear la empresa: "+companyNameField.getText()+"?");
            }
        });
    }

    @Override
    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);
        Company company;
        switch (dialogType) {
            case Const.DIALOG_SAVE_EDIT:
                company = selectCompany;
                company.setName(companyNameField.getText());
                company.setAcronym(acronymField.getText());
                company.setNumeration(numerationField.getText());
                service.doEdit();
                loadCompany(selectCompany);
                showSnackBar("Empresa modificada con exito");
                break;
            case Const.DIALOG_SAVE:
                company = new Company();
                company.setName(companyNameField.getText());
                company.setAcronym(acronymField.getText());
                company.setNumeration(numerationField.getText());
                company.setActive(true);
                service.saveCompany(company);
                loadCompany(company);
                showSnackBar("Empresa creada con exito");
                break;
            case Const.DIALOG_DELETE:
                company = service.findCompanyById(selectCompany.getId());
                service.deleteCompany(company);
                nonCompanyInfo();
                showSnackBar("Empresa borrada");
                break;
        }


    }
    private void deleteCompany() {
        Company company = selectCompany;
        if (selectCompany.getActive()) {
            dialogType = Const.DIALOG_DELETE;
            showDialog("Confirmacion",
                    "¿Seguro que desea eliminar la empresa: "
                            + company.getName()+ "?");
        } else {
            dialogType = Const.DIALOG_ENABLE;
            showDialog("Confirmacion",
                    "¿Seguro que desea reactivar la empresa: "
                            + company+"?");
        }
    }

    private void filterCompany() {
        FilteredList<HBox> filteredData = new FilteredList<>(dataCompany, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            nonCompanyInfo();
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                Company company = (Company) hBox.getUserData();
                if (company == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = filterField.getText().toLowerCase();

                if (company.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (company.getAcronym().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (company.getNumeration().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        companyListView.setItems(sortedData);
        checkFilter(filteredData);
    }

    void checkFilter(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (filterField.getText() == null || filterField.getText().isEmpty()) {
                return true;
            }
            Company company = (Company) hBox.getUserData();
            if (company == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = filterField.getText().toLowerCase();

            if (company.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            } else if (company.getAcronym().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            } else if (company.getNumeration().toLowerCase().contains(lowerCaseFilter)) {
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

        private CompanyController principal;

        private JFXPopup popup;

        public InputController(CompanyController principal) {
            this.principal = principal;
        }

        @FXML
        private void submit() {
            popup.hide();
            if (toolbarPopupList.getSelectionModel().getSelectedIndex() == 0) {
                principal.deleteCompany();
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
