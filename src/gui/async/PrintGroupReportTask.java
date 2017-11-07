package gui.async;

import javafx.application.Platform;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.File;
import java.util.Map;

public class PrintGroupReportTask implements Runnable {

    private PrintTask listener;
    private JRDataSource dataSource;
    private File file;
    private String fileName;
    private Map<String, Object> parameters;

    public PrintGroupReportTask(PrintTask listener, JRDataSource dataSource , File file, String fileName, Map<String, Object> parameters) {
        this.listener = listener;
        this.dataSource = dataSource;
        this.file = file;
        this.fileName = fileName;
        this.parameters = parameters;
    }

    @Override
    public void run() {
        try {
            JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("report/group_details.jrxml"));
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint  jasperPrint  = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint,file.getPath() + "\\" + fileName +".pdf");
            System.out.println("Printed");
            Platform.runLater(() -> listener.onPrintCompleted());
        } catch (JRException e) {
            e.printStackTrace();
            Platform.runLater(() -> listener.onPrintFailure("Error"));
        }
    }


    public interface PrintTask {

        void onPrintCompleted();

        void onPrintFailure(String message);
    }

}
