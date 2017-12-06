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
import org.joda.time.DateTimeUtils;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;

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
    
    public static String getDateWithMonth(DateTime dateTime) {
        String dateString = dateTime.getDayOfMonth() + " de "
                + getMonthName(dateTime.getMonthOfYear()) 
                + " " + dateTime.getYear();
        return dateString;
    }

    public static String getDateWithMonth(Long date) {
        DateTime dateTime = new DateTime(date);
        String dateString = dateTime.getDayOfMonth() + " de "
                + getMonthName(dateTime.getMonthOfYear())
                + " " + dateTime.getYear();
        return dateString;
    }

    public static String getDateWithMonth(Date date) {
        DateTime dateTime = new DateTime(date.getTime());
        String dateString = dateTime.getDayOfMonth() + " de "
                + getMonthName(dateTime.getMonthOfYear())
                + " " + dateTime.getYear();
        return dateString;
    }
    
    public static String getDateWithMonthWithoutYear(DateTime dateTime) {
        String dateString = dateTime.getDayOfMonth() + " de "
                + getMonthName(dateTime.getMonthOfYear());
        return dateString;
    }
    
    public static String getDateWithMonth(Timestamp timestamp) {
        DateTime dateTime = new DateTime(timestamp.getTime());
        String dateString = dateTime.getDayOfMonth() + " de "
                + getMonthName(dateTime.getMonthOfYear()) 
                + " " + dateTime.getYear();
        return dateString;
    }
    
    public static String getDateWithMonthAndTime(DateTime dateTime) {
        String dateString = dateTime.getDayOfMonth() + " de "
                + getMonthName(dateTime.getMonthOfYear()) 
                + " " + dateTime.getYear() + " a las "
                + dateTime.toString("HH:mm:ss");
        return dateString;
    }
    
    public static String getDateWithMonthAndTime(Timestamp timestamp) {
        DateTime dateTime = new DateTime(timestamp.getTime());
        String dateString = dateTime.getDayOfMonth() + " de "
                + getMonthName(dateTime.getMonthOfYear()) 
                + " " + dateTime.getYear() + " a las " 
                + dateTime.toString("HH:mm:ss");
        return dateString;
    }

    public static String getDateWithMonthAndTime(Long longDate) {
        DateTime dateTime = new DateTime(longDate);
        String dateString = dateTime.getDayOfMonth() + " de "
                + getMonthName(dateTime.getMonthOfYear())
                + " " + dateTime.getYear() + " a las "
                + dateTime.toString("HH:mm:ss");
        return dateString;
    }
    
    public static String getHours(Time time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
        return formatter.format(getLocalFromTime(time));
    }
    public static String getHours(DateTime dateTime) {
        String dateString = "a las "+dateTime.toString("HH:mm:ss");
        return dateString;
    }

    public static String getHours(Long longDate) {
        DateTime dateTime = new DateTime(longDate);
        String dateString = "a las "+dateTime.toString("HH:mm:ss");
        return dateString;
    }

    public static String getDayMonthHour(Long longDate) {
        DateTime dateTime = new DateTime(longDate);
        String dateString = dateTime.getDayOfMonth() + " de "
                + getMonthName(dateTime.getMonthOfYear())
                + " a las "+ dateTime.toString("HH:mm:ss");
        return dateString;
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

    public static String differenceBetweenHours(Long time1, Long time2) {
        DateTime dateTime1 = new DateTime(time1);
        DateTime dateTime2 = new DateTime(time2);
        Seconds seconds = Seconds.secondsBetween(dateTime2, dateTime1);
        return secondsToMinutesBest(seconds.getSeconds());
    }

    public static String differenceBetweenHMS(Long time1, Long time2) {
        DateTime dateTime1 = new DateTime(time1);
        DateTime dateTime2 = new DateTime(time2);
        Seconds seconds = Seconds.secondsBetween(dateTime2, dateTime1);
        return secondsToMinutesBest(seconds.getSeconds());
    }

    public static int differenceBetweenMinutes(Long time1, Long time2) {
        DateTime dateTime1 = new DateTime(time1);
        DateTime dateTime2 = new DateTime(time2);
        Minutes minutes = Minutes.minutesBetween(dateTime2, dateTime1);
        return minutes.getMinutes();
    }

    public static Integer differenceBetweenSeconds(Long time1, Long time2) {
        DateTime dateTime1 = new DateTime(time1);
        DateTime dateTime2 = new DateTime(time2);
        Seconds seconds = Seconds.secondsBetween(dateTime2, dateTime1);
        return seconds.getSeconds();
    }

    public static String secondsToMinutes(int seconds) {
        int minutes = 0;
        int hours = 0;
        while (seconds > 59) {
            seconds -= 60;
            minutes++;
        }
        while (minutes > 59) {
            minutes -= 60;
            hours++;
        }
        return hours+":"+minutes+":"+seconds;
    }

    public static String secondsToMinutesBest(int seconds) {
        int minutes = 0;
        int hours = 0;
        while (seconds > 59) {
            seconds -= 60;
            minutes++;
        }
        while (minutes > 59) {
            minutes -= 60;
            hours++;
        }
        if (hours == 0 && minutes == 0) {
            return seconds+"s";
        }
        if (hours == 0) {
            return minutes+"m "+seconds+"s";
        }
        return hours+"h "+minutes+"m "+seconds+"s";
    }

    public static String getDateShort(Long time) {
        DateTime dateTime = new DateTime(time);
        return dateTime.toString("dd/MM/yyyy");
    }
    
    public static String getDateShort(Timestamp timestamp) {
        DateTime dateTime = new DateTime(timestamp.getTime());
        return dateTime.toString("dd/MM/yyyy");
    }
    
    public static String getDateShort(Date date) {
        DateTime dateTime = new DateTime(date.getTime());
        return dateTime.toString("dd/MM/yyyy");
    }
    
    public static String getDateShort(DateTime dateTime) {
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

    public static void main(String[] args) {
        //System.out.println(differenceBetweenHMS(DateTimeUtils.currentTimeMillis(), Long.valueOf("1503010000849")));
        //System.out.println(getDateWithMonthAndTime(DateTimeUtils.currentTimeMillis()));
        //System.out.println(getDateWithMonthAndTime(Long.valueOf("1503010000849")));
        System.out.println(Long.valueOf("1503010000849").intValue());
    }
}
