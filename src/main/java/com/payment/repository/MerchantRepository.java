package com.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payment.entity.Merchant;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    
    Optional<Merchant> findByMerchantId(String merchantId);
    
    Optional<Merchant> findByEmail(String email);
    
    Optional<Merchant> findByApiKey(String apiKey);
    
    List<Merchant> findByStatus(Merchant.MerchantStatus status);
    
    List<Merchant> findByCountry(String country);
    
    List<Merchant> findByIsLiveMode(Boolean isLiveMode);
    
    @Query("SELECT m FROM Merchant m WHERE m.businessName LIKE %:businessName%")
    List<Merchant> findByBusinessNameContaining(@Param("businessName") String businessName);
    
    @Query("SELECT m FROM Merchant m WHERE m.email = :email AND m.status = :status")
    Optional<Merchant> findByEmailAndStatus(@Param("email") String email, 
                                           @Param("status") Merchant.MerchantStatus status);
    
    @Query("SELECT COUNT(m) FROM Merchant m WHERE m.status = :status")
    long countByStatus(@Param("status") Merchant.MerchantStatus status);
    
    @Query("SELECT COUNT(m) FROM Merchant m WHERE m.isLiveMode = :isLiveMode")
    long countByIsLiveMode(@Param("isLiveMode") Boolean isLiveMode);
    
    // Pagination support
    Page<Merchant> findByStatus(Merchant.MerchantStatus status, Pageable pageable);
    
    Page<Merchant> findByCountry(String country, Pageable pageable);
    
    Page<Merchant> findByIsLiveMode(Boolean isLiveMode, Pageable pageable);
    
    @Query("SELECT m FROM Merchant m WHERE m.businessName LIKE %:businessName%")
    Page<Merchant> findByBusinessNameContaining(@Param("businessName") String businessName, Pageable pageable);
    
    // Analytics queries
    @Query("SELECT m.country, COUNT(m) FROM Merchant m GROUP BY m.country")
    List<Object[]> getMerchantStatsByCountry();
    
    @Query("SELECT m.status, COUNT(m) FROM Merchant m GROUP BY m.status")
    List<Object[]> getMerchantStatsByStatus();
    
    @Query("SELECT m.isLiveMode, COUNT(m) FROM Merchant m GROUP BY m.isLiveMode")
    List<Object[]> getMerchantStatsByMode();
} 