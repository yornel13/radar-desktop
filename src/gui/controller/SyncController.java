package gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.RadarService;
import util.HibernateSessionFactory;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SyncController implements Initializable {
    @FXML
    private JFXButton employeeBtn;
    @FXML
    private JFXButton btnImport;
    @FXML
    private TextArea txtArea;
    @FXML
    JFXDrawer drawer;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
        try {
            employeeBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/img/admin_64.png"))));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        */
        HibernateSessionFactory.getConfiguration().configure();
    }

    public void importFile(ActionEvent actionEvent) {
        final FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(null);
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("extension", ".json"));

        if (file == null) {
            System.out.println("path is no selected");
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

    }

    public void exportFile(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);

        if(file != null) {
            FileWriter fw;
            PrintWriter pw = null;
            try {
                fw = new FileWriter("",false);
                pw = new PrintWriter(fw);
                String cadena = "dsdsadsadas";

                pw.println(cadena);

                pw.flush();
            }catch (IOException e) {
                e.printStackTrace();
            }
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
}

