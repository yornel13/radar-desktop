package model;

import java.util.List;

public class Export {

    List<ControlPosition> controlPositions;
    List<User> users;
    List<Route> routes;
    List<Group> groups;
    List<RoutePosition> routePositions;
    List<Admin> admins;

    public List<ControlPosition> getControlPositions() {
        return controlPositions;
    }

    public void setControlPositions(List<ControlPosition> controlPositions) {
        this.controlPositions = controlPositions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Admin> getAdmins() {
        return admins;
    }

    public void setAdmins(List<Admin> admins) {
        this.admins = admins;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<RoutePosition> getRoutePositions() {
        return routePositions;
    }

    public void setRoutePositions(List<RoutePosition> routePositions) {
        this.routePositions = routePositions;
    }
}
