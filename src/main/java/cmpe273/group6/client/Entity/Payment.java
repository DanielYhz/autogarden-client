package cmpe273.group6.client.Entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private long id;

    @Column(name = "user")
    private long user;

    @Column(name = "amount")
    private int amount;

    protected Payment() {

    }

    public Payment(long user_id, int amount) {
        this.user = user_id;
        this.amount = amount;
    }

    public long getPayment_id() {
        return id;
    }

    public void setPayment_id(long payment_id) {
        this.id = payment_id;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
