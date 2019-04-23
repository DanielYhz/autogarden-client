package cmpe273.group6.client.Controller;


import cmpe273.group6.client.Entity.Payment;
import cmpe273.group6.client.Entity.User;
import cmpe273.group6.client.Service.PaymentRepository;
import cmpe273.group6.client.Service.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;
    private PaymentRepository paymentRepository;

    UserController (UserRepository userRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
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

    @PostMapping(value="/subscriptions/{id}")
    public String subscriptionUser(@PathVariable(value = "id") long userId, @RequestBody Map<String, String> map) {
        if (userRepository.findUserById(userId) == null) {
            return "User does not exist.";
        }

        User user = userRepository.findUserById(userId);
        if (map.containsKey("payment_plan")) {
            if (map.get("payment_plan").equals("regular")) {
                user.setPayment_plan(1);
                Payment first_charge = new Payment(userId, 10);
                paymentRepository.save(first_charge);
            } else if (map.get("payment_plan").equals("premium")) {
                user.setPayment_plan(2);
                Payment first_charge = new Payment(userId, 20);
                paymentRepository.save(first_charge);
            } else if (map.get("payment_plan").equals("cancel")) {
                user.setPayment_plan(0);
                return "You have successfully unsubscribed.";
            } else {
                return "Invalid input.";
            }
        }
        userRepository.save(user);
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate);
        return user.getFirst_name() + ", thanks for your subscription";
    }

    @GetMapping(value="/observe/{id}")
    public String observeDevice(@PathVariable(value = "id") long userId) {
        if (userRepository.findUserById(userId) == null) {
            return "User does not exist.";
        }

        User user = userRepository.findUserById(userId);
        if (user.getPayment_plan() == 0) {
            return "Please subscribe.";
        } else {
            return "You can now observe the system.";
        }
    }

    @PostMapping(value="/repair/{id}")
    public String repairDevice(@PathVariable(value = "id") long userId, @RequestBody Map<String, String> map) {
        if (userRepository.findUserById(userId) == null) {
            return "User does not exist.";
        }

        User user = userRepository.findUserById(userId);
        if (user.getPayment_plan() == 0) {
            return "Please subscribe.";
        } else if (user.getPayment_plan() == 1) {
            return "You are a basic user.";
        } else {
            if (map.containsKey("repair")) {
                return "The device is under repair.";
            }
            if (map.containsKey("maintenance")) {
                return "The device is under maintenance";
            }
        }
        return "Request done.";
    }
}













