package com.payment.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByOrderId(String orderId);
    
    Optional<Payment> findByPaymentId(String paymentId);
    
    List<Payment> findByCustomerEmail(String customerEmail);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    List<Payment> findByMerchantId(String merchantId);
    
    List<Payment> findByMerchantIdAndStatus(String merchantId, Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.customerEmail = :email AND p.status = :status")
    List<Payment> findByCustomerEmailAndStatus(@Param("email") String email, 
                                              @Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.merchantId = :merchantId AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByMerchantIdAndDateRange(@Param("merchantId") String merchantId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.merchantId = :merchantId AND p.status = :status")
    long countByMerchantIdAndStatus(@Param("merchantId") String merchantId, 
                                   @Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalAmountByDateRange(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.merchantId = :merchantId AND p.status = 'SUCCESS' AND p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalAmountByMerchantIdAndDateRange(@Param("merchantId") String merchantId,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount AND p.status = :status")
    List<Payment> findByAmountRangeAndStatus(@Param("minAmount") BigDecimal minAmount,
                                            @Param("maxAmount") BigDecimal maxAmount,
                                            @Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentMethod = :paymentMethod AND p.status = :status")
    List<Payment> findByPaymentMethodAndStatus(@Param("paymentMethod") String paymentMethod,
                                              @Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.currency = :currency AND p.status = :status")
    List<Payment> findByCurrencyAndStatus(@Param("currency") String currency,
                                         @Param("status") Payment.PaymentStatus status);
    
    // Pagination support
    Page<Payment> findByStatus(Payment.PaymentStatus status, Pageable pageable);
    
    Page<Payment> findByMerchantId(String merchantId, Pageable pageable);
    
    Page<Payment> findByCustomerEmail(String customerEmail, Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    Page<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  Pageable pageable);
    
    // Analytics queries
    @Query("SELECT p.currency, COUNT(p), SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.createdAt BETWEEN :startDate AND :endDate GROUP BY p.currency")
    List<Object[]> getPaymentStatsByCurrency(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.createdAt BETWEEN :startDate AND :endDate GROUP BY p.paymentMethod")
    List<Object[]> getPaymentStatsByMethod(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT DATE(p.createdAt), COUNT(p), SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(p.createdAt) ORDER BY DATE(p.createdAt)")
    List<Object[]> getDailyPaymentStats(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
} 