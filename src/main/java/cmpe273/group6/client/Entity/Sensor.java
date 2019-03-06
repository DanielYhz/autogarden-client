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
    @NotBlank
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "sunlight")
    private int sunlight;

    @NotBlank
    @Column(name = "water_received")
    private int water_received;

    @NotBlank
    @Column(name = "state")
    private boolean state;

    protected Sensor(@NotBlank Long id) {
        this.id = id;
    }

    public Sensor(@NotBlank int sunlight, @NotBlank int water_received) {
        this.sunlight = sunlight;
        this.water_received = water_received;
        this.state = true;
    }

    public Long getId() {
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
}
