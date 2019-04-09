package cmpe273.group6.client.Entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "sensor")
@EntityListeners(AuditingEntityListener.class)
public class Sensor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sensor_id")
    private long id;

    @Column(name = "sunlight")
    private int sunlight;

    @Column(name = "fid")
    private String fid;

    @Column(name = "water_received")
    private int water_received;

    @Column(name = "state")
    private boolean state;

    @Column(name = "access_server")
    private String auth;

    @Column(name = "access_mode")
    private int access_mode;

    @Column(name = "observe")
    private boolean observe;

    protected Sensor() {

    }

    public Sensor(@NotBlank int sunlight, @NotBlank int water_received, @NotBlank String fid) {
        this.sunlight = sunlight;
        this.water_received = water_received;
        this.state = true;
        this.fid = fid;
        this.auth = "";
        this.access_mode = 2;
        this.observe = false;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public long getId() {
        return id;
    }

    public int getSunlight() {
        return sunlight;
    }

    public void setSunlight(int sunlight) {
        this.sunlight = sunlight;
    }

    public int getWater_received() {
        return water_received;
    }

    public void setWater_received(int water_received) {
        this.water_received = water_received;
    }

    public boolean getState() {
        return state;
    }

    public boolean setState(boolean state) {
        this.state = state;
        return state;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public int getAccess_mode() {
        return access_mode;
    }

    public void setAccess_mode(int access_mode) {
        this.access_mode = access_mode;
    }

    public boolean isObserve() {
        return observe;
    }

    public void setObserve(boolean observe) {
        this.observe = observe;
    }
}
