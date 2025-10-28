package com.fintech.repository;

import com.fintech.entity.Transaction;
import com.fintech.enums.Category;
import com.fintech.enums.SourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCategory(Category category);

    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findBySourceType(SourceType sourceType);

    List<Transaction> findByAccountNumber(String accountNumber);

    List<Transaction> findByCategoryAndTransactionDateBetween(
            Category category,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Transaction> findByAccountNumberAndTransactionDateBetween(
            String accountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:category IS NULL OR t.category = :category) AND " +
            "(:sourceType IS NULL OR t.sourceType = :sourceType) AND " +
            "(:accountNumber IS NULL OR t.accountNumber = :accountNumber) AND " +
            "(CAST(:startDate AS timestamp) IS NULL OR t.transactionDate >= :startDate) AND " +
            "(CAST(:endDate AS timestamp) IS NULL OR t.transactionDate <= :endDate)")
    List<Transaction> findByFilters(
            @Param("category") Category category,
            @Param("sourceType") SourceType sourceType,
            @Param("accountNumber") String accountNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}