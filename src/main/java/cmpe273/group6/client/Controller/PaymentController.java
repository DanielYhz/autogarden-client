package cmpe273.group6.client.Controller;

import cmpe273.group6.client.Entity.Payment;
import cmpe273.group6.client.Entity.User;
import cmpe273.group6.client.Service.PaymentRepository;
import cmpe273.group6.client.Service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private PaymentRepository paymentRepository;
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;

    PaymentController (PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    // Get all payments.
    @GetMapping
    public @ResponseBody
    Iterable<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Create a single payment.
    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentRepository.save(payment);
    }

    // Get payments for user.
    // need to choose the right find all user to return the list.
    @GetMapping("/{id}")
    public List<Payment> getPaymentById(@PathVariable(value = "id") long userId) {
        // .orElseThrow(() -> new ResourceNotFoundException("Payment", "Id", paymentId))
        Iterable<Payment> all_payments = paymentRepository.findAll();
        List<Payment> user_allpayments = new ArrayList<>();
        for (Payment c : all_payments) {
            if (c.getUser() == userId) {
                user_allpayments.add(c);
            }
        }
        return user_allpayments;
    }

    // Update a payment.
    // Update a payment can only update the email, last name, and first name.
    @PutMapping(value = "/{id}")
    public String updatePayment(@PathVariable(value = "id") long paymentId, @RequestBody Map<String, String> map) {
        // paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payement", "Id", paymentId));
        Payment payment = paymentRepository.findPaymentById(paymentId);
        if (map.containsKey("user_id")) {
            payment.setUser(Long.parseLong(map.get("user_id")));
        }
        if (map.containsKey("amount")) {
            payment.setAmount(Integer.parseInt(map.get("amount")));
        }
        Payment updatePayment = paymentRepository.save(payment);
        return "Update success";
    }

    @DeleteMapping(value = "/{id}")
    public String deletePayment(@PathVariable(value = "id") long paymentId) {
        if (paymentRepository.findPaymentById(paymentId) != null) {
            this.paymentRepository.delete(paymentRepository.findPaymentById(paymentId));
            return "Payment deleted.";
        }
        return "Payment does not exist.";
    }

    @GetMapping(value = "/charge")
    public String chargeAll() {
        Iterable<User> all_users = userRepository.findAll();
//        for (User c : all_users) {
//            if (c.getPayment_plan() != 0) {
//                // charge user need to send out email, have bills.
//                // charge user need to add charged amount to the payment database.
////                chargeUser(c.getId());
////            }
//        }

        for (User c : all_users) {
            if (c.getPayment_plan() != 0) {
                sendEmail(c.getEmail(), c.getPayment_plan());
            }
        }
        return "Charged for all users";
    }

    private void sendEmail(String address, int payment_plan) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setBcc();

        msg.setTo(address);
        msg.setSubject("Your monthly bill");
        if (payment_plan == 1) {
            msg.setText("Charged $10");
        } else {
            msg.setText("Charged $20");
        }
        try {
            javaMailSender.send(msg);
        } catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }


    @GetMapping(value = "/sendEmail")

    @ResponseBody
    public boolean sendEmail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setBcc();

        msg.setTo("yhzdaniel@gmail.com");
        msg.setSubject("Java技术栈投稿");
        msg.setText("技术分享");
        try {
            javaMailSender.send(msg);
        } catch (MailException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }

}
