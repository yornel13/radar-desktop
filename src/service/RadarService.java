package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import dao.*;
import model.*;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.internal.util.SerializationHelper;
import org.joda.time.DateTime;
import util.HibernateProxyTypeAdapter;
import util.HibernateSessionFactory;
import util.RadarDate;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RadarService {

    AdminDAO adminDAO;
    ControlPositionDAO cpDao;
    UserDAO userDao;
    GroupDAO groupDAO;
    WatchDAO watchDAO;
    PositionDAO posDAO;
    RouteDAO routeDAO;
    RoutePositionDAO rpDAO;
    RouteMarkerDAO rmDAO;
    CompanyDAO companyDAO;
    Gson gson;
    ErrorCases listener;

    private static RadarService ourInstance = new RadarService();

    public static RadarService getInstance() {
        return ourInstance;
    }

    public void setListener(ErrorCases listener) {
        this.listener = listener;
    }

    private RadarService() {
        adminDAO = new AdminDAO();
        cpDao = new ControlPositionDAO();
        userDao = new UserDAO();
        groupDAO = new GroupDAO();
        watchDAO = new WatchDAO();
        posDAO = new PositionDAO();
        routeDAO = new RouteDAO();
        rpDAO = new RoutePositionDAO();
        rmDAO = new RouteMarkerDAO();
        companyDAO = new CompanyDAO();
        gson = new Gson();
    }

    public static <T> T clone(Class<T> clazz, T dtls) {
        T clonedObject = (T) SerializationHelper.clone((Serializable) dtls);
        return clonedObject;
    }

    public void doEdit() {
        HibernateSessionFactory.getSession().flush();
    }

    public Boolean doDelete() {
        try {
            HibernateSessionFactory.getSession().flush();
            return true;
        } catch (ConstraintViolationException e) {
            System.err.println("can't delete");
            HibernateSessionFactory.getSession().clear();
            HibernateSessionFactory.getSession().flush();
            listener.onError("No se puede borrar porque esta en uso");
            return false;
        }
    }

    public Boolean saveImport(String json){
        try {
            Import imp = gson.fromJson(json, Import.class);

            if (imp.getControlPositions() != null) {
                for (ControlPosition control : imp.getControlPositions()) {

                    ControlPosition controlDB = cpDao.findByLatitudeLongitude(
                            control.getLatitude(), control.getLongitude());
                    if (controlDB == null) {
                        cpDao.save(control);
                    } else {
                        controlDB.setActive(control.getActive());
                        controlDB.setPlaceName(control.getPlaceName());
                        doEdit();
                    }
                }
            }

            if (imp.getWatches() != null) {

                for (Watch watch : imp.getWatches()) {
                    User user = userDao.findById(watch.getUser().getId());
                    watch.setUser(user);

                    Watch watchDB = watchDAO.findByTime(watch.getStartTime(), watch.getEndTime());

                    if (watchDB == null) {
                        watchDAO.save(watch);
                        for (Position position : watch.getPositionsList()) {
                            ControlPosition controlDB = cpDao.findById(position.getControlPosition().getId());
                            position.setWatch(watch);
                            position.setControlPosition(controlDB);
                            posDAO.save(position);
                        }
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

        JsonObject jsonExport = new JsonObject();

        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        Gson gson = b.create();

        try {
            List<User> users = userDao.findAll();
            for (User user: users) {
                user.setWatchs(null);
                user.setRouteMarkers(null);
                user.setCompany(null);
            }
            List<ControlPosition> controlPositions = cpDao.findAll();
            for (ControlPosition control: controlPositions) {
                control.setPositions(null);
                control.setRouteMarkers(null);
                control.setRoutePositions(null);
            }
            List<RouteMarker> routeMarkers = rmDAO.findAll();
            List<Group> groups = (List<Group>) groupDAO.findAll();
            for (Group group: groups) {
                group.setUsers(null);
            }
            List<Route> routes = (List<Route>) routeDAO.findAll();
            for (Route route: routes) {
                route.setGroups(null);
                route.setRoutePositions(null);
            }
            List<RoutePosition> routePositions = rpDAO.findAll();
            List<Admin> admins = adminDAO.findAll();

            jsonExport.add("admins", gson.toJsonTree(admins));
            jsonExport.add("users", gson.toJsonTree(users));
            jsonExport.add("controlPositions", gson.toJsonTree(controlPositions));
            jsonExport.add("groups", gson.toJsonTree(groups));
            jsonExport.add("routes", gson.toJsonTree(routes));
            jsonExport.add("routeMarkers", gson.toJsonTree(routeMarkers));
            jsonExport.add("routePositions", gson.toJsonTree(routePositions));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("json creation failed");
        }
        return jsonExport.toString();
    }

    public List<Group> getAllGroup() {
        List<Group> groups = groupDAO.findAll();
        return groups;
    }

    public List<User> getAllUser() {
        List<User> users = userDao.findAllOrder();
        return users;
    }

    public List<User> getAllUserByCompany(Company company) {
        List<User> users = userDao.findAllOrderByCompanyId(company.getId());
        return users;
    }

    public List<User> getAllUserActive() {
        List<User> users = userDao.findAllActive();
        return users;
    }

    public List<User> getAllUserByCompanyActive(Company company) {
        List<User> users = userDao.findAllByCompanyIdActive(company.getId());
        return users;
    }

    public List<Admin> getAllAdmin() {
        List<Admin> admins = adminDAO.findAll();
        return admins;
    }

    public List<Company> getAllCompanies() {
        List<Company> companies = companyDAO.findAll();
        return companies;
    }

    public List<Company> getAllCompaniesActive() {
        List<Company> companies = companyDAO.findAllActive();
        return companies;
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

    public List<Watch> findAllWatch() {
        List<Watch> watches = watchDAO.findAll();
        return watches;
    }

    public List<Position> findAllPositionsByWatchUpdateTime(Watch watch) {
        List<Position> positions = posDAO.findAllByWatchId(watch.getId());
        for (Position position :
                positions) {
            if (positions.indexOf(position) == positions.size()-1) {
                position.setDifferent("Duración "+RadarDate.differenceBetweenHMS(position.getTime(),
                        watch.getStartTime()));
                position.setMinutes(RadarDate.differenceBetweenMinutes(position.getTime(),
                        watch.getStartTime()));
            } else {
                position.setDifferent("Duración "+RadarDate.differenceBetweenHMS(position.getTime(),
                        positions.get(positions.indexOf(position)+1).getTime()));
                position.setMinutes(RadarDate.differenceBetweenMinutes(position.getTime(),
                        positions.get(positions.indexOf(position)+1).getTime()));
            }
        }
        return positions;
    }

    public List<Position> findAllPositionsByControl(ControlPosition control) {
        List<Position> positions = posDAO.findAllByControlId(control.getId());
        return positions;
    }

    public List<Position> findAllPositionsByControlAndCompany(ControlPosition control, Company company) {
        List<Position> positions = posDAO.findAllByControlId(control.getId());
        List<Position> positionsFilter = new ArrayList<>();
        for (Position position: positions) {
            if (position.getWatch().getUser().getCompany().getId().equals(company.getId())) {
                positionsFilter.add(position);
            }
        }
        return positionsFilter;
    }

    public List<Position> findAllPositionsByControl(ControlPosition control, Date dateFrom, Date dateTo) {
        List<Position> positions = posDAO.findAllByControlIdBetween(control.getId(), dateFrom.getTime(),
                new DateTime(dateTo.getTime()).plusDays(1).getMillis());
        return positions;
    }

    public List<Position> findAllPositionsByControlAndCompany(ControlPosition control, Company company, Date dateFrom, Date dateTo) {
        List<Position> positions = posDAO.findAllByControlIdBetween(control.getId(), dateFrom.getTime(),
                new DateTime(dateTo.getTime()).plusDays(1).getMillis());
        List<Position> positionsFilter = new ArrayList<>();
        for (Position position: positions) {
            if (position.getWatch().getUser().getCompany().getId().equals(company.getId())) {
                positionsFilter.add(position);
            }
        }
        return positionsFilter;
    }

    public List<Position> findAllPositionsByControlAndCompany(ControlPosition control, Company company, Date dateFrom, Date dateTo, int min, int max) {
        List<Position> positions = posDAO.findAllByControlIdBetween(control.getId(), dateFrom.getTime(),
                new DateTime(dateTo.getTime()).plusDays(1).getMillis());
        List<Position> positionsFilter = new ArrayList<>();
        for (Position position: positions) {
            if (position.getWatch().getUser().getCompany().getId().equals(company.getId())) {
                int updateTime = position.getUpdateTime().intValue()/60;
                if (updateTime >= min && updateTime <= max) {
                    positionsFilter.add(position);
                }
            }
        }
        return positionsFilter;
    }

    public List<Position> findAllPositionsByControlAndCompany(ControlPosition control, Company company, int min, int max) {
        List<Position> positions = posDAO.findAllByControlId(control.getId());
        List<Position> positionsFilter = new ArrayList<>();
        for (Position position: positions) {
            if (position.getWatch().getUser().getCompany().getId().equals(company.getId())) {
                int updateTime = position.getUpdateTime().intValue()/60;
                if (updateTime >= min && updateTime <= max) {
                    positionsFilter.add(position);
                }
            }
        }
        return positionsFilter;
    }

    public void saveUser(User user) {
        userDao.save(user);
    }

    public void saveAdmin(Admin admin) {
        adminDAO.save(admin);
    }

    public void saveCompany(Company company) {
        companyDAO.save(company);
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
        doDelete();
        return isDeleted;
    }

    public Boolean deleteGroup(Group group) {
        groupDAO.delete(group);
        doDelete();
        return true;
    }

    public void deleteAdmin(Admin admin) {
        adminDAO.delete(admin);
        doDelete();
    }

    public Boolean deleteCompany(Company company) {
        companyDAO.delete(company);
        return doDelete();
    }

    public User findUserById(Long id) {
        User user = userDao.findById(id);
        return user;
    }

    public Group findGroupById(Long id) {
        Group group = groupDAO.findById(id);
        return group;
    }


    public User findUserByDni(String dni) {
        User user = userDao.findByDni(dni);
        return user;
    }

    public List<User> findUserByGroupId(Long id) {
        List user = userDao.findUserByGroupId(id);
        return user;
    }

    public List<User> findUsersByGroupIdAndCompany(Long id, Company company) {
        List user = userDao.findUserByCompanyIdByGroupId(id, company.getId());
        return user;
    }

    public List<User> findAllOrderByGroup() {
        List user = userDao.findAllOrderByGroup();
        return user;
    }

    public List<User> findAllOrderByGroup(Company company) {
        List user = userDao.findAllOrderByCompanyId(company.getId());
        return user;
    }

    public Admin findAdminById(Long id) {
        Admin admin = adminDAO.findById(id);
        return admin;
    }

    public Company findCompanyById(Long id) {
        Company company = companyDAO.findById(id);
        return company;
    }

    public Admin findAdminByDni(String dni) {
        Admin admin = adminDAO.findByDni(dni);
        return  admin;
    }

    public Admin findAdminByUserName(String userName){
        Admin admin = adminDAO.findByUserName(userName);
        return admin;
    }

    public Company findCompanyByAcronym(String acronym){
        Company company = companyDAO.findByAcronym(acronym);
        return company;
    }

    public Company findCompanyByNumeration(String numeration){
        Company company = companyDAO.findByNumeration(numeration);
        return company;
    }

    public void saveGroup(Group group) {
        groupDAO.save(group);
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
        doDelete();
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
        doDelete();
    }

    public void saveRoutePosition(RoutePosition routePosition) {
        rpDAO.save(routePosition);
    }

    public void deleteControl(ControlPosition control) {
        if (posDAO.findAllByControlId(control.getId()).isEmpty()
                && rpDAO.findAllByControlId(control.getId()).isEmpty()
                && rmDAO.findAllByControlId(control.getId()).isEmpty()) {
            cpDao.delete(control);
            doDelete();
            listener.onSuccess("Borrado hecho con exito.");
        } else {
            listener.onError("No se puede borrar esta ubicacion porque esta en uso!");
        }
    }

    public interface ErrorCases {

        void onError(String error);

        void onSuccess(String message);
    }
}
