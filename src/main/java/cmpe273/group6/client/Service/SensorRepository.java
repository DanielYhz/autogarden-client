package cmpe273.group6.client.Service;

import cmpe273.group6.client.Entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Sensor findSensorById(long Id);
}
