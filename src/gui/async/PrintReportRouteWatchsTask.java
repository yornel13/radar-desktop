package gui.async;

import javafx.application.Platform;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.File;
import java.util.Map;

public class PrintReportRouteWatchsTask implements Runnable {

    private PrintTask listener;
    private JRDataSource dataSource;
    private File file;
    private String fileName;
    private Map<String, Object> parameters;

    public PrintReportRouteWatchsTask(PrintTask listener, JRDataSource dataSource, Map<String, Object> parameters, File file, String fileName) {
        this.listener = listener;
        this.dataSource = dataSource;
        this.file = file;
        this.fileName = fileName;
        this.parameters = parameters;
    }

    @Override
    public void run() {

        try {
            JasperDesign jasperDesignMaster = JRXmlLoader.load(getClass().getResourceAsStream("report/route_master_watchs.jrxml"));
            JasperDesign jasperDesignSub = JRXmlLoader.load(getClass().getResourceAsStream("report/sub_route.jrxml"));
            JasperReport jasperMasterReport = JasperCompileManager
                    .compileReport(jasperDesignMaster);
            JasperReport jasperSubReport = JasperCompileManager
                    .compileReport(jasperDesignSub);
            parameters.put("subReport", jasperSubReport);
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
