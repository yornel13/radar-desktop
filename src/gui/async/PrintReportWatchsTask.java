package gui.async;

import javafx.application.Platform;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class PrintReportWatchsTask implements Runnable {

    private PrintTask listener;
    private JRDataSource dataSource;
    private File file;
    private String fileName;
    private Map<String, Object> parameters;

    public PrintReportWatchsTask(PrintTask listener, JRDataSource dataSource, Map<String, Object> parameters, File file, String fileName) {
        this.listener = listener;
        this.dataSource = dataSource;
        this.file = file;
        this.fileName = fileName;
        this.parameters = parameters;
    }

    @Override
    public void run() {
        FileInputStream inputStreamMaster = null;
        FileInputStream inputStreamSub = null;

        try{
            inputStreamMaster = new FileInputStream("MyReports/master_watch_points.jrxml");
            inputStreamSub = new FileInputStream("MyReports/sub_watch_points.jrxml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            JasperDesign jasperDesignMaster = JRXmlLoader.load(inputStreamMaster);
            JasperDesign jasperDesignSub = JRXmlLoader.load(inputStreamSub);
            JasperReport jasperMasterReport = JasperCompileManager
                    .compileReport(jasperDesignMaster);
            JasperReport jasperSubReport = JasperCompileManager
                    .compileReport(jasperDesignSub);
            parameters.put("subreportParameter", jasperSubReport);
            JasperPrint jasperPrint  = JasperFillManager.fillReport(jasperMasterReport,
                    parameters, dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint,file.getPath() + "\\" + fileName +".pdf");
            System.out.println("Printed");
            Platform.runLater(() -> listener.onPrintCompleted());
        } catch (JRException ex) {
            ex.printStackTrace();
            Platform.runLater(() -> listener.onPrintFailure("Error"));
        }
    }

    public interface PrintTask {

        void onPrintCompleted();

        void onPrintFailure(String message);
    }
}
