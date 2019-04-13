package cmpe273.group6.client.Controller;

import cmpe273.group6.client.Entity.Sprinkler;
import cmpe273.group6.client.Service.SprinklerRepository;
import org.springframework.web.bind.annotation.*;

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
    Iterable<Sprinkler> getAllSensors() {
        return sprinklerRepository.findAll();
    }

    // Create a single sprinkler.
    @PostMapping
    public Sprinkler createSensor(@RequestBody Sprinkler sprinkler) {
        return sprinklerRepository.save(sprinkler);
    }

}
