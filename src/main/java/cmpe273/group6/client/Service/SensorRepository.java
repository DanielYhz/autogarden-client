package cmpe273.group6.client.Service;

import cmpe273.group6.client.Entity.Sensor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends CrudRepository<Sensor, Long> {
    Sensor findSensorById(String Id);
}
