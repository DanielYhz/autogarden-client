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
    private boolean detection;

    @Column (name = "state")
    private boolean state;

    @Column (name = "access_server")
    private String auth;

    @Column (name = "fid")
    private String fid;

    public Camera() {

    }

    public Camera(@NotBlank String fid) {
        this.fid = fid;
        this.auth = "";
        this.detection = false;
        this.state = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
