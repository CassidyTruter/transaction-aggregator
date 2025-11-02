package com.fintech.controller;

import com.fintech.dto.AccountSummaryDTO;
import com.fintech.dto.AggregationResultDTO;
import com.fintech.dto.TransactionDTO;
import com.fintech.dto.TransactionSummaryDTO;
import com.fintech.entity.Transaction;
import com.fintech.enums.Category;
import com.fintech.enums.SourceType;
import com.fintech.exception.TransactionNotFoundException;
import com.fintech.repository.TransactionRepository;
import com.fintech.service.TransactionAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Management", description = "APIs for managing financial transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionRepository transactionRepository;
    private final TransactionAggregationService aggregationService;

    public TransactionController(
            TransactionRepository transactionRepository,
            TransactionAggregationService aggregationService) {
        this.transactionRepository = transactionRepository;
        this.aggregationService = aggregationService;
    }

    @GetMapping
    @Operation(summary = "Get all transactions (Providing a date range is strongly recommended)",
            description = "Retrieve all transactions with optional (but recommended) filtering and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(
            @Parameter(description = "Filter by category")
            @RequestParam(required = false) Category category,

            @Parameter(description = "Filter by source type")
            @RequestParam(required = false) SourceType sourceType,

            @Parameter(description = "Filter by account number")
            @RequestParam(required = false) String accountNumber,

            @Parameter(description = "Start date for filtering (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "End date for filtering (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "transactionDate") String sortBy,

            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String sortDir) {

        logger.info("Fetching transactions with filters - category: {}, sourceType: {}, accountNumber: {}, startDate: {}, endDate: {}",
                category, sourceType, accountNumber, startDate, endDate);

        // Validate date range
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        List<Transaction> filteredTransactions;

        if (category != null || sourceType != null || accountNumber != null || startDate != null || endDate != null) {
            filteredTransactions = transactionRepository.findByFilters(
                    category, sourceType, accountNumber, startDate, endDate);
        } else {
            filteredTransactions = transactionRepository.findAll(sort);
        }

        // Convert to DTOs
        List<TransactionDTO> dtoList = filteredTransactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());
        List<TransactionDTO> pageContent = dtoList.subList(start, end);

        Page<TransactionDTO> transactionPage = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, dtoList.size());

        return ResponseEntity.ok(transactionPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID",
            description = "Retrieve a single transaction by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction found"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<TransactionDTO> getTransactionById(
            @Parameter(description = "Transaction ID")
            @PathVariable Long id) {
        logger.info("Fetching transaction with id: {}", id);

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        return ResponseEntity.ok(convertToDTO(transaction));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get summary by category",
            description = "Get transaction totals grouped by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    })
    public ResponseEntity<List<TransactionSummaryDTO>> getSummaryByCategory() {
        logger.info("Generating transaction summary by category");
        List<TransactionSummaryDTO> summary = aggregationService.getSummaryByCategory();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/by-account")
    @Operation(summary = "Get summary by account",
            description = "Get transaction totals grouped by account number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    })
    public ResponseEntity<List<AccountSummaryDTO>> getSummaryByAccount() {
        logger.info("Generating transaction summary by account");
        List<AccountSummaryDTO> summary = aggregationService.getSummaryByAccount();
        return ResponseEntity.ok(summary);
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getTransactionId(),
                transaction.getClientId(),
                transaction.getAccountNumber(),
                transaction.getSourceType(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getCategory(),
                transaction.getSubcategory(),
                transaction.getMerchantName(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}