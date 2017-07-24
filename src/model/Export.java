package model;

import java.util.List;

public class Export {

    List<ControlPosition> controlPositions;
    List<Admin> admins;

    public List<ControlPosition> getControlPositions() {
        return controlPositions;
    }

    public void setControlPositions(List<ControlPosition> controlPositions) {
        this.controlPositions = controlPositions;
    }

    public List<Admin> getAdmins() {
        return admins;
    }

    public void setAdmins(List<Admin> admins) {
        this.admins = admins;
    }

}
