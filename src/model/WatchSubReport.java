package model;

import service.RadarService;

import java.util.ArrayList;
import java.util.List;

public class WatchSubReport {

    private String dni;
    private String user;
    private String marker;
    private String date;
    private String time;
    private String diff;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public static List<WatchSubReport> cloneList(List<WatchSubReport> list) {
        List<WatchSubReport> clone = new ArrayList<>(list.size());
        for (WatchSubReport item : list) clone.add(RadarService.clone(WatchSubReport.class, item));
        return clone;
    }
}
