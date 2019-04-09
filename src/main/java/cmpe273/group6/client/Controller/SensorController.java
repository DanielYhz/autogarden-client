package cmpe273.group6.client.Controller;


import cmpe273.group6.client.Entity.Sensor;
// import cmpe273.group6.client.Exception.ResourceNotFoundException;
import cmpe273.group6.client.Service.SensorRepository;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


@RestController
@RequestMapping("/sensors")
public class SensorController {

    private SensorRepository sensorRepository;

    SensorController (SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    // Get all sensors.
    @GetMapping
    public @ResponseBody Iterable<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    // Create a single sensor.
    @PostMapping
    public Sensor createSensor(@RequestBody Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    // Get a single sensor.
    @GetMapping("/{id}")
    public Sensor getSensorById(@PathVariable(value = "id") long sensorId) {
        // .orElseThrow(() -> new ResourceNotFoundException("Sensor", "Id", sensorId))
        return sensorRepository.findSensorById(sensorId);
    }

    // Update a Note.
    @PutMapping(value = "/{id}")
    public Sensor updateSensor(@PathVariable(value = "id") Long sensorId, @Valid @RequestBody Sensor sensorDetails) {
        // sensorRepository.findById(sensorId).orElseThrow(() -> new ResourceNotFoundException("Sensor", "Id", sensorId));
        Sensor sensor = sensorRepository.findSensorById(sensorDetails.getId());
        sensor.setFid(sensorDetails.getFid());
        sensor.setState(sensorDetails.getState());
        sensor.setSunlight(sensorDetails.getSunlight());
        sensor.setWater_received(sensorDetails.getWater_received());
        Sensor updateSensor = sensorRepository.save(sensor);
        return updateSensor;
    }

    // Delete a Note.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSensor (@PathVariable (value="id") long sensorId) {
//        Sensor sensor = sensorRepository.findById(sensorId).orElseThrow(() -> new ResourceNotFoundException("Sensor", "Id", sensorId));
        Sensor sensor = sensorRepository.findSensorById(sensorId);
        sensorRepository.delete(sensor);
        return ResponseEntity.ok().build();
    }

    // Bootstrap a sensor.
    @GetMapping("/bs/{id}")
    public String bootStrapSensor(@PathVariable(value = "id") long sensorId) {
        // System.out.println(sensorDetails.getId());
        Sensor sensor = sensorRepository.findSensorById(sensorId);
        String bs_fid = sensor.getFid();
        // send Bootstrap request operation to bootstrap server
        String url = "http://localhost:8091/bs/" + bs_fid;
        StringBuffer response = new StringBuffer();
        try {
            URL urlObj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.getResponseMessage();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response.toString().substring(0,4).equals("http")) {
            sensor.setAuth(response.toString());
            sensorRepository.save(sensor);

            return "Bootstrap Succeed.";
        } else {
            return response.toString();
        }
    }

    // Register a sensor
    @PostMapping("/registration/{id}")
    public String registerSensor(@PathVariable(value = "id") long sensorId) {
        Sensor sensor = sensorRepository.findSensorById(sensorId);
        String access_server = sensor.getAuth() + "/sensors/registration/" + sensorId;
        URLConnection client = null;
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(access_server);
            client = url.openConnection();
            client.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(
                    client.getOutputStream());
            out.write(Long.toString(sensorId));
            out.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            //bad  URL, tell the user
        } catch (IOException e) {
            //network error/ tell the user
        }

        if (response.toString().equals("Registration Complete")) {
            sensor.setState(true);
            sensorRepository.save(sensor);
        }

        return response.toString();
    }

    // Deregister a sensor
    @GetMapping("/deregistration/{id}")
    public String deregistrerSensor(@PathVariable(value = "id") long sensorId) {
        Sensor sensor = sensorRepository.findSensorById(sensorId);
        String access_server = sensor.getAuth() + "/sensors/deregistration/" + sensorId;
        URLConnection client = null;
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(access_server);
            client = url.openConnection();
            client.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(
                    client.getOutputStream());
            out.write(Long.toString(sensorId));
            out.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            //bad  URL, tell the user
        } catch (IOException e) {
            //network error/ tell the user
        }
        return response.toString();
    }

    // Update a sensor in server
    // sensor_id, state, access_mode. This part have to change in the same time with server.

    @PostMapping("/update/{id}")
    public String update(@PathVariable(value = "id") long sensorId, @RequestBody Map<String, String> map){
        if (sensorRepository.findSensorById(sensorId) == null) {
            return "The device is not being bootstrapped, please check!";
        }
        Sensor sensor = sensorRepository.findSensorById(sensorId);
        String access_server = sensor.getAuth() + "/sensors/update/" + sensorId;

        HttpClient httpClient = HttpClientBuilder.create().build();

        StringBuffer response_message = new StringBuffer();

        try {
            HttpPost request = new HttpPost(access_server);
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (String c : map.keySet()) {
                sb.append("\"");
                sb.append(c);
                sb.append("\" : ");
                sb.append("\"");
                sb.append(map.get(c));
                sb.append("\",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("}");
            String content = sb.toString();
            StringEntity params = new StringEntity(content);

            request.setHeader("content-type", "application/json");
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response_message.append(inputLine);
            }
            in.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        if (response_message.toString().equals("Update succeed!")) {
            return "Update device id: " + sensorId + " succeed!";
        } else {
            return "Something is wrong!";
        }
    }

    // Update the local data stored in the client (sensor)
    // change water received and sunlight only.
    @PostMapping("/update/data/{id}")
    public String updateData(@PathVariable(value = "id") long sensorId, @RequestBody Map<String, String> map) {
        if (sensorRepository.findSensorById(sensorId) == null) {
            return "The device is not being bootstrapped, please check!";
        }
        Sensor sensor = sensorRepository.findSensorById(sensorId);
        if (map.containsKey("sunlight")) {
            sensor.setSunlight(Integer.parseInt(map.get("sunlight")));
        }
        if (map.containsKey("water_received")) {
            sensor.setWater_received(Integer.parseInt(map.get("water_received")));
        }
        this.sensorRepository.save(sensor);
        return "Update complete";
    }
}
