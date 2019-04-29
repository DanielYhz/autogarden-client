package cmpe273.group6.client.Controller;


import cmpe273.group6.client.Entity.Sensor;
// import cmpe273.group6.client.Exception.ResourceNotFoundException;
import cmpe273.group6.client.Entity.Sprinkler;
import cmpe273.group6.client.Service.SensorRepository;
import cmpe273.group6.client.Service.SprinklerRepository;
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
    private SprinklerRepository sprinklerRepository;

    Set<Long> sensor_registered = new HashSet<>();

    SensorController (SensorRepository sensorRepository, SprinklerRepository sprinklerRepository) {
        this.sensorRepository = sensorRepository;
        this.sprinklerRepository = sprinklerRepository;
    }

    // Get all sensors.
    @GetMapping
    public @ResponseBody Iterable<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    // return all devices that need to be bootstrapped.
    @GetMapping("/bs/no")
    public List<Sensor> getAllNoBs() {
        List<Sensor> all_nobs_sensors = new ArrayList<>();
        Iterable<Sensor> all_sensors = sensorRepository.findAll();
        for (Sensor c : all_sensors) {
            if (c.getAuth() == null) {
                all_nobs_sensors.add(c);
            }
        }
        return all_nobs_sensors;
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

    // Update a sensor.
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

    // Delete a sensor.
    @DeleteMapping("/{id}")
    public String deleteSensor (@PathVariable (value="id") long sensorId) {
        if (sensorRepository.findSensorById(sensorId) == null) {
            return "The device is not being bootstrapped, please check!";
        }

        Sensor sensor = sensorRepository.findSensorById(sensorId);
        String access_server = sensor.getAuth() + "/sensors/" + sensorId;
        StringBuffer response = new StringBuffer();
        try {
            URL urlObj = new URL(access_server);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("DELETE");
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

        if (response.toString().equals("Delete Succeed")) {
            sensorRepository.delete(sensor);
            sensor_registered.remove(sensorId);
            return "Delete success";
        }
        return "Something is wrong with your deletion.";
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
    @PostMapping("/register/{id}")
    public String registerSensor(@PathVariable(value = "id") long sensorId) {
        Sensor sensor = sensorRepository.findSensorById(sensorId);
        String access_server = sensor.getAuth() + "/sensors/register/" + sensorId;
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

        if (response.toString().substring(0,12).equals("Registration")) {
            sensor.setState(1);
            sensorRepository.save(sensor);
            sensor_registered.add(sensorId);
        }
        return response.toString();
    }

    @GetMapping("/register/no")
    public List<Sensor> getAllNoRegister() {
        Iterable<Sensor> all_sensors = sensorRepository.findAll();
        List<Sensor> noreg_sensors = new ArrayList<>();

        for (Sensor c : all_sensors) {
            if (!sensor_registered.contains(c.getId())) {
                noreg_sensors.add(c);
            }
        }
        return noreg_sensors;
    }


    public void notify(Sensor sensor, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"");
        sb.append("sensor_id");
        sb.append("\" : ");
        sb.append("\"");
        sb.append(Long.toString(sensor.getId()));
        sb.append("\"");
        sb.append("\"");
        sb.append("message");
        sb.append("\" : \"");
        sb.append(message);
        sb.append("\"}");

        String access_server = sensor.getAuth() + "/sensors/notify/" + sensor.getId();
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(access_server);
            String content = sb.toString();
            StringEntity params = new StringEntity(content);

            request.setHeader("content-type", "application/json");
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
        }
        catch (Exception e) {
            System.out.println(e);
        }
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

            // System.out.print(content);
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
        StringBuilder sb = new StringBuilder();
        if (response_message.toString().equals("Update succeed!")) {
            if (map.containsKey("sensor_id")) {
                sensor.setId(Long.parseLong(map.get("sensor_id")));
                if (sensor.getObserve() == 1) {
                    sb.append("Sensor id is updated to " + Integer.parseInt(map.get("sensor_id")));
                    sb.append("\n");
                }
            }

            if (map.containsKey("state")) {
                sensor.setState(Integer.parseInt(map.get("state")));
                if (sensor.getObserve() == 1) {
                    sb.append("Sensor state is updated to ");
                    if (Integer.parseInt(map.get("state")) == 1) {
                        sb.append("on");
                    } else {
                        sb.append("off");
                    }
                    sb.append("\n");
                }
            }

            if (map.containsKey("access_mode")) {
                sensor.setAccess_mode(Integer.parseInt(map.get("access_mode")));
                if (sensor.getObserve() == 1) {
                    sb.append("Sensor access mode is updated to ");
                    if (Integer.parseInt(map.get("access_mode")) == 0) {
                        sb.append("Read Only");
                    } else if (Integer.parseInt(map.get("access_mode")) == 1){
                        sb.append("Write Only");
                    } else {
                        sb.append("Read and Write");
                    }
                    sb.append("\n");
                }
            }

            this.sensorRepository.save(sensor);

            sb.append("Update device id: " + sensorId + " succeed!");

            if (sensor.getObserve() == 1) {
                notify(sensor,sb.toString());
                sb.append("\n Server notified.");
            }

            return sb.toString();
        } else {
            return "Something is wrong!";
        }
    }

    // Update the local data stored in the client (sensor)
    // change water received and sunlight only.
    // need to give the information to the server to see if the sprinkler need to turn off.
    @PostMapping("/update/data/{id}")
    public String updateData(@PathVariable(value = "id") long sensorId, @RequestBody Map<String, String> map) {
            if (sensorRepository.findSensorById(sensorId) == null) {
                return "The device is not being bootstrapped, please check!";
            }
            Sensor sensor = sensorRepository.findSensorById(sensorId);
            StringBuilder sb = new StringBuilder();
            if (map.containsKey("sunlight")) {
                sensor.setSunlight(Integer.parseInt(map.get("sunlight")));
                if (sensor.getObserve() == 1) {
                    sb.append("Sunlight is updated to " + Integer.parseInt(map.get("sunlight")));
                    sb.append("\n");
                }
            }
            if (map.containsKey("water_received")) {
                sensor.setWater_received(Integer.parseInt(map.get("water_received")));
                if (sensor.getObserve() == 1) {
                    sb.append("Water received is updated to " + Integer.parseInt(map.get("water_received")));
                    sb.append("\n");
                }
            }
            this.sensorRepository.save(sensor);

            // send the information to the server to see if it needs to turn off the sprinkler

            String access_server = sensor.getAuth() + "/areas/check/" + sensorId;

            HttpClient httpClient = HttpClientBuilder.create().build();

            StringBuffer response_message = new StringBuffer();

            try {
                HttpPost request = new HttpPost(access_server);
                StringBuilder sb_2 = new StringBuilder();
                sb_2.append("{");
                for (String c : map.keySet()) {
                    sb_2.append("\"");
                    sb_2.append(c);
                    sb_2.append("\" : ");
                    sb_2.append("\"");
                    sb_2.append(map.get(c));
                    sb_2.append("\",");
                }
                sb_2.deleteCharAt(sb_2.length() - 1);
                sb_2.append("}");
                String content = sb_2.toString();

                // System.out.print(content);
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

            // need to check if the sprinkler has been turned off.

            if (response_message.toString().length() > 20 && response_message.toString().substring(0,22).equals("Sprinkler turned off. ")) {
                long sprinklerId = Long.parseLong(response_message.toString().substring(22));
                Sprinkler sprinkler = sprinklerRepository.findSprinklerById(sprinklerId);
                sprinkler.setState(0);
                sb.append(response_message.toString());
            } else {
                sb.append(response_message.toString());
            }

            sb.append("Update complete");

            if (sensor.getObserve() == 1) {
                notify(sensor,sb.toString());
                sb.append("\n Server notified.");
            }
            return sb.toString();
    }

    @PostMapping("/observe/{id}")
    public String observe(@PathVariable(value = "id") long sensorId, @RequestBody Map<String, String> map) {
        if (sensorRepository.findSensorById(sensorId) == null) {
            return "The device is not being bootstrapped, please check!";
        }
        Sensor sensor = sensorRepository.findSensorById(sensorId);
        String access_server = sensor.getAuth() + "/sensors/observe/" + sensorId;

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
        if (response_message.toString().equals("Observe on")) {
            if (map.containsKey("observe")) {
                sensor.setObserve(Integer.parseInt(map.get("observe")));
            }
            this.sensorRepository.save(sensor);
            return "Sensor of ID " + sensorId + " is being observed.";
        } else {
            if (map.containsKey("observe")) {
                sensor.setObserve(Integer.parseInt(map.get("observe")));
            }
            this.sensorRepository.save(sensor);
            return "Device is not being observed";
        }
    }

    @GetMapping("/discover/{id}")
    public String discover(@PathVariable(value = "id") long sensorId) {
        try {
            Sensor sensor = sensorRepository.findSensorById(sensorId);
            StringBuilder sb = new StringBuilder();
            sb.append("This is Sensor " + sensorId + "\n");
            sb.append("The current sunlight amount is " + sensor.getSunlight() + "\n");
            sb.append("The current water received is " + sensor.getWater_received() + "\n");
            sb.append("The sensor is ");
            if (sensor.getState() == 1) {
                sb.append("on" + "\n");
            } else {
                sb.append("off" + "\n");
            }
            sb.append("The access mode is ");
            if (sensor.getAccess_mode() == 0) {
                sb.append("read only" + "\n");
            } else if (sensor.getAccess_mode() == 1) {
                sb.append("write only" + "\n");
            } else {
                sb.append("read and write" + "\n");
            }
            if (sensor.getObserve() == 1) {
                sb.append("The device is being observed");
            } else {
                sb.append("The device is not being observed");
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }
}
