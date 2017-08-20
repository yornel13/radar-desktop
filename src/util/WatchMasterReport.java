package util;

import java.util.List;

public class WatchMasterReport {

    private String date;
    private List<SubReportBean> subReportBeanList;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<SubReportBean> getSubReportBeanList() {
        return subReportBeanList;
    }

    public void setSubReportBeanList(List<SubReportBean> subReportBeanList) {
        this.subReportBeanList = subReportBeanList;
    }
}
