package cmpe273.group6.client.Service;

import cmpe273.group6.client.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findPaymentById(long Id);
    Payment findPaymentByUser(long Id);
    List<Payment> findAllByUser(long Id);
}
