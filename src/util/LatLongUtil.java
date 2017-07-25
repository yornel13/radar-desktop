package util;

import com.lynden.gmapsfx.javascript.object.LatLong;

import java.util.List;

public class LatLongUtil {

    private static LatLongUtil ourInstance = new LatLongUtil();

    public static LatLongUtil getInstance() {
        return ourInstance;
    }

    public LatLong computeCentroid(List<LatLong> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLong point : points) {
            latitude += point.getLatitude();
            longitude += point.getLongitude();
        }

        return new LatLong(latitude/n, longitude/n);
    }
}
