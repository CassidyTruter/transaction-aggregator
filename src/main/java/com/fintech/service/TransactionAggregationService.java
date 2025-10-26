package com.fintech.service;

import com.fintech.dto.AccountSummaryDTO;
import com.fintech.dto.AggregationResultDTO;
import com.fintech.dto.RawTransactionDTO;
import com.fintech.dto.TransactionSummaryDTO;
import com.fintech.entity.Transaction;
import com.fintech.enums.Category;
import com.fintech.repository.TransactionRepository;
import com.fintech.util.DataSources;
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

    private final DataSources dataSources;
    private final TransactionCategorizationService categorizationService;
    private final TransactionRepository transactionRepository;

    public TransactionAggregationService(
            DataSources dataSources,
            TransactionCategorizationService categorizationService,
            TransactionRepository transactionRepository) {
        this.dataSources = dataSources;
        this.categorizationService = categorizationService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public AggregationResultDTO aggregateAllTransactions() {
        logger.info("Starting transaction aggregation from all data sources");
        long startTime = System.currentTimeMillis();

        Map<String, Integer> sourceTypeCounts = new HashMap<>();
        List<Transaction> allTransactions = new ArrayList<>();

        // Process EFT transactions
        List<RawTransactionDTO> eftTransactions = dataSources.getEftTransactions();
        logger.info("Processing {} EFT transactions", eftTransactions.size());
        List<Transaction> categorizedEft = eftTransactions.stream()
                .map(categorizationService::categorize)
                .collect(Collectors.toList());
        allTransactions.addAll(categorizedEft);
        sourceTypeCounts.put("EFT", eftTransactions.size());

        // Process Bank Fee transactions
        List<RawTransactionDTO> bankFees = dataSources.getBankFees();
        logger.info("Processing {} Bank Fee transactions", bankFees.size());
        List<Transaction> categorizedFees = bankFees.stream()
                .map(categorizationService::categorize)
                .collect(Collectors.toList());
        allTransactions.addAll(categorizedFees);
        sourceTypeCounts.put("BANK_FEE", bankFees.size());

        // Process Debit Order transactions
        List<RawTransactionDTO> debitOrders = dataSources.getDebitOrders();
        logger.info("Processing {} Debit Order transactions", debitOrders.size());
        List<Transaction> categorizedDebitOrders = debitOrders.stream()
                .map(categorizationService::categorize)
                .collect(Collectors.toList());
        allTransactions.addAll(categorizedDebitOrders);
        sourceTypeCounts.put("DEBIT_ORDER", debitOrders.size());

        // Process Card transactions
        List<RawTransactionDTO> cardTransactions = dataSources.getCardTransactions();
        logger.info("Processing {} Card transactions", cardTransactions.size());
        List<Transaction> categorizedCards = cardTransactions.stream()
                .map(categorizationService::categorize)
                .collect(Collectors.toList());
        allTransactions.addAll(categorizedCards);
        sourceTypeCounts.put("CARD", cardTransactions.size());

        // Save all transactions to database
        logger.info("Saving {} total transactions to database", allTransactions.size());
        transactionRepository.saveAll(allTransactions);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Transaction aggregation completed in {}ms. Total processed: {}",
                duration, allTransactions.size());

        return new AggregationResultDTO(
                allTransactions.size(),
                sourceTypeCounts,
                "Successfully aggregated " + allTransactions.size() + " transactions"
        );
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