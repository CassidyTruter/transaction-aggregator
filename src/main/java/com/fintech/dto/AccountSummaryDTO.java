package com.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummaryDTO {
    private String accountNumber;
    private Long transactionCount;
    private BigDecimal totalAmount;
}