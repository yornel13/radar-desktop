package gui.async;

import javafx.application.Platform;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrintReportGlobalDetailRoute implements Runnable {

    private PrintReportWatchsTask.PrintTask listener;
    private JRDataSource dataSourceGroups;
    private JRDataSource dataSourceRoute;
    private File file;
    private String fileName;
    private Map<String, Object> parameters;

    public PrintReportGlobalDetailRoute(PrintReportWatchsTask.PrintTask listener,
                                        JRDataSource dataSourceGroups, JRDataSource dataSourceRoute, Map<String, Object> parameters, File file, String fileName) {
        this.listener = listener;
        this.dataSourceGroups = dataSourceGroups;
        this.dataSourceRoute = dataSourceRoute;
        this.file = file;
        this.fileName = fileName;
        this.parameters = parameters;
    }

    @Override
    public void run() {

        try {

            JasperPrint jasperPrintRoute;
            {
                JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("report/route_details.jrxml"));
                JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
                jasperPrintRoute  = JasperFillManager.fillReport(jasperReport, parameters, dataSourceRoute);
            }

            JasperDesign jasperDesignMaster = JRXmlLoader.load(getClass().getResourceAsStream("report/master_group_report.jrxml"));
            JasperDesign jasperDesignSub = JRXmlLoader.load(getClass().getResourceAsStream("report/sub_group.jrxml"));
            JasperReport jasperMasterReport = JasperCompileManager
                    .compileReport(jasperDesignMaster);
            JasperReport jasperSubReport = JasperCompileManager
                    .compileReport(jasperDesignSub);
            parameters.put("subReport", jasperSubReport);
            JasperPrint jasperPrint  = JasperFillManager.fillReport(jasperMasterReport,
                    parameters, dataSourceGroups);

            for (JRPrintPage page: jasperPrint.getPages()) {
                jasperPrintRoute.addPage(page);
            }

            JasperExportManager.exportReportToPdfFile(jasperPrintRoute,file.getPath() + "\\" + fileName +".pdf");
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

