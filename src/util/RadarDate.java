/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.joda.time.DateTime;

/**
 *
 * @author user 01
 */
public class RadarDate {
    
    public static LocalDate getLocalFromTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            DateTime dateTime = new DateTime(timestamp.getTime());
            return LocalDate.of(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
        }
    }
    
    public static LocalTime getLocalFromTime(Time time) {
        if (time == null) {
            return null;
        } else {
            DateTime dateTime = new DateTime(time.getTime());
            return LocalTime.of(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
        }
    }
    
    public static String getMonthName(int month){
        Calendar cal = Calendar.getInstance();
        // Calendar numbers months from 0
        cal.set(Calendar.MONTH, month - 1);
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }
    
    public static String getFechaConMes(DateTime dateTime) {
        String fecha = dateTime.getDayOfMonth() + " de " 
                + getMonthName(dateTime.getMonthOfYear()) 
                + " " + dateTime.getYear();
        return fecha;
    }
    
    public static String getFechaConMesSinAno(DateTime dateTime) {
        String fecha = dateTime.getDayOfMonth() + " de " 
                + getMonthName(dateTime.getMonthOfYear());
        return fecha;
    }
    
    public static String getFechaConMes(Date date) {
        DateTime dateTime = new DateTime(date.getTime());
        String fecha = dateTime.getDayOfMonth() + " de " 
                + getMonthName(dateTime.getMonthOfYear()) 
                + " " + dateTime.getYear();
        return fecha;
    }
    
    public static String getFechaConMes(Timestamp timestamp) {
        DateTime dateTime = new DateTime(timestamp.getTime());
        String fecha = dateTime.getDayOfMonth() + " de " 
                + getMonthName(dateTime.getMonthOfYear()) 
                + " " + dateTime.getYear();
        return fecha;
    }
    
    public static String getFechaConMesYHora(DateTime dateTime) {
        String fecha = dateTime.getDayOfMonth() + " de " 
                + getMonthName(dateTime.getMonthOfYear()) 
                + " " + dateTime.getYear() + " a las "
                + dateTime.toString("HH:mm:ss");
        return fecha;
    }
    
    public static String getFechaConMesYHora(Timestamp timestamp) {
        DateTime dateTime = new DateTime(timestamp.getTime());
        String fecha = dateTime.getDayOfMonth() + " de " 
                + getMonthName(dateTime.getMonthOfYear()) 
                + " " + dateTime.getYear() + " a las " 
                + dateTime.toString("HH:mm:ss");
        return fecha;
    }

    public static String getFechaConMesYHora(Long longDate) {
        DateTime dateTime = new DateTime(longDate);
        String fecha = dateTime.getDayOfMonth() + " de "
                + getMonthName(dateTime.getMonthOfYear())
                + " " + dateTime.getYear() + " a las "
                + dateTime.toString("HH:mm:ss");
        return fecha;
    }
    
    public static String getHora(Time time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
        return formatter.format(getLocalFromTime(time));
    }
    public static String getHora(DateTime dateTime) {
        String fecha = "a las "+dateTime.toString("HH:mm:ss");
        return fecha;
    }

    public static String getHora(Long longDate) {
        DateTime dateTime = new DateTime(longDate);
        String date = "a las "+dateTime.toString("HH:mm:ss");
        return date;
    }
    
    public static String differenceBetweenHours(Time time1, Time time2) {
        DateTime dateTime1 = new DateTime(time1.getTime());
        dateTime1 = dateTime1.plus(1);
        DateTime dateTime2 = new DateTime(time2.getTime());
        dateTime2 = dateTime2.plus(1);
        long c = dateTime2.getMillis() - dateTime1.getMillis();
        DateTime dateTime = new DateTime(c);
        dateTime = dateTime.plusHours(4);
        Time diff = new Time(dateTime.getMillis());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return formatter.format(getLocalFromTime(diff));
    }
    
    public static String getFechaCorta(Timestamp timestamp) {
        DateTime dateTime = new DateTime(timestamp.getTime());
        return dateTime.toString("dd/MM/yyyy");
    }
    
    public static String getFechaCorta(Date date) {
        DateTime dateTime = new DateTime(date.getTime());
        return dateTime.toString("dd/MM/yyyy");
    }
    
    public static String getFechaCorta(DateTime dateTime) {
        return dateTime.toString("dd/MM/yyyy");
    }
    
    public static Time getTimeFromLocalTime(LocalTime local) {
        Time time = new Time(local.getHour(), local.getMinute(), 0);
        return time;
    }
    
    public static Timestamp getToday() {
        
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        java.util.Date today = new java.util.Date();

        java.util.Date todayWithZeroTime = null;
        try {
            todayWithZeroTime = formatter.parse(formatter.format(today));
        } catch (ParseException ex) {
            Logger.getLogger(RadarDate.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new Timestamp(todayWithZeroTime.getTime());
    }
     
    public static ObservableList<String> arraySpinnerDia() {
        String[] items = new String[30];
        items[0] = "01";
        items[1] = "02";
        items[2] = "03";
        items[3] = "04";
        items[4] = "05";
        items[5] = "06";
        items[6] = "07";
        items[7] = "08";
        items[8] = "09";
        items[9] = "10";
        items[10] = "11";
        items[11] = "12";
        items[12] = "13";
        items[13] = "14";
        items[14] = "15";
        items[15] = "16";
        items[16] = "17";
        items[17] = "18";
        items[18] = "19";
        items[19] = "20";
        items[20] = "21";
        items[21] = "22";
        items[22] = "23";
        items[23] = "24";
        items[24] = "25";
        items[25] = "26";
        items[26] = "27";
        items[27] = "28";
        items[28] = "29";
        items[29] = "30";
        return FXCollections.observableArrayList(items);
    }
    
    public static ObservableList<String> arraySpinnerMes() {
        String[] items = new String[12];
        items[0] = "01";
        items[1] = "02";
        items[2] = "03";
        items[3] = "04";
        items[4] = "05";
        items[5] = "06";
        items[6] = "07";
        items[7] = "08";
        items[8] = "09";
        items[9] = "10";
        items[10] = "11";
        items[11] = "12";
        return FXCollections.observableArrayList(items);
    }
    
    public static ObservableList<String> arraySpinnerAno() {
        String[] items = new String[20];
        Integer count = 0;
        
        Integer secuencia = (new DateTime().getYear()) - 10;
        for (String number : items) {
            items[count] = secuencia.toString();
            secuencia++;
            count++;
        }
        return FXCollections.observableArrayList(items);
    }
}
