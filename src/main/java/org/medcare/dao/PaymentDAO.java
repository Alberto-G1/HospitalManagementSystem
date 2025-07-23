package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.medcare.models.Payment;

@ApplicationScoped
public class PaymentDAO extends GenericDAO<Payment> {
    public PaymentDAO() { super(Payment.class); }
}