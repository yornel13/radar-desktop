package model;

import java.util.List;

public class RouteReport {


    private String point_marker;
    private String distance;
    private List<GroupReport> groupReportList;

    public String getPoint_marker() {
        return point_marker;
    }

    public void setPoint_marker(String point_marker) {
        this.point_marker = point_marker;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<GroupReport> getGroupReportList() {
        return groupReportList;
    }

    public void setGroupReportList(List<GroupReport> groupReportList) {
        this.groupReportList = groupReportList;
    }
}
