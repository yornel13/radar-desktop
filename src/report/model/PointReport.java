package report.model;

public class PointReport {

    private String id;
    private String user;
    private String point;
    private String distance;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDistanceMeters(int distance) {
        if (distance == 0) {
            this.distance = "a menos de 1 metro";
        } else if (distance == 1) {
            this.distance = "a 1 metro";
        } else {
            this.distance = "a "+distance+" metros";
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
