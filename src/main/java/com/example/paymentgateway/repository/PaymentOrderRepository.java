package com.example.paymentgateway.repository;

import com.example.paymentgateway.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    
    Optional<PaymentOrder> findByRazorpayOrderId(String razorpayOrderId);
    
    Optional<PaymentOrder> findByRazorpayPaymentId(String razorpayPaymentId);
}