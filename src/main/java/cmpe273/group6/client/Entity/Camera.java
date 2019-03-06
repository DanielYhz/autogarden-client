package cmpe273.group6.client.Entity;

public class Camera {
    private int id;
    private boolean detection;
    private boolean state;

    public Camera(boolean detection, boolean state) {
        this.detection = detection;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDetection() {
        return detection;
    }

    public void setDetection(boolean detection) {
        this.detection = detection;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
