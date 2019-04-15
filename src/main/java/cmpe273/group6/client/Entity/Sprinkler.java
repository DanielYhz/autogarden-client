package cmpe273.group6.client.Entity;


import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "sprinkler")
@EntityListeners(AuditingEntityListener.class)
public class Sprinkler implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sprinkler_id")
    private long id;

    @Column (name = "state")
    private boolean state;

    @Column(name = "access_server")
    private String auth;

    @Column(name = "fid")
    private String fid;

    public Sprinkler () {

    }

    public Sprinkler(int id, boolean state, @NotBlank String fid) {
        this.id = id;
        this.state = state;
        this.auth = "";
        this.fid = fid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
