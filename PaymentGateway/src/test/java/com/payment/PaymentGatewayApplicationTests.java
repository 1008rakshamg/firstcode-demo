package com.payment;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.entity.Payment;
import com.payment.service.PaymentService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.profiles.active=test",
    "razorpay.key.id=rzp_test_test",
    "razorpay.key.secret=test_secret"
})
class PaymentGatewayApplicationTests {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
        assertTrue(true);
    }

    @Test
    void testCreatePayment() {
        // Create a payment request
        PaymentRequest request = new PaymentRequest();
        request.setCustomerName("Test Customer");
        request.setCustomerEmail("test@example.com");
        request.setCustomerPhone("+1234567890");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("USD");
        request.setDescription("Test payment");
        request.setNotes("Test notes");

        // Create payment
        PaymentResponse response = paymentService.createPayment(request);

        // Assertions
        assertNotNull(response);
        assertNotNull(response.getOrderId());
        assertNotNull(response.getPaymentId());
        assertEquals("Test Customer", response.getCustomerName());
        assertEquals("test@example.com", response.getCustomerEmail());
        assertEquals("+1234567890", response.getCustomerPhone());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals("USD", response.getCurrency());
        assertEquals(Payment.PaymentStatus.PENDING, response.getStatus());
        assertNotNull(response.getPaymentUrl());
    }

    @Test
    void testGetPaymentById() {
        // First create a payment
        PaymentRequest request = new PaymentRequest();
        request.setCustomerName("Test Customer");
        request.setCustomerEmail("test@example.com");
        request.setCustomerPhone("+1234567890");
        request.setAmount(new BigDecimal("50.00"));
        request.setCurrency("EUR");
        request.setDescription("Test payment for retrieval");

        PaymentResponse createdResponse = paymentService.createPayment(request);
        assertNotNull(createdResponse.getId());

        // Get payment by ID
        PaymentResponse retrievedResponse = paymentService.getPaymentById(createdResponse.getId());

        // Assertions
        assertNotNull(retrievedResponse);
        assertEquals(createdResponse.getId(), retrievedResponse.getId());
        assertEquals(createdResponse.getOrderId(), retrievedResponse.getOrderId());
        assertEquals(createdResponse.getPaymentId(), retrievedResponse.getPaymentId());
    }

    @Test
    void testGetPaymentByOrderId() {
        // First create a payment
        PaymentRequest request = new PaymentRequest();
        request.setCustomerName("Order Test Customer");
        request.setCustomerEmail("order@example.com");
        request.setCustomerPhone("+9876543210");
        request.setAmount(new BigDecimal("75.00"));
        request.setCurrency("GBP");
        request.setDescription("Test payment for order retrieval");

        PaymentResponse createdResponse = paymentService.createPayment(request);
        assertNotNull(createdResponse.getOrderId());

        // Get payment by order ID
        PaymentResponse retrievedResponse = paymentService.getPaymentByOrderId(createdResponse.getOrderId());

        // Assertions
        assertNotNull(retrievedResponse);
        assertEquals(createdResponse.getOrderId(), retrievedResponse.getOrderId());
        assertEquals(createdResponse.getPaymentId(), retrievedResponse.getPaymentId());
    }

    @Test
    void testGetPaymentsByCustomerEmail() {
        // Create multiple payments for the same customer
        String customerEmail = "multi@example.com";
        
        PaymentRequest request1 = new PaymentRequest();
        request1.setCustomerName("Multi Customer");
        request1.setCustomerEmail(customerEmail);
        request1.setCustomerPhone("+1111111111");
        request1.setAmount(new BigDecimal("25.00"));
        request1.setCurrency("USD");
        request1.setDescription("First payment");

        PaymentRequest request2 = new PaymentRequest();
        request2.setCustomerName("Multi Customer");
        request2.setCustomerEmail(customerEmail);
        request2.setCustomerPhone("+1111111111");
        request2.setAmount(new BigDecimal("35.00"));
        request2.setCurrency("USD");
        request2.setDescription("Second payment");

        paymentService.createPayment(request1);
        paymentService.createPayment(request2);

        // Get payments by customer email
        List<PaymentResponse> payments = paymentService.getPaymentsByCustomerEmail(customerEmail);

        // Assertions
        assertNotNull(payments);
        assertTrue(payments.size() >= 2);
        
        // Verify all payments belong to the same customer
        for (PaymentResponse payment : payments) {
            assertEquals(customerEmail, payment.getCustomerEmail());
        }
    }

    @Test
    void testUpdatePaymentStatus() {
        // Create a payment
        PaymentRequest request = new PaymentRequest();
        request.setCustomerName("Status Test Customer");
        request.setCustomerEmail("status@example.com");
        request.setCustomerPhone("+2222222222");
        request.setAmount(new BigDecimal("60.00"));
        request.setCurrency("INR");
        request.setDescription("Test payment for status update");

        PaymentResponse createdResponse = paymentService.createPayment(request);
        assertEquals(Payment.PaymentStatus.PENDING, createdResponse.getStatus());

        // Update payment status to SUCCESS
        PaymentResponse updatedResponse = paymentService.updatePaymentStatus(
            createdResponse.getId(), 
            Payment.PaymentStatus.SUCCESS
        );

        // Assertions
        assertNotNull(updatedResponse);
        assertEquals(Payment.PaymentStatus.SUCCESS, updatedResponse.getStatus());
        assertEquals(createdResponse.getId(), updatedResponse.getId());
    }

    @Test
    void testRefundPayment() {
        // Create a payment
        PaymentRequest request = new PaymentRequest();
        request.setCustomerName("Refund Test Customer");
        request.setCustomerEmail("refund@example.com");
        request.setCustomerPhone("+3333333333");
        request.setAmount(new BigDecimal("80.00"));
        request.setCurrency("USD");
        request.setDescription("Test payment for refund");

        PaymentResponse createdResponse = paymentService.createPayment(request);

        // First update to SUCCESS status
        paymentService.updatePaymentStatus(createdResponse.getId(), Payment.PaymentStatus.SUCCESS);

        // Refund the payment
        PaymentResponse refundedResponse = paymentService.refundPayment(createdResponse.getId());

        // Assertions
        assertNotNull(refundedResponse);
        assertEquals(Payment.PaymentStatus.REFUNDED, refundedResponse.getStatus());
        assertEquals(createdResponse.getId(), refundedResponse.getId());
    }

    @Test
    void testGetPaymentCountByStatus() {
        // Create payments with different statuses
        PaymentRequest request1 = new PaymentRequest();
        request1.setCustomerName("Count Test Customer 1");
        request1.setCustomerEmail("count1@example.com");
        request1.setCustomerPhone("+4444444444");
        request1.setAmount(new BigDecimal("10.00"));
        request1.setCurrency("USD");
        request1.setDescription("Test payment 1");

        PaymentRequest request2 = new PaymentRequest();
        request2.setCustomerName("Count Test Customer 2");
        request2.setCustomerEmail("count2@example.com");
        request2.setCustomerPhone("+5555555555");
        request2.setAmount(new BigDecimal("20.00"));
        request2.setCurrency("USD");
        request2.setDescription("Test payment 2");

        PaymentResponse payment1 = paymentService.createPayment(request1);
        PaymentResponse payment2 = paymentService.createPayment(request2);

        // Update one payment to SUCCESS
        paymentService.updatePaymentStatus(payment1.getId(), Payment.PaymentStatus.SUCCESS);

        // Get counts
        long pendingCount = paymentService.getPaymentCountByStatus(Payment.PaymentStatus.PENDING);
        long successCount = paymentService.getPaymentCountByStatus(Payment.PaymentStatus.SUCCESS);

        // Assertions
        assertTrue(pendingCount >= 1); // At least the second payment should be pending
        assertTrue(successCount >= 1); // At least the first payment should be success
    }

    @Test
    void testHealthCheckEndpoint() {
        String url = "http://localhost:" + port + "/api/payments/health";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment Gateway is running!", response.getBody());
    }

    @Test
    void testApiDocsEndpoint() {
        String url = "http://localhost:" + port + "/api/payments/api-docs";
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testDashboardStatsEndpoint() {
        String url = "http://localhost:" + port + "/api/payments/stats/dashboard";
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testInvalidPaymentRequest() {
        // Test with invalid data
        PaymentRequest invalidRequest = new PaymentRequest();
        // Missing required fields

        try {
            paymentService.createPayment(invalidRequest);
            fail("Should have thrown an exception for invalid request");
        } catch (Exception e) {
            // Expected exception
            assertTrue(e.getMessage().contains("Failed to create payment"));
        }
    }

    @Test
    void testGetNonExistentPayment() {
        PaymentResponse response = paymentService.getPaymentById(999999L);
        assertEquals("Payment not found", response.getMessage());
    }
} 