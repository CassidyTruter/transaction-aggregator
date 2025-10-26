package com.fintech.dto;

import com.fintech.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryDTO {
    private Category category;
    private Long transactionCount;
    private BigDecimal totalAmount;
}