package cmpe273.group6.client.Controller;

import cmpe273.group6.client.Entity.Camera;
import cmpe273.group6.client.Entity.Sprinkler;
import cmpe273.group6.client.Service.CameraRepository;
import cmpe273.group6.client.Service.SprinklerRepository;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
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
import java.util.Map;

@RestController
@RequestMapping("/cameras")
public class CameraController {

    private CameraRepository cameraRepository;
    private SprinklerRepository sprinklerRepository;

    public CameraController(CameraRepository cameraRepository, SprinklerRepository sprinklerRepository) {
        this.cameraRepository = cameraRepository;
        this.sprinklerRepository = sprinklerRepository;
    }

    // Get all cameras
    @GetMapping
    public @ResponseBody
    Iterable<Camera> getAllSprinklers() {
        return cameraRepository.findAll();
    }

    // Create a single cameras.
    @PostMapping
    public Camera createSprinkler(@RequestBody Camera camera) {
        return cameraRepository.save(camera);
    }

    // Get a single cameras.
    @GetMapping("/{id}")
    public Camera getSprinklerById(@PathVariable(value="id") long cameraId) {

        return cameraRepository.findCameraById(cameraId);
    }

    // update a cameras.
    @PutMapping(value = "/{id}")
    public Camera updateSprinkler(@PathVariable(value="id") long cameraId, @Valid @RequestBody Camera cameraDetails) {
        Camera camera = cameraRepository.findCameraById(cameraId);
        camera.setId(cameraDetails.getId());
        camera.setState(cameraDetails.getState());
        Camera updateCamera = cameraRepository.save(camera);
        return camera;
    }

    // Delete a camera.
    @DeleteMapping("/{id}")
    public String deleteSprinkler (@PathVariable (value="id") long cameraId) {
        if (cameraRepository.findCameraById(cameraId) == null) {
            return "The camera is not being bootstrapped, please check!";
        }

        Camera camera = cameraRepository.findCameraById(cameraId);
        String access_server = camera.getAuth() + "/cameras/" + cameraId;
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
            cameraRepository.delete(camera);
            return "Delete success";
        }
        return "Something is wrong with your deletion.";
    }

    // Bootstrap a sensor.
    @GetMapping("/bs/{id}")
    public String bootStrapSprinkler(@PathVariable(value = "id") long cameraId) {
        // System.out.println(sensorDetails.getId());
        Camera camera = cameraRepository.findCameraById(cameraId);
        String bs_fid = camera.getFid();
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
            camera.setAuth(response.toString());
            cameraRepository.save(camera);

            return "Bootstrap Succeed.";
        } else {
            return response.toString();
        }
    }

    // Register a camera
    @PostMapping("/register/{id}")
    public String registerCamera(@PathVariable(value = "id") long cameraId) {
        Camera camera = cameraRepository.findCameraById(cameraId);
        String access_server = camera.getAuth() + "/cameras/register/" + cameraId;
        URLConnection client = null;
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(access_server);
            client = url.openConnection();
            client.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(
                    client.getOutputStream());
            out.write(Long.toString(cameraId));
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
            camera.setState(1);
            cameraRepository.save(camera);
        }

        return response.toString();
    }


    @PostMapping("/detect/{id}")
    public String detectPeople(@PathVariable(value = "id") long cameraId, @RequestBody Map<String, String> map) {
        if (cameraRepository.findCameraById(cameraId) == null) {
            return "The camera is not being bootstrapped, please check!";
        }

        Camera camera = cameraRepository.findCameraById(cameraId);
        if (map.containsKey("detect")) {
            camera.setDetection(Integer.parseInt(map.get("detect")));
        }
        cameraRepository.save(camera);
        System.out.println(map.get("detect"));
        if (Integer.parseInt(map.get("detect")) == 1 || Integer.parseInt(map.get("detect")) == 0) {
            // detect people, turn off the sprinkler of that area.
            // send the information to the server to see if it needs to turn off the sprinkler
            // System.out.println("it's in!!!");
            String access_server = camera.getAuth() + "/areas/detect/" + cameraId;
            // System.out.println(access_server);

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

            if (response_message.toString().equals("The camera is not being registered.") || response_message.toString().equals("The camera is not being added to any area.")
            || response_message.toString().equals("There is no sprinkler in this area") || response_message.toString().equals("need more information")) {
                return "nothing has been changed.";
            } else if (response_message.charAt(18) == 'f') {
                Sprinkler sprinkler = sprinklerRepository.findSprinklerById(Long.parseLong(response_message.substring(22)));
                sprinkler.setState(0);
                sprinklerRepository.save(sprinkler);
            } else if (response_message.charAt(18) == 'n') {
                Sprinkler sprinkler = sprinklerRepository.findSprinklerById(Long.parseLong(response_message.substring(22)));
                sprinkler.setState(1);
                sprinklerRepository.save(sprinkler);
            } else {
                return "Sprinkler status has been updated";
            }
        } else {
            return "Invalid detection.";
        }
        return "Detection complete.";
    }
}
