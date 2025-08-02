package com.payment.controller;

import com.payment.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@CrossOrigin(origins = "*")
public class WebhookController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(@RequestBody String payload,
                                                       @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            // Parse the webhook payload
            JSONObject webhookData = new JSONObject(payload);
            
            // Extract payment information
            String event = webhookData.getString("event");
            JSONObject payloadData = webhookData.getJSONObject("payload");
            JSONObject payment = payloadData.getJSONObject("payment");
            JSONObject order = payloadData.getJSONObject("order");
            
            String paymentId = payment.getString("id");
            String orderId = order.getString("id");
            String status = payment.getString("status");
            
            // Process the webhook based on event type
            switch (event) {
                case "payment.captured":
                    paymentService.processPaymentSuccess(paymentId, orderId);
                    break;
                case "payment.failed":
                    paymentService.processPaymentFailure(paymentId, orderId);
                    break;
                case "payment.refunded":
                    paymentService.processPaymentRefund(paymentId, orderId);
                    break;
                default:
                    // Log unhandled events
                    System.out.println("Unhandled webhook event: " + event);
            }
            
            return ResponseEntity.ok("Webhook processed successfully");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to process webhook: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testWebhook() {
        return ResponseEntity.ok(Map.of("message", "Webhook endpoint is working"));
    }
} 