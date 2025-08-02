package com.payment.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.entity.Merchant;
import com.payment.repository.MerchantRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/merchants")
@CrossOrigin(origins = "*")
public class MerchantController {
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    @PostMapping("/register")
    public ResponseEntity<Merchant> registerMerchant(@Valid @RequestBody Merchant merchant) {
        try {
            // Generate merchant ID if not provided
            if (merchant.getMerchantId() == null || merchant.getMerchantId().isEmpty()) {
                merchant.setMerchantId("MERCH_" + System.currentTimeMillis());
            }
            
            merchant.setStatus(Merchant.MerchantStatus.ACTIVE);
            Merchant savedMerchant = merchantRepository.save(merchant);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMerchant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        Optional<Merchant> merchant = merchantRepository.findById(id);
        return merchant.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/merchant-id/{merchantId}")
    public ResponseEntity<Merchant> getMerchantByMerchantId(@PathVariable String merchantId) {
        Optional<Merchant> merchant = merchantRepository.findByMerchantId(merchantId);
        return merchant.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Merchant> getMerchantByEmail(@PathVariable String email) {
        Optional<Merchant> merchant = merchantRepository.findByEmail(email);
        return merchant.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Merchant>> getMerchantsByStatus(@PathVariable Merchant.MerchantStatus status) {
        List<Merchant> merchants = merchantRepository.findByStatus(status);
        return ResponseEntity.ok(merchants);
    }
    
    @GetMapping
    public ResponseEntity<List<Merchant>> getAllMerchants() {
        List<Merchant> merchants = merchantRepository.findAll();
        return ResponseEntity.ok(merchants);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Merchant> updateMerchantStatus(
            @PathVariable Long id,
            @RequestBody Merchant.MerchantStatus status) {
        Optional<Merchant> merchantOpt = merchantRepository.findById(id);
        if (merchantOpt.isPresent()) {
            Merchant merchant = merchantOpt.get();
            merchant.setStatus(status);
            Merchant updatedMerchant = merchantRepository.save(merchant);
            return ResponseEntity.ok(updatedMerchant);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Merchant> updateMerchant(@PathVariable Long id, @Valid @RequestBody Merchant merchantDetails) {
        Optional<Merchant> merchantOpt = merchantRepository.findById(id);
        if (merchantOpt.isPresent()) {
            Merchant merchant = merchantOpt.get();
            
            // Update fields
            merchant.setBusinessName(merchantDetails.getBusinessName());
            merchant.setContactName(merchantDetails.getContactName());
            merchant.setEmail(merchantDetails.getEmail());
            merchant.setPhone(merchantDetails.getPhone());
            merchant.setAddress(merchantDetails.getAddress());
            merchant.setCity(merchantDetails.getCity());
            merchant.setState(merchantDetails.getState());
            merchant.setPostalCode(merchantDetails.getPostalCode());
            merchant.setCountry(merchantDetails.getCountry());
            
            Merchant updatedMerchant = merchantRepository.save(merchant);
            return ResponseEntity.ok(updatedMerchant);
        }
        return ResponseEntity.notFound().build();
    }
} 