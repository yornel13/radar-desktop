package gui.async;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import javax.sql.DataSource;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;

public class PrintReportTask implements Runnable {

    private PrintTask listener;
    private JRDataSource dataSource;
    private File file;
    private String fileName;

    public PrintReportTask(PrintTask listener, JRDataSource dataSource, File file, String fileName) {
        this.listener = listener;
        this.dataSource = dataSource;
        this.file = file;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        InputStream inputStream = null;

        try{
            inputStream = new FileInputStream("MyReports/watch_points.jrxml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint  = JasperFillManager.fillReport(jasperReport, null, (Connection) dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint,file.getPath() + "\\" + fileName +".pdf");
            System.out.println("Printed");
            listener.onPrintCompleted();
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null,"Error al cargar fichero jrml jasper report "+ex.getMessage());
            listener.onPrintFailure("Error");
        }
    }

    public interface PrintTask {

        void onPrintCompleted();

        void onPrintFailure(String message);
    }
}
