package model;

import java.util.List;

public class WatchRouteMasterReport {

    private String date;
    private List<WatchSubReport> watchsReportList;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<WatchSubReport> getWatchsReportList() {
        return watchsReportList;
    }

    public void setWatchsReportList(List<WatchSubReport> watchsReportList) {
        this.watchsReportList = watchsReportList;
    }
}
