package cmpe273.group6.client.Entity;

public class Springkler {
    private int id;
    private boolean state;

    public Springkler(int id, boolean state) {
        this.id = id;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
