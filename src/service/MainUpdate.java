package service;

import model.Position;
import model.Watch;
import util.HibernateSessionFactory;
import util.RadarDate;

import java.util.List;

public class MainUpdate {

    public static void main(String[] args) {
        for (Watch watch:
                RadarService.getInstance().findAllWatch()) {
            List<Position> positions = RadarService.getInstance().findAllPositionsByWatch(watch);
            for (Position position:
                    positions) {
                if (positions.indexOf(position) == positions.size()-1) {
                    position.setUpdateTime(Long.valueOf(RadarDate.differenceBetweenSeconds(position.getTime(),
                            watch.getStartTime())));
                } else {
                    position.setUpdateTime(Long.valueOf(RadarDate.differenceBetweenSeconds(position.getTime(),
                            positions.get(positions.indexOf(position)+1).getTime())));
                }
            }
        }
        HibernateSessionFactory.getSession().flush();
    }
}
