package com.fintech.dto;

import com.fintech.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawTransactionDTO {
    private String transactionId;
    private String clientId;
    private String accountNumber;
    private SourceType sourceType;
    private String description;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
}