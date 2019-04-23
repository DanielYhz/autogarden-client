package cmpe273.group6.client.Controller;


import cmpe273.group6.client.Entity.User;
import cmpe273.group6.client.Service.UserRepository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;

    UserController (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users.
    @GetMapping
    public @ResponseBody
    Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Create a single user.
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // Get a single user.
    @GetMapping("/{id}")
    public User getUserById(@PathVariable(value = "id") long userId) {
        // .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId))
        return userRepository.findUserById(userId);
    }

    // Update a user.
    // Update a user can only update the email, last name, and first name.
    @PutMapping(value = "/{id}")
    public String updateUser(@PathVariable(value = "id") long userId, @RequestBody Map<String, String> map) {
        // userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
        User user = userRepository.findUserById(userId);
        if (map.containsKey("email")) {
            user.setEmail(map.get("email"));
        }
        if (map.containsKey("first_name")) {
            user.setFirst_name(map.get("first_name"));
        }
        if (map.containsKey("last_name")) {
            user.setLast_name(map.get("last_name"));
        }
        User updateUser = userRepository.save(user);
        return "Update success";
    }

    @DeleteMapping(value = "/{id}")
    public String deleteUser(@PathVariable(value = "id") long userId) {
        if (userRepository.findUserById(userId) != null) {
            this.userRepository.delete(userRepository.findUserById(userId));
            return "User deleted.";
        }
        return "User does not exist.";
    }
}













