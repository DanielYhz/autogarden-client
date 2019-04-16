package cmpe273.group6.client.Entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "camera")
@EntityListeners(AuditingEntityListener.class)
public class Camera implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "camera_id")
    private long id;

    @Column (name = "detection")
    private int detection;

    @Column (name = "state")
    private int state;

    @Column (name = "access_server")
    private String auth;

    @Column (name = "fid")
    private String fid;

    public Camera() {

    }

    public Camera(@NotBlank String fid) {
        this.fid = fid;
        this.auth = "";
        this.detection = 0;
        this.state = 1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDetection() {
        return detection;
    }

    public void setDetection(int detection) {
        this.detection = detection;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }
}
