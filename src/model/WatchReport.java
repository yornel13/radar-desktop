package model;

import java.util.List;

public class WatchReport {

    private String id;
    private String start;
    private String finish;
    private List<PointReport> pointReportList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public List<PointReport> getPointReportList() {
        return pointReportList;
    }

    public void setPointReportList(List<PointReport> pointReportList) {
        this.pointReportList = pointReportList;
    }
}
