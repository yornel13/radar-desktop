package gui.controller;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionTrigger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.RadarService;
import util.Const;

import javax.annotation.PostConstruct;
import java.io.*;

@ViewController("../view/sync.fxml")
public class SyncController extends BaseController {

    @FXML
    private JFXButton buttonImport;
    @FXML
    private JFXButton buttonExport;
    @FXML
    @ActionTrigger("map")
    private JFXButton buttonMap;

    @FXML
    @ActionTrigger("admin")
    private JFXButton optionAdmin;
    @FXML
    @ActionTrigger("employee")
    private JFXButton optionEmployee;
    @FXML
    @ActionTrigger("control")
    private JFXButton optionControl;


    @PostConstruct
    public void init() {
        buttonImport.setOnMouseEntered(event ->  buttonImport.setStyle(" -fx-background-color: #d6d6d6"));
        buttonImport.setOnMouseExited( event ->  buttonImport.setStyle(" -fx-background-color: #ffc107"));
        buttonExport.setOnMouseEntered(event ->  buttonExport.setStyle(" -fx-background-color: #d6d6d6"));
        buttonExport.setOnMouseExited( event ->  buttonExport.setStyle(" -fx-background-color: #ffc107"));
        buttonMap.setOnMouseEntered(event -> buttonMap.setStyle(" -fx-background-color: #d6d6d6"));
        buttonMap.setOnMouseExited( event -> buttonMap.setStyle(" -fx-background-color: #ffc107"));

        try {
            optionAdmin.setGraphic(new ImageView(new Image(new FileInputStream("src/img/admin_32.png"))));
            optionEmployee.setGraphic(new ImageView(new Image(new FileInputStream("src/img/employee_32.png"))));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
        successful = RadarService.getInstance().saveImport(json);
        if (successful) {
            // TODO, show dialog successful
        } else {
            // TODO, show dialog process error
        }

    }

    public void exportFile(ActionEvent actionEvent) {

        Boolean successful = false;

        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(new File(System
                .getProperty(Const.EXPORT_DEFAULT_DIRECTORY)));
        File file = fileChooser.showDialog(null);

        File filePath = new File(file, Const.EXPORT_FILE_NAME);

        BufferedWriter bufferedWriter = null;
        try {
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(filePath));

            bufferedWriter.write(RadarService.getInstance().getExportJson());

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
            // TODO, show dialog successful
        } else {
            // TODO, show dialog process error
        }

    }
}

