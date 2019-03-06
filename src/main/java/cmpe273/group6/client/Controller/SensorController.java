package cmpe273.group6.client.Controller;


import cmpe273.group6.client.Entity.Sensor;
// import cmpe273.group6.client.Exception.ResourceNotFoundException;
import cmpe273.group6.client.Service.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    @Autowired
    SensorRepository sensorRepository;

    // Get all sensors.
    @GetMapping("/sensors")
    public @ResponseBody Iterable<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    // Create a single sensor.
    @PostMapping("/sensors")
    public Sensor createSensor(@Valid @RequestBody Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    // Get a single sensor.
    @GetMapping("/sensors/{id}")
    public Sensor getSensorById(@PathVariable(value = "id") String sensorId) {
        // .orElseThrow(() -> new ResourceNotFoundException("Sensor", "Id", sensorId))
        return sensorRepository.findSensorById(sensorId);
    }

    // Update a Note.
    @PutMapping("/sensors/{id}")
    public Sensor updateSensor(@PathVariable(value = "id") Long sensorId, @Valid @RequestBody Sensor sensorDetails) {
        // sensorRepository.findById(sensorId).orElseThrow(() -> new ResourceNotFoundException("Sensor", "Id", sensorId));
        Sensor sensor = sensorRepository.findSensorById(sensorDetails.getId().toString());
        sensor.setState(sensorDetails.getState());
        sensor.setSunlight(sensorDetails.getSunlight());
        sensor.setWater_received(sensorDetails.getWater_received());
        Sensor updateSensor = sensorRepository.save(sensor);
        return updateSensor;
    }

    // Delete a Note.
    @DeleteMapping("/sensors/{id}")
    public ResponseEntity<?> deleteSensor (@PathVariable (value="id") Long sensorId) {
//        Sensor sensor = sensorRepository.findById(sensorId).orElseThrow(() -> new ResourceNotFoundException("Sensor", "Id", sensorId));
        Sensor sensor = sensorRepository.findSensorById(sensorId.toString());
        sensorRepository.delete(sensor);
        return ResponseEntity.ok().build();
    }
}
