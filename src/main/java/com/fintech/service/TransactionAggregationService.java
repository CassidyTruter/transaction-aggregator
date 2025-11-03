package com.fintech.service;

import com.fintech.dto.AccountSummaryDTO;
import com.fintech.dto.AggregationResultDTO;
import com.fintech.dto.RawTransactionDTO;
import com.fintech.dto.TransactionSummaryDTO;
import com.fintech.entity.Transaction;
import com.fintech.enums.Category;
import com.fintech.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionAggregationService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionAggregationService.class);

    private final TransactionRepository transactionRepository;

    public TransactionAggregationService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionSummaryDTO> getSummaryByCategory() {
        logger.info("Generating transaction summary by category");

        List<Transaction> allTransactions = transactionRepository.findAll();

        Map<Category, List<Transaction>> groupedByCategory = allTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCategory));

        return groupedByCategory.entrySet().stream()
                .map(entry -> {
                    Category category = entry.getKey();
                    List<Transaction> transactions = entry.getValue();

                    BigDecimal totalAmount = transactions.stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new TransactionSummaryDTO(
                            category,
                            (long) transactions.size(),
                            totalAmount
                    );
                })
                .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                .collect(Collectors.toList());
    }

    public List<AccountSummaryDTO> getSummaryByAccount() {
        logger.info("Generating transaction summary by account");

        List<Transaction> allTransactions = transactionRepository.findAll();

        Map<String, List<Transaction>> groupedByAccount = allTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getAccountNumber));

        return groupedByAccount.entrySet().stream()
                .map(entry -> {
                    String accountNumber = entry.getKey();
                    List<Transaction> transactions = entry.getValue();

                    BigDecimal totalAmount = transactions.stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new AccountSummaryDTO(
                            accountNumber,
                            (long) transactions.size(),
                            totalAmount
                    );
                })
                .sorted((a, b) -> a.getAccountNumber().compareTo(b.getAccountNumber()))
                .collect(Collectors.toList());
    }
}