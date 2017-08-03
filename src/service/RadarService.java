package service;

import com.google.gson.Gson;
import com.lynden.gmapsfx.javascript.object.LatLong;
import dao.*;
import model.*;
import org.hibernate.exception.ConstraintViolationException;
import util.HibernateSessionFactory;

import java.util.List;

public class RadarService {

    AdminDAO adminDAO;
    ControlPositionDAO cpDao;
    UserDAO userDao;
    WatchDAO watchDAO;
    PositionDAO posDAO;
    RouteDAO routeDAO;
    RoutePositionDAO rpDAO;
    Gson gson;

    private static RadarService ourInstance = new RadarService();

    public static RadarService getInstance() {
        return ourInstance;
    }

    private RadarService() {
        adminDAO = new AdminDAO();
        cpDao = new ControlPositionDAO();
        userDao = new UserDAO();
        watchDAO = new WatchDAO();
        posDAO = new PositionDAO();
        routeDAO = new RouteDAO();
        rpDAO = new RoutePositionDAO();
        gson = new Gson();
    }

    public void doEdit() {
        HibernateSessionFactory.getSession().flush();
    }

    public Boolean saveImport(String json){
        try {
            Import imp = gson.fromJson(json, Import.class);

            if (imp.getControlPositions() != null)
                for (ControlPosition control: imp.getControlPositions()) {

                    ControlPosition controlDB = cpDao.findByLatitudeLongitude(
                            control.getLatitude(), control.getLongitude());
                    if (controlDB == null) {
                        cpDao.save(control);
                    }else{
                        controlDB.setActive(control.getActive());
                        controlDB.setPlaceName(control.getPlaceName());
                        doEdit();
                    }
                }

            if (imp.getWatches() != null)
                for (Watch watch: imp.getWatches()) {
                    User user = userDao.findByDni(watch.getUser().getDni());
                    if (user == null){
                        user = watch.getUser();
                        userDao.save(user);
                    }
                    watch.setUser(user);
                }

            for (Watch watch: imp.getWatches()) {

                Watch watchDB = watchDAO.findByTime(watch.getStartTime(), watch.getEndTime());

                if (watchDB == null){
                    watchDAO.save(watch);
                    for(Position position: watch.getPositionsList()) {
                        ControlPosition controlDB = cpDao.findByLatitudeLongitude(
                                position.getControlPosition().getLatitude(),
                                position.getControlPosition().getLongitude());
                        position.setWatch(watch);
                        position.setControlPosition(controlDB);
                        posDAO.save(position);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getExportJson() {

        String jsonExport = null;
        try {
            Export export = new Export();
            export.setAdmins(adminDAO.findAllActive());
            export.setUsers(userDao.findAllActive());
            for (User user: export.getUsers()) {
                // Watch(set) give stack over flow error, so this should be null
                user.setWatchs(null);
            }
            export.setControlPositions(cpDao.findAllActive());
            for (ControlPosition control: export.getControlPositions()) {
                // Positions(set) give stack over flow error, so this should be null
                control.setPositions(null);
            }
            jsonExport = gson.toJson(export);

        } catch (Exception e) {
            System.err.println("json creation failed");
            e.printStackTrace();
        }
        return jsonExport;
    }

    public List<User> getAllUser() {
        List<User> users = userDao.findAllOrder();
        return users;
    }

    public List<User> getAllUserActive() {
        List<User> users = userDao.findAllActive();
        return users;
    }

    public List<Admin> getAllAdmin() {
        List<Admin> admins = adminDAO.findAll();
        return admins;
    }

    public List<ControlPosition> getAllControlActive() {
        List<ControlPosition> control = cpDao.findAllActive();
        return control;
    }

    public List<ControlPosition> getAllControl() {
        List<ControlPosition> control = cpDao.findAllOrder();
        return control;
    }

    public List<Watch> getAllUserWatches(Long id) {
        List<Watch> watches = watchDAO.findAllByUserId(id);
        return watches;

    }

    public ControlPosition findCPByLatLong(Double latitude, Double longitude) {
        ControlPosition controlPosition = cpDao.findByLatitudeLongitude(latitude, longitude);
        return controlPosition;
    }

    public ControlPosition findCPById(Long id) {
        ControlPosition controlPosition = cpDao.findById(id);
        return controlPosition;
    }

    public ControlPosition findCPByLatLong(LatLong latLong) {
        ControlPosition controlPosition =
                cpDao.findByLatitudeLongitude(latLong.getLatitude(), latLong.getLongitude());
        return controlPosition;
    }

    public List<Position> findAllPositionsByWatch(Watch watch) {
        List<Position> positions = posDAO.findAllByWatchId(watch.getId());
        return positions;
    }

    public void saveUser(User user) {
        userDao.save(user);
    }

    public void saveAdmin(Admin admin) {
        adminDAO.save(admin);
    }

    public Boolean deleteUser(User user) {
        Boolean isDeleted = true;
        if (getAllUserWatches(user.getId()).isEmpty()) {
            userDao.delete(user);
            isDeleted = true;
        } else {
            user.setActive(false);
            isDeleted = false;
        }
        doEdit();
        return isDeleted;
    }

    public void deleteAdmin(Admin admin) {
        adminDAO.delete(admin);
        doEdit();
    }

    public User findUserById(Long id) {
        User user = userDao.findById(id);
        return user;
    }

    public User findUserByDni(String dni) {
        User user = userDao.findByDni(dni);
        return user;
    }

    public Admin findAdminById(Long id) {
        Admin admin = adminDAO.findById(id);
        return admin;
    }

    public Admin findAdminByDni(String dni) {
        Admin admin = adminDAO.findByDni(dni);
        return  admin;
    }

    public Admin findAdminByUserName(String userName){
        Admin admin = adminDAO.findByUserName(userName);
        return admin;
    }

    public void saveRoute(Route route) {
        routeDAO.save(route);
    }

    public List<Route> getAllRoute() {
        List<Route> routes = routeDAO.findAll();
        return routes;
    }

    public void deleteRoute(Route route) {
        deleteAllRPByRouteId(route);
        routeDAO.delete(route);
        doEdit();
    }

    public Route findRouteById(Long id) {
        Route route = routeDAO.findById(id);
        return route;
    }

    public List<RoutePosition> findAllRPByRouteId(Route route) {
        List<RoutePosition> routePositions = rpDAO.findAllByRouteId(route.getId());
        return routePositions;
    }



    public void deleteAllRPByRouteId(Route route) {
        for (RoutePosition routePosition: findAllRPByRouteId(route)) {
            rpDAO.delete(routePosition);
        }
        doEdit();
    }

    public void saveRoutePosition(RoutePosition routePosition) {
        rpDAO.save(routePosition);
    }

}
