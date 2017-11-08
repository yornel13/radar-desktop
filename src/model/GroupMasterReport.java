package model;

import java.util.List;

public class GroupMasterReport {

    private String id;
    private String group_name;
    private String emp_num;
    private List<GroupReport> groupReportList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getEmp_num() {
        return emp_num;
    }

    public void setEmp_num(String emp_num) {
        this.emp_num = emp_num;
    }

    public List<GroupReport> getGroupReportList() {
        return groupReportList;
    }

    public void setGroupReportList(List<GroupReport> groupReportList) {
        this.groupReportList = groupReportList;
    }
}
