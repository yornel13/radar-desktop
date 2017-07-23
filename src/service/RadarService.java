package service;

import com.google.gson.Gson;
import com.lynden.gmapsfx.javascript.object.LatLong;
import dao.ControlPositionDAO;
import dao.PositionDAO;
import dao.UserDAO;
import dao.WatchDAO;
import model.*;
import util.HibernateSessionFactory;

import java.util.List;

public class RadarService {

    ControlPositionDAO cpDao;
    UserDAO userDao;
    WatchDAO watchDAO;
    PositionDAO posDAO;
    Gson gson;

    private static RadarService ourInstance = new RadarService();

    public static RadarService getInstance() {
        return ourInstance;
    }

    private RadarService() {
        cpDao = new ControlPositionDAO();
        userDao = new UserDAO();
        watchDAO = new WatchDAO();
        posDAO = new PositionDAO();
        gson = new Gson();
    }

    public Boolean saveImport(String json){
        try {
            Import imp = gson.fromJson(json, Import.class);

            for (ControlPosition control: imp.getControlPositions()) {

                ControlPosition controlDB = cpDao.findByLatitudeLongitude(
                        control.getLatitude(), control.getLongitude());
                if (controlDB == null) {
                    cpDao.save(control);
                }else{
                    controlDB.setActive(control.getActive());
                    controlDB.setPlaceName(control.getPlaceName());
                    HibernateSessionFactory.getSession().flush();
                }
            }

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

    public List<User> getAllUser() {
        List<User> users = userDao.findAll();
        return users;

    }

    public List<ControlPosition> getAllControlActive() {
        List<ControlPosition> control = cpDao.findAll();
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

    public ControlPosition findCPByLatLong(LatLong latLong) {
        ControlPosition controlPosition =
                cpDao.findByLatitudeLongitude(latLong.getLatitude(), latLong.getLongitude());
        return controlPosition;

    }

    public List<Position> findAllPositionsByWatch(Watch watch) {
        List<Position> positions = posDAO.findAllByWatchId(watch.getId());
        return positions;
    }
}
