package gui;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.util.VetoException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import model.Company;
import util.Const;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;

@ViewController("view/start.fxml")
public class StartController extends BaseController {

    @FXML
    private JFXButton buttonImport;
    @FXML
    private JFXButton buttonExport;
    @FXML
    private Label companyName;
    @FXML
    private Label numeration;

    @FXML
    @ActionTrigger("admin")
    private JFXButton optionAdmin;
    @FXML
    @ActionTrigger("company")
    private JFXButton optionEnterprise;

    @FXML
    private ComboBox selector;

    private List<Company> companies;


    @PostConstruct
    public void init() {

        setTitle("Radar app");

        optionAdmin.setGraphic(new ImageView(new Image(getClass().getResource("img/admin_32.png").toExternalForm())));
        optionEnterprise.setGraphic(new ImageView(new Image(getClass().getResource("img/enterprise_32.png").toExternalForm())));

        loadCompany();
    }

    private void loadCompany() {
        company = service.getCompany();
        if (company != null) {
            companyName.setText(company.getName());
            numeration.setText(company.getNumeration());
        }
    }

    private void loadCompanies() {
        companies = service.getAllCompaniesActive();
        String[] items = new String[companies.size()];

        companies.stream().forEach((company) -> {
            items[companies.indexOf(company)] = company.getName();
        });

        selector.setItems(FXCollections.observableArrayList(items));
    }

    public void importFile(ActionEvent actionEvent) {
        final FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(null);
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("extension", ".json"));

        if (file == null) {
            System.err.println("path is no selected");
            return;
        }

        FileReader fr = null;
        String json = "";
        try {
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;

            if(file.exists()) {

               while((line = br.readLine()) != null) {
                   json += line+"\n";
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                    jsonToObject(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void jsonToObject(String json) {
        Boolean successful;
        successful = service.saveImport(json);
        if (successful) {
            showSnackBar("Informacion guardada en la base de datos con exito");
        } else {
            showSnackBar("Error de guardado de informacion!");
        }

    }

    public void exportFile(ActionEvent actionEvent) {

        Boolean successful = false;

        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(new File(System
                .getProperty(Const.EXPORT_DEFAULT_DIRECTORY)));
        File file = fileChooser.showDialog(null);
        if (file == null)
            return;

        File filePath = new File(file, Const.EXPORT_FILE_NAME);

        BufferedWriter bufferedWriter = null;
        try {
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(filePath));

            bufferedWriter.write(service.getExportJson());

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Closing the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    successful = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (successful) {
            showSnackBar(Const.EXPORT_FILE_NAME+" creado con exito en la ruta: "+filePath);
        } else {
            showSnackBar("error al intentar exporta la informacion!");
        }
    }

    public void onSelectCompany(ActionEvent event) {

    }

    public void onEnter(ActionEvent event) throws VetoException, FlowException {
        /*if (!companies.isEmpty() && !selector.getSelectionModel().isEmpty()) {
            flowContext.register("company", companies.get(selector.getSelectionModel().getSelectedIndex()));
            actionHandler.handle("sync");
        }  else {
            showSnackBar("Selecciona una empresa primero.");
        }*/
        if (company != null) {
            flowContext.register("company", company);
            actionHandler.handle("sync");
        }  else {
            showSnackBar("Se deben crear primero los detalles de la empresa.");
        }
    }
}

