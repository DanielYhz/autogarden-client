package cmpe273.group6.client.Controller;

import cmpe273.group6.client.Entity.Camera;
import cmpe273.group6.client.Service.CameraRepository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/cameras")
public class CameraController {

    private CameraRepository cameraRepository;

    public CameraController(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
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
        camera.setState(cameraDetails.isState());
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

}
