package cmpe273.group6.client.Controller;

import cmpe273.group6.client.Entity.Sprinkler;
import cmpe273.group6.client.Service.SprinklerRepository;
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
import java.util.ArrayList;
import java.util.List;

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
        sprinkler.setState(sprinklerDetails.getState());
        Sprinkler updateSprinkler = sprinklerRepository.save(sprinkler);
        return updateSprinkler;
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

    // Bootstrap a sprinkle.
    @GetMapping("/bs/{id}")
    public String bootStrapSprinkler(@PathVariable(value = "id") long sprinklerId) {
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

    // Register a Sprinkler
    @PostMapping("/register/{id}")
    public String registerSprinkler(@PathVariable(value = "id") long sprinklerId) {
        Sprinkler sprinkler = sprinklerRepository.findSprinklerById(sprinklerId);
        String access_server = sprinkler.getAuth() + "/sprinklers/register/" + sprinklerId;
        URLConnection client = null;
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(access_server);
            client = url.openConnection();
            client.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(
                    client.getOutputStream());
            out.write(Long.toString(sprinklerId));
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
            sprinkler.setState(1);
            sprinklerRepository.save(sprinkler);
        }

        return response.toString();
    }

    // return all devices that need to be bootstrapped.
    @GetMapping("/bs/no")
    public List<Sprinkler> getAllSprinklerNoBs() {
        List<Sprinkler> all_nobs_sprinklers = new ArrayList<>();
        Iterable<Sprinkler> all_sprinklers = sprinklerRepository.findAll();
        for (Sprinkler c : all_sprinklers) {
            if (c.getAuth() == null) {
                all_nobs_sprinklers.add(c);
            }
        }
        return all_nobs_sprinklers;
    }
}
