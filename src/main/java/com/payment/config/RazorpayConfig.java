package com.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Configuration
public class RazorpayConfig {
    
    @Value("${razorpay.key.id:test_key}")
    private String keyId;
    
    @Value("${razorpay.key.secret:test_secret}")
    private String keySecret;
    
    @Bean
    @Profile("!test")
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(keyId, keySecret);
    }
    
    @Bean
    @Profile("test")
    public RazorpayClient razorpayClientTest() throws RazorpayException {
        // For testing, we'll create a client with test credentials
        return new RazorpayClient("rzp_test_test", "test_secret");
    }
} 