package cmpe273.group6.client.Controller;

import cmpe273.group6.client.Entity.Sprinkler;
import cmpe273.group6.client.Service.SprinklerRepository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/sprinklers")
public class SprinklerController {

    private SprinklerRepository sprinklerRepository;

    public SprinklerController(SprinklerRepository sprinklerRepository) {
        this.sprinklerRepository = sprinklerRepository;
    }

    // Get all sprinklers
    @GetMapping
    public @ResponseBody
    Iterable<Sprinkler> getAllSprinklers() {
        return sprinklerRepository.findAll();
    }

    // Create a single sprinkler.
    @PostMapping
    public Sprinkler createSprinkler(@RequestBody Sprinkler sprinkler) {
        return sprinklerRepository.save(sprinkler);
    }

    // Get a single sprinkler.
    @GetMapping("/{id}")
    public Sprinkler getSprinklerById(@PathVariable(value="id") long sprinklerId) {
        // .orElseThrow(() -> new ResourceNotFoundException("Sprinkler", "Id", sprinklerId))
        return sprinklerRepository.findSprinklerById(sprinklerId);
    }

    // update a sprinkler.
    @PutMapping(value = "/{id}")
    public Sprinkler updateSprinkler(@PathVariable(value="id") long sprinklerId, @Valid @RequestBody Sprinkler sprinklerDetails) {
        Sprinkler sprinkler = sprinklerRepository.findSprinklerById(sprinklerDetails.getId());
        sprinkler.setId(sprinklerDetails.getId());
        sprinkler.setState(sprinklerDetails.isState());
        Sprinkler updateSprinkler = sprinklerRepository.save(sprinkler);
        return sprinkler;
    }

    // Delete a sprinkler.
    @DeleteMapping("/{id}")
    public String deleteSprinkler (@PathVariable (value="id") long sprinklerId) {
        if (sprinklerRepository.findSprinklerById(sprinklerId) == null) {
            return "The sprinkler is not being bootstrapped, please check!";
        }

        Sprinkler sprinkler = sprinklerRepository.findSprinklerById(sprinklerId);
        String access_server = sprinkler.getAuth() + "/sprinklers/" + sprinklerId;
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
            sprinklerRepository.delete(sprinkler);
            return "Delete success";
        }
        return "Something is wrong with your deletion.";
    }

    // Bootstrap a sensor.
    @GetMapping("/bs/{id}")
    public String bootStrapSprinkler(@PathVariable(value = "id") long sprinklerId) {
        // System.out.println(sensorDetails.getId());
        Sprinkler sprinkler = sprinklerRepository.findSprinklerById(sprinklerId);
        String bs_fid = sprinkler.getFid();
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
            sprinkler.setAuth(response.toString());
            sprinklerRepository.save(sprinkler);

            return "Bootstrap Succeed.";
        } else {
            return response.toString();
        }
    }
}
