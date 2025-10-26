package com.fintech.dto;

import com.fintech.enums.Category;
import com.fintech.enums.SourceType;
import com.fintech.enums.Subcategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private String transactionId;
    private String clientId;
    private String accountNumber;
    private SourceType sourceType;
    private String description;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private Category category;
    private Subcategory subcategory;
    private String merchantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}