package gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.RadarService;
import util.Const;
import util.HibernateSessionFactory;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SyncController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        HibernateSessionFactory.getConfiguration().configure();
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

    public void controlScene(ActionEvent actionEvent) throws IOException {
        Parent parentControl = FXMLLoader.load(getClass().getResource("../view/workman.fxml"));
        String css = StartApp.class.getResource("../style/style.css").toExternalForm();
        Scene sceneControl = new Scene(parentControl);
        sceneControl.getStylesheets().add(css);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(sceneControl);
        stage.show();

    }

    public void markerScene(ActionEvent actionEvent) {

    }
}

